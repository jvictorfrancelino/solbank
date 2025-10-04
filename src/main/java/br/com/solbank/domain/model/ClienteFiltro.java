package br.com.solbank.domain.model;

import java.util.UUID;

public record ClienteFiltro(
        UUID id,
        String nome,        // buscar com ILIKE "%nome%"
        String cpfCnpj,     // só dígitos
        String email,       // ILIKE "%email%"
        String telefone     // só dígitos
) {
}
