package br.com.dompagamentos.infrastructure.adapters.input.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MerchantRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 200)
        String name,

        @NotBlank(message = "CPF/CNPJ é obrigatório")
        @Size(min = 11, max = 18)
        String document,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Size(max = 20)
        String phone,

        @Pattern(regexp = "https?://.+", message = "callbackUrl deve ser uma URL válida (http ou https)")
        @Size(max = 500)
        String callbackUrl
) {}
