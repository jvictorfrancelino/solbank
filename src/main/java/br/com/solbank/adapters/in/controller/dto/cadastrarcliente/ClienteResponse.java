package br.com.solbank.adapters.in.controller.dto.cadastrarcliente;

import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String nome,
        String cpfCnpj,
        String email,
        String telefone
) {}
