package org.quipux.dtos;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.quipux.entities.Person;

@Schema(description = "Dados de uma pessoa cadastrada.")
public class PersonResponse {

    @Schema(description = "Identificador interno.", example = "1")
    public Long id;

    @Schema(description = "CPF com 11 dígitos.", example = "11144477735")
    public String cpf;

    @Schema(description = "Primeiro nome.", example = "Nathan")
    public String firstName;

    @Schema(description = "Sobrenome.", example = "Ferreira")
    public String lastName;

    @Schema(description = "Endereço de e-mail.", example = "nathan.ferreira@exemplo.com")
    public String email;

    public static PersonResponse from(Person person) {
        PersonResponse response = new PersonResponse();
        response.id = person.personId;
        response.cpf = person.cpf;
        response.firstName = person.firstName;
        response.lastName = person.lastName;
        response.email = person.email;
        return response;
    }
}