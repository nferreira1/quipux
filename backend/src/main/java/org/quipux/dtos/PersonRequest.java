package org.quipux.dtos;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro de uma pessoa.")
public class PersonRequest {

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos numéricos, sem pontos ou traços.")
    @CPF(message = "O CPF está em um formato inválido.")
    @Schema(description = "CPF com 11 dígitos, sem pontuação.", example = "11144477735")
    public String cpf;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter no mínimo 2 e no máximo 100 caracteres.")
    @Schema(description = "Primeiro nome.", example = "Nathan")
    public String firstName;

    @NotBlank(message = "O sobrenome é obrigatório.")
    @Size(min = 2, max = 100, message = "O sobrenome deve ter no mínimo 2 e no máximo 100 caracteres.")
    @Schema(description = "Sobrenome.", example = "Ferreira")
    public String lastName;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail está em um formato inválido.")
    @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
    @Schema(description = "Endereço de e-mail.", example = "nathan.ferreira@exemplo.com")
    public String email;
}