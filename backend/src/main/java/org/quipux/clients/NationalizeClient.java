package org.quipux.clients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.quipux.dtos.NationalizeResponse;

@RegisterRestClient(configKey = "nationalize-api")
@Produces(MediaType.APPLICATION_JSON)
public interface NationalizeClient {

    @GET
    @Path("/")
    NationalizeResponse findNationalityByName(@QueryParam("name") String name);
}