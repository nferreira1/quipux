package org.quipux.dtos;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Formato padrão de retorno em caso de erro.")
public class ErrorResponse {

    @Schema(description = "Mensagem descrevendo o erro.", example = "Erro de validação nos dados enviados.")
    public String message;

    @Schema(description = "Erros por campo. Lista vazia quando a falha não é de validação.")
    public List<FieldError> errors = new ArrayList<>();

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, List<FieldError> errors) {
        this.message = message;
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    @Schema(description = "Erro de validação associado a um campo específico.")
    public static class FieldError {

        @Schema(description = "Nome do campo que falhou na validação.", example = "email")
        public String field;

        @Schema(description = "Motivo da falha.", example = "O e-mail está em um formato inválido.")
        public String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
