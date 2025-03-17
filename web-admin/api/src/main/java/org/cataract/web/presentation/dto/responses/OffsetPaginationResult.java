package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import lombok.Setter;
import org.cataract.web.presentation.dto.ResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class OffsetPaginationResult<T> implements ResponseDto {

    List<T> data;
    OffsetPagination pagination;

    public OffsetPaginationResult(Page<T> pageResult) {
        this.data = pageResult.getContent();
        this.pagination = new OffsetPagination(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements());
    }
}
