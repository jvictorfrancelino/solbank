package br.com.solbank.adapters.in.controller.dto.cadastrarcliente;

import br.com.solbank.adapters.in.web.validation.CPFOrCNPJ;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest (
        @NotBlank
        @Size(min = 2, max = 120)
        String nome,

        @NotBlank
        @CPFOrCNPJ // Anotação customizada
        String cpfCnpj,

        @Email
        @Size(max = 120)
        String email,

        // Aceita só dígitos, com 10 a 15 (ex.: DDI+DDD+numero)
        @Pattern(regexp = "^\\d{10,15}$", message = "telefone deve conter apenas dígitos (10 a 15)")
        String telefone
){}