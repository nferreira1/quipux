package org.quipux.dtos;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado paginado.")
public class PageResponse<T> {

    @Schema(description = "Registros da página atual.")
    public List<T> data;

    @Schema(description = "Página atual, começando em 1.", example = "1")
    public int page;

    @Schema(description = "Quantidade de registros por página.", example = "50")
    public int size;

    @Schema(description = "Total de registros existentes.", example = "137")
    public long totalElements;

    @Schema(description = "Total de páginas disponíveis.", example = "3")
    public int totalPages;

    @Schema(description = "Indica se existe uma próxima página.", example = "true")
    public boolean hasNext;

    public PageResponse(List<T> data, int page, int size, long totalElements) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        this.hasNext = page < this.totalPages;
    }
}