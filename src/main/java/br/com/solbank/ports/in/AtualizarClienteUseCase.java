package br.com.solbank.ports.in;

import java.util.UUID;

public interface AtualizarClienteUseCase {
    void executar (Comando comando);

    record Comando(
            UUID id,
            String nome,
            String cpfCnpj,     // se informado, será normalizado p/ dígitos e checado unicidade
            String email,
            String telefone     // se informado, só dígitos
    ){}
}
