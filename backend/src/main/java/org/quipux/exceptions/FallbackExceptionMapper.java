package org.quipux.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.quipux.dtos.ErrorResponse;

@Provider
public class FallbackExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(FallbackExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        LOG.error("Erro não tratado ao processar a requisição.", exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Erro interno ao processar a requisição."))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
