package org.quipux.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.quipux.dtos.ErrorResponse;

@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    @Override
    public Response toResponse(JsonProcessingException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Corpo da requisição não é um JSON válido."))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
