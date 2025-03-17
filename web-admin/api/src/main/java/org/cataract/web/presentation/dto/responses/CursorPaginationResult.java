package org.cataract.web.presentation.dto.responses;

import java.util.List;

public class CursorPaginationResult<T> {
    private final List<T> data;
    private final Long currentCursor;
    private final Long nextCursor;
    private final boolean hasNextPage;

    public CursorPaginationResult(List<T> data, Long currentCursor, Long nextCursor, boolean hasNextPage) {
        this.data = data;
        this.currentCursor = currentCursor;
        this.nextCursor = nextCursor;
        this.hasNextPage = hasNextPage;
    }
}