package br.com.solbank.adapters.in.controller.dto.atualizarcliente;

import br.com.solbank.adapters.in.web.validation.CPFOrCNPJOrNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteUpdateRequest(
        @Size(min = 2, max = 120, message = "nome deve ter entre 2 e 120 letras")
        String nome,

        @CPFOrCNPJOrNull
        String cpfCnpj,

        @Email(message = "email inválido")
        @Size(max = 120)
        String email,

        @Pattern(regexp = "^\\d{10,15}$", message = "telefone deve ter entre 10 a 15 dígitos")
        String telefone
) {}
