package br.com.solbank.ports.in;

import java.util.UUID;

public interface CadastrarClienteUseCase {
    UUID executar(Comando comando);

    record Comando(String nome, String cpfCnpj, String email, String telefone){}
}
