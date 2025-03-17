package org.cataract.web.presentation.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public abstract class ListRequestDto {
    @Transient
    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(1900, 1, 1);
    @Transient
    private static final LocalDate DEFAULT_END_DATE = LocalDate.now().plusDays(1);
    @Transient
    private static final int DEFAULT_PAGE = 0;
    @Transient
    private static final int DEFAULT_PAGE_SIZE = 10;
    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PastOrPresent(message = "Date must be today or in the past")
    @JsonSerialize(using= LocalDateSerializer.class)
    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate startDate = DEFAULT_START_DATE;

    @JsonSerialize(using= LocalDateSerializer.class)
    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate endDate = DEFAULT_END_DATE;
    int page = DEFAULT_PAGE;
    int size = DEFAULT_PAGE_SIZE;

    @Nullable
    String query;

    @Override
    public String toString() {

        try {
            JavaTimeModule javaTimeModule=new JavaTimeModule();
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE_TIME));
            objectMapper.registerModule(javaTimeModule);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }

    }
}
