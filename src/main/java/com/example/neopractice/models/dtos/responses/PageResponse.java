package com.example.neopractice.models.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;          // Содержимое страницы (список задач)
    private int page;                 // Номер текущей страницы (начинается с 1)
    private int size;                 // Размер страницы (количество элементов)
    private long totalElements;       // Общее количество элементов во всех страницах
    private int totalPages;           // Общее количество страниц
    private boolean first;            // Является ли текущая страница первой
    private boolean last;
}
