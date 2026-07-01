package com.example.neopractice.models.mappers;

import com.example.neopractice.models.dtos.responses.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageMapper {
    public <T, R> PageResponse<R> toPageResponse(Page<T> page, List<R> content, int pageNumber, int pageSize) {
        return PageResponse.<R>builder()
                .content(content)
                .page(pageNumber)
                .size(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    public <R> PageResponse<R> emptyPageResponse(int page, int size) {
        return PageResponse.<R>builder()
                .content(List.of())
                .page(page)
                .size(size)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .build();
    }
}
