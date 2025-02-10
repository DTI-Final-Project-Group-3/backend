package com.warehub.warehub.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationInfo<T> {
    private int currentPage;
    private int totalPages;
    private boolean hasPrev;
    private boolean hasNext;
    private List<T> content;

    public PaginationInfo(Page<?> page, List<T> content){
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.hasPrev = page.hasPrevious();
        this.hasNext = page.hasNext();
        this.content = content;
    }
}
