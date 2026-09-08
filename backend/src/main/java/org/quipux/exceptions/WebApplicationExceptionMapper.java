package org.quipux.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.quipux.dtos.ErrorResponse;

import java.util.Map;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Map<Integer, String> DEFAULT_MESSAGES = Map.of(
            400, "Requisição inválida.",
            401, "Chave de API ausente ou inválida.",
            403, "Acesso negado.",
            404, "Recurso não encontrado.",
            405, "Método HTTP não suportado para este recurso.",
            406, "Formato de resposta não suportado.",
            415, "Formato de conteúdo não suportado. Envie JSON.",
            500, "Erro interno ao processar a requisição.",
            503, "Serviço temporariamente indisponível.");

    @Override
    public Response toResponse(WebApplicationException exception) {
        int status = exception.getResponse().getStatus();

        String message = exception instanceof ApiException
                ? exception.getMessage()
                : DEFAULT_MESSAGES.getOrDefault(status, "Erro ao processar a requisição.");

        return Response.status(status)
                .entity(new ErrorResponse(message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
