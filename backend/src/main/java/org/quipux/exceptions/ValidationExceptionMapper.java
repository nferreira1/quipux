package org.quipux.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path.Node;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.quipux.dtos.ErrorResponse;

import java.util.Comparator;
import java.util.List;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<ErrorResponse.FieldError> errors = exception.getConstraintViolations().stream()
                .map(violation -> new ErrorResponse.FieldError(
                        fieldNameOf(violation), violation.getMessage()))
                .sorted(Comparator.comparing(error -> error.field))
                .toList();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Erro de validação nos dados enviados.", errors))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String fieldNameOf(ConstraintViolation<?> violation) {
        String fieldName = null;

        for (Node node : violation.getPropertyPath()) {
            fieldName = node.getName();
        }

        return fieldName != null ? fieldName : "desconhecido";
    }
}
