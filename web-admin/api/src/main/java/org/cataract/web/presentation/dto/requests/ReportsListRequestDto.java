package org.cataract.web.presentation.dto.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.cataract.web.domain.AiResult;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;

@Getter
public class ReportsListRequestDto extends ListRequestDto {

    @Transient
    private static final String DEFAULT_SORT_BY = "scanDate";

    @Transient
    private static final String DEFAULT_SORT_DIRECTION = "desc";

    @Pattern(
            regexp = "^(name|scanDate|updatedAt)$",
            message = "only accepts 'name','scanDate' or 'updatedAt' as 'sortBy'"
    )
    String sortBy = DEFAULT_SORT_BY;
    @Pattern(
            regexp = "^(asc|desc)$",
            message = "only accepts 'asc' or 'desc' as 'sortDir'"
    )
    String sortDir = DEFAULT_SORT_DIRECTION;

    @Nullable
    String sex;

    @Nullable
    AiResult status;

    int linkStatus = -1;

    public ReportsListRequestDto(String query, String startDate, String endDate, Integer page, Integer size, String sortBy,
                                 String sortDir, String sex, AiResult status, Integer linkStatus) {
        super();
        if (query !=null)
            this.query = query;
        if (startDate !=null)
            this.startDate = LocalDate.parse(startDate);
        if (endDate !=null)
            this.endDate = LocalDate.parse(endDate);
        if (page !=null)
            this.page = page;
        if (size !=null)
            this.size = size;
        if (sortBy !=null)
            this.sortBy = sortBy;
        if (sortDir !=null)
            this.sortDir = sortDir;
        if (sex !=null)
            this.sex = sex;
        if (status !=null)
            this.status = status;
        if (linkStatus !=null)
            this.linkStatus = linkStatus;
    }

    public ReportsListRequestDto(String query, String startDate, String endDate, String sortBy, String sortDir, String sex, AiResult status, Integer linkStatus) {
        if (query !=null)
            this.query = query;
        if (startDate !=null)
            this.startDate = LocalDate.parse(startDate);
        if (endDate !=null)
            this.endDate = LocalDate.parse(endDate);
        if (sortBy !=null)
            this.sortBy = sortBy;
        if (sortDir !=null)
            this.sortDir = sortDir;
        if (sex !=null)
            this.sex = sex;
        if (status !=null)
            this.status = status;
        if (linkStatus !=null)
            this.linkStatus = linkStatus;
    }

    public ReportsListRequestDto() {

    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
