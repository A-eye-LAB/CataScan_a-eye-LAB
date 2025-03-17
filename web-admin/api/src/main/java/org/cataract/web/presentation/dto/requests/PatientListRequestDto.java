package org.cataract.web.presentation.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.cataract.web.helper.ValidPastOrPresentDate;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.util.Date;

@Getter
public class PatientListRequestDto extends ListRequestDto{

    @Transient
    private static final String DEFAULT_SORT_BY = "createdAt";
    @Transient
    private static final String DEFAULT_SORT_DIRECTION = "desc";

    @Pattern(
            regexp = "^(name|dateOfBirth|createdAt|updatedAt)$",
            message = "only accepts 'name', 'dateOfBirth', 'createdAt', or 'updatedAt' as 'sortBy'"
    )
    String sortBy = DEFAULT_SORT_BY;
    @Pattern(
            regexp = "^(asc|desc)$",
            message = "only accepts 'asc' or 'desc' as 'sortDir'"
    )
    String sortDir = DEFAULT_SORT_DIRECTION;

    @Nullable
    @Pattern(
            regexp = "^(male|female|other|all)$",
            message = "Sex must be 'male', 'female', 'other', or 'all'"
    )
    String sex;

    @Nullable
    @ValidPastOrPresentDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirthFrom; // Start date for filtering by birthdate

    @Nullable
    @ValidPastOrPresentDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirthTo; // End date for filtering by birthdate

    @Nullable
    @Range(min=0, max=1)
    private int dataStatus = 1;

    public PatientListRequestDto(String query, LocalDate startDate, LocalDate endDate, Integer page, Integer size,
                                 String sortBy, String sortDir, Integer dataStatus, String sex, Date dateOfBirthFrom, Date dateOfBirthTo) {
        super();
        if (query !=null)
            this.query = query;
        if (startDate !=null)
            this.startDate = startDate;
        if (endDate !=null)
            this.endDate = endDate;
        if (page !=null)
            this.page = page;
        if (size !=null)
            this.size = size;
        if (sortBy !=null)
            this.sortBy = sortBy;
        if (sortDir !=null)
            this.sortDir = sortDir;
        if (dataStatus !=null)
            this.dataStatus = dataStatus;
        if (sex !=null)
            this.sex = sex;
        if (dateOfBirthFrom !=null)
            this.dateOfBirthFrom = dateOfBirthFrom;
        if (dateOfBirthTo !=null)
            this.dateOfBirthTo = dateOfBirthTo;
    }

    public PatientListRequestDto(String query, LocalDate startDate, LocalDate endDate, Integer page, Integer size, String sortBy, String sortDir) {
        if (query !=null)
            this.query = query;
        if (startDate !=null)
            this.startDate = startDate;
        if (endDate !=null)
            this.endDate = endDate;
        if (page !=null)
            this.page = page;
        if (size !=null)
            this.size = size;
        if (sortBy !=null)
            this.sortBy = sortBy;
        if (sortDir !=null)
            this.sortDir = sortDir;
    }

    public PatientListRequestDto(String query, LocalDate startDate, LocalDate endDate, String sortBy, String sortDir,
                                 Integer dataStatus, String sex, Date dateOfBirthFrom, Date dateOfBirthTo) {
        if (query !=null)
            this.query = query;
        if (startDate !=null)
            this.startDate = startDate;
        if (endDate !=null)
            this.endDate = endDate;
        if (sortBy !=null)
            this.sortBy = sortBy;
        if (sortDir !=null)
            this.sortDir = sortDir;
        if (dataStatus !=null)
            this.dataStatus = dataStatus;
        if (sex !=null)
            this.sex = sex;
        if (dateOfBirthFrom !=null)
            this.dateOfBirthFrom = dateOfBirthFrom;
        if (dateOfBirthTo !=null)
            this.dateOfBirthTo = dateOfBirthTo;
    }
}
