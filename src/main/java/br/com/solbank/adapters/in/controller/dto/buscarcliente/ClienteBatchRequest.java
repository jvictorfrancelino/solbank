package br.com.solbank.adapters.in.controller.dto.buscarcliente;

import br.com.solbank.ports.in.BuscarClientesBatchUseCase;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ClienteBatchRequest(
        @NotEmpty(message = "ids cannot be empty")
        List<UUID> ids,
        BuscarClientesBatchUseCase.Mode mode

) {
}
