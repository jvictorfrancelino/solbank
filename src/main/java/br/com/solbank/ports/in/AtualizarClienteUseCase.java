package br.com.solbank.ports.in;

import java.util.UUID;

public interface AtualizarClienteUseCase {
    //TODO entender com o chatGPT o que é o comando
    void executar (Comando comando);

    record Comando(
            UUID id,
            String nome,
            String cpfCnpj,     // se informado, será normalizado p/ dígitos e checado unicidade
            String email,
            String telefone     // se informado, só dígitos
    ){}
}
