package br.com.solbank.domain.usecase;

import br.com.solbank.common.logging.LogExec;
import br.com.solbank.domain.model.Cliente;
import br.com.solbank.ports.in.BuscarClientesBatchUseCase;
import br.com.solbank.ports.out.ClienteRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

@Service
public class BuscarClientesBatchUseCaseImpl implements BuscarClientesBatchUseCase {

    private final ClienteRepository repo;
    private final Executor virtualExecutor;
    private final Executor fixedExecutor;

    public BuscarClientesBatchUseCaseImpl(
            ClienteRepository repo,
            @Qualifier("virtualExecutor") Executor virtualExecutor,
            @Qualifier("fixedExecutor") Executor fixedExecutor
    ){
        this.repo = repo;
        this.virtualExecutor = virtualExecutor;
        this.fixedExecutor = fixedExecutor;
    }

    @Override
    @LogExec("buscar-clientes-batch")
    public Resultado executar(List<UUID> ids, Mode mode){
        Objects.requireNonNull(mode, "mode");
        List<UUID> idsSafe = ids == null ? List.of() : ids;

        Instant t0 = Instant.now();
        List<Cliente> out;

        switch (mode) {
            case SEQ -> {
                //baseline: 1 query IN
                out = repo.buscarPorIds(idsSafe);
            }
            case VIRTUAL -> {
                out = paralelo(idsSafe, virtualExecutor);
            }
            case POOL -> {
                out = paralelo(idsSafe, fixedExecutor);
            }
            default -> throw new IllegalArgumentException("Modo não suportado: " + mode);
        }

        long elapsed = Duration.between(t0, Instant.now()).toMillis();
        return new Resultado(out, elapsed, mode);

    }

    private List<Cliente> paralelo(List<UUID> ids, Executor exec) {
        if (ids.isEmpty()) return List.of();
        int maxParallel = Math.min(32, ids.size());
        var sem = new Semaphore(maxParallel);
        var futures = new ArrayList<CompletableFuture<Optional<Cliente>>>(ids.size());
        for (UUID id : ids) {
            sem.acquireUninterruptibly();
            futures.add(CompletableFuture
                    .supplyAsync(() -> repo.buscarPorId(id), exec)
                    .whenComplete((r, t ) -> sem.release()));
        }
        // aguarda todas
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        var out = new ArrayList<Cliente>();
        for (var f : futures) f.join().ifPresent(out::add);
        return out;
    }

}
