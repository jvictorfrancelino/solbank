package br.com.solbank.ports.in;

import br.com.solbank.domain.model.Cliente;
import br.com.solbank.domain.model.ClienteFiltro;

import java.util.List;

public interface BuscarClientesUseCase {
    List<Cliente> executar(ClienteFiltro filtro, int limit, int offset);
}
