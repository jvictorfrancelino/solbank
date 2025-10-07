package br.com.solbank.ports.out;

import br.com.solbank.domain.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository {
    UUID salvar (Cliente cliente);
    Optional<Cliente> buscarPorCpfCnpj(String cpfCnpjDigits);
    Optional<Cliente> buscarPorId(UUID id);

    List<Cliente> pesquisar (
            UUID id,
            String nomeLike,        // já com %...%
            String cpfCnpjDigits,
            String emailLike,       // já com %...%
            String telefoneDigits,
            int limit,
            int offset
    );

    int atualizarParcial(UUID id, String nome, String cpfCnpjDigits, String email, String telefone);

    int deletarPorId(UUID id);

    List<Cliente> buscarPorIds(List<UUID> ids);

    List<Cliente> buscarPorIdsArray(List<UUID> ids);
}
