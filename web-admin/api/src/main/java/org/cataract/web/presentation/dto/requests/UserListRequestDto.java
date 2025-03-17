package org.cataract.web.presentation.dto.requests;

import lombok.Getter;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;

@Getter
public class UserListRequestDto extends ListRequestDto {

    @Transient
    private static final String DEFAULT_SORT_BY = "username";
    @Transient
    private static final String DEFAULT_SORT_DIRECTION = "asc";
    String sortBy = DEFAULT_SORT_BY;
    String sortDir = DEFAULT_SORT_DIRECTION;

    public UserListRequestDto(String query, LocalDate startDate, LocalDate endDate, Integer page, Integer size, String sortBy, String sortDir) {
        if (query != null)
            this.query = query;
        if (startDate != null)
            this.startDate = startDate;
        if (endDate != null)
            this.endDate = endDate;
        if (page != null)
            this.page = page;
        if (size != null)
            this.size = size;
        if (sortBy != null)
            this.sortBy = sortBy;
        if (sortDir != null)
            this.sortDir = sortDir;
    }

}
