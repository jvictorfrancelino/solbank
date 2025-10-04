package br.com.solbank.domain.usecase;

import br.com.solbank.common.logging.LogExec;
import br.com.solbank.domain.model.Cliente;
import br.com.solbank.domain.model.ClienteFiltro;
import br.com.solbank.ports.in.BuscarClientesUseCase;
import br.com.solbank.ports.out.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscarClientesUseCaseImpl implements BuscarClientesUseCase {

    private final ClienteRepository repo;

    public BuscarClientesUseCaseImpl(ClienteRepository repo){
        this.repo = repo;
    }

    @Override
    @LogExec("buscar-clientes")
    public List<Cliente> executar (ClienteFiltro filtro, int limit, int offset) {
        // Normalizações
        var id = filtro.id();
        var nomeLike = isBlank(filtro.nome()) ? null : "%" + filtro.nome().trim() + "%";
        var emailLike = isBlank(filtro.email()) ? null : "%" + filtro.email().trim() + "%";
        var cpf = digitsOrNull(filtro.cpfCnpj());
        var tel = digitsOrNull(filtro.telefone());

        // Proteção: limites razoáveis
        int safeLimit = (limit <= 0 || limit > 200) ? 50 : limit;
        int safeOffset = Math.max(0, offset);

        return repo.pesquisar(id, nomeLike, cpf, emailLike, tel, safeLimit, safeOffset);
    }

    private static String digitsOrNull(String s){
        if (s == null) return null;
        String d = s.replace("\\D", "");
        return d.isEmpty() ? null : d;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
