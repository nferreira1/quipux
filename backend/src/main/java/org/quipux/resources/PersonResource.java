package org.quipux.resources;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.quipux.dtos.ErrorResponse;
import org.quipux.dtos.PageResponse;
import org.quipux.dtos.PersonRequest;
import org.quipux.dtos.PersonResponse;
import org.quipux.entities.Person;
import org.quipux.services.PersonService;

import java.util.List;
import java.util.Map;

@Path("/")
@Tag(name = "Pessoas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {

    private static final String CPF_REGEX = "\\d{11}";
    private static final String CPF_MESSAGE = "O CPF deve conter 11 dígitos numéricos, sem pontos ou traços.";
    private static final String CPF_PARAM_DESCRIPTION = "CPF com 11 dígitos, sem pontuação.";
    private static final String CPF_EXAMPLE = "11144477735";

    @Inject
    PersonService personService;

    @POST
    @Path("/registrarName")
    @Secured
    @SecurityRequirement(name = "apiKey")
    @Operation(summary = "Cadastra uma pessoa", description = "Registra uma nova pessoa no sistema. Exige chave de API.")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Pessoa cadastrada com sucesso.", content = @Content(schema = @Schema(implementation = PersonResponse.class))),
            @APIResponse(responseCode = "400", description = "Dados inválidos ou CPF em formato incorreto.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "401", description = "Chave de API ausente ou inválida.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "409", description = "Já existe pessoa com o mesmo CPF ou e-mail.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response register(@Valid PersonRequest request) {
        Person created = personService.register(request);
        return Response.status(Response.Status.CREATED)
                .entity(PersonResponse.from(created))
                .build();
    }

    @GET
    @Path("/list")
    @Operation(summary = "Lista as pessoas cadastradas", description = "Retorna as pessoas de forma paginada. Use os parâmetros page e size para navegar.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Página retornada com sucesso.", content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @APIResponse(responseCode = "400", description = "Parâmetros de paginação inválidos.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PageResponse<PersonResponse> listAll(
            @Parameter(description = "Número da página, começando em 1.", example = "1") @QueryParam("page") @DefaultValue("1") @Min(value = 1, message = "A página deve ser maior ou igual a 1.") int page,
            @Parameter(description = "Registros por página, entre 1 e 100.", example = "50") @QueryParam("size") @DefaultValue("50") @Min(value = 1, message = "O tamanho da página deve ser maior ou igual a 1.") @Max(value = 100, message = "O tamanho da página deve ser no máximo 100.") int size) {

        List<PersonResponse> data = personService.listAll(page, size).stream()
                .map(PersonResponse::from)
                .toList();

        return new PageResponse<>(data, page, size, personService.countAll());
    }

    @GET
    @Path("/list/{cpf}")
    @Operation(summary = "Busca uma pessoa pelo CPF", description = "Retorna os dados de uma pessoa específica a partir do CPF.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Pessoa encontrada.", content = @Content(schema = @Schema(implementation = PersonResponse.class))),
            @APIResponse(responseCode = "400", description = "CPF em formato inválido.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Nenhuma pessoa encontrada com o CPF informado.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PersonResponse findByCpf(
            @Parameter(description = CPF_PARAM_DESCRIPTION, example = CPF_EXAMPLE) @PathParam("cpf") @Pattern(regexp = CPF_REGEX, message = CPF_MESSAGE) String cpf) {
        return PersonResponse.from(personService.findByCpf(cpf));
    }

    @DELETE
    @Path("/list/{cpf}")
    @Secured
    @SecurityRequirement(name = "apiKey")
    @Operation(summary = "Exclui uma pessoa", description = "Remove do sistema a pessoa correspondente ao CPF. Exige chave de API.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Pessoa excluída com sucesso."),
            @APIResponse(responseCode = "400", description = "CPF em formato inválido.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "401", description = "Chave de API ausente ou inválida.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Nenhuma pessoa encontrada com o CPF informado.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response deleteByCpf(
            @Parameter(description = CPF_PARAM_DESCRIPTION, example = CPF_EXAMPLE) @PathParam("cpf") @Pattern(regexp = CPF_REGEX, message = CPF_MESSAGE) String cpf) {
        personService.deleteByCpf(cpf);
        return Response.noContent().build();
    }

    @GET
    @Path("/findNacionalityByPerson/{cpf}")
    @Operation(summary = "Prevê a nacionalidade de uma pessoa", description = "Consulta a API pública nationalize.io com base no nome da pessoa e retorna o nome do país mais provável.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Nacionalidade prevista com sucesso."),
            @APIResponse(responseCode = "400", description = "CPF em formato inválido.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Pessoa não encontrada ou sem previsão disponível.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "503", description = "Falha ao consultar a API externa.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response findNationalityByCpf(
            @Parameter(description = CPF_PARAM_DESCRIPTION, example = CPF_EXAMPLE) @PathParam("cpf") @Pattern(regexp = CPF_REGEX, message = CPF_MESSAGE) String cpf) {
        String nationality = personService.findNationalityByCpf(cpf);
        return Response.ok(Map.of("nationality", nationality)).build();
    }
}
