package org.cataract.web.presentation.dto.responses;

import lombok.Getter;

@Getter
public class OffsetPagination {

    int currentPage;
    int pageSize;
    int totalPages;
    long totalElements;

    public OffsetPagination(int currentPage, int size, int totalPages, long totalElements) {

        this.currentPage = currentPage;
        this.pageSize = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;

    }
}
