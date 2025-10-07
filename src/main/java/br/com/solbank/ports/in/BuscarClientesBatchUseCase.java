package br.com.solbank.ports.in;

import br.com.solbank.domain.model.Cliente;

import java.util.List;
import java.util.UUID;

public interface BuscarClientesBatchUseCase {
    enum Mode { SEQ, VIRTUAL, POOL }

    record Resultado (List<Cliente> clientes, long elapsedMs, Mode mode) {}

    Resultado executar (List<UUID> ids, Mode mode);
}
