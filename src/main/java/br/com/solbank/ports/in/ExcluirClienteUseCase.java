package br.com.solbank.ports.in;

import java.util.UUID;

public interface ExcluirClienteUseCase {

    void executar(UUID id);
}
