package br.com.solbank.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Cliente(
        UUID id,
        String nome,
        String cpfCnpj,
        String email,
        String telefone,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {}
