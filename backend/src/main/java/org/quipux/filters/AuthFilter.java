package org.quipux.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.quipux.dtos.ErrorResponse;
import org.quipux.resources.Secured;

@Provider
@Secured
public class AuthFilter implements ContainerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";

    @ConfigProperty(name = "app.api-key")
    String expectedApiKey;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String providedKey = requestContext.getHeaderString(API_KEY_HEADER);

        if (providedKey == null || !providedKey.equals(expectedApiKey)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity(new ErrorResponse("Chave de API ausente ou inválida."))
                            .type(MediaType.APPLICATION_JSON)
                            .build());
        }
    }
}
