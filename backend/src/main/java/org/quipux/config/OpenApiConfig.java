package org.quipux.config;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(info = @Info(title = "Person API - Quipux", version = "1.0.0", description = "API para cadastro de pessoas e previsão de nacionalidade."), tags = @Tag(name = "Pessoas", description = "Operações de cadastro e consulta de pessoas."))
@SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.APIKEY, apiKeyName = "X-API-Key", in = SecuritySchemeIn.HEADER, description = "Chave de API necessária para as operações de escrita.")
public class OpenApiConfig extends Application {
}