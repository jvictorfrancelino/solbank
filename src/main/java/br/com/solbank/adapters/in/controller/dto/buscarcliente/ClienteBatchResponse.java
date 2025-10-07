package br.com.solbank.adapters.in.controller.dto.buscarcliente;

import java.util.List;

public record ClienteBatchResponse(
        String mode,
        long elapsedMs,
        List<ClienteSearchResponse> clientes
) {
}
