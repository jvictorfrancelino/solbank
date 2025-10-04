package br.com.solbank.adapters.in.controller.dto.buscarcliente;

import java.util.UUID;

public record ClienteSearchResponse(
        UUID id,
        String nome,
        String cpfCnpj,
        String email,
        String telefone
) {
}
