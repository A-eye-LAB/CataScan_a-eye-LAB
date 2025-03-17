package org.cataract.web.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Table(name = "institutions")
@Entity
@Getter
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer institutionId;

    private String name;

    private String address;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL)
    private List<User> users;

    public Institution() {

    }

    public Institution(String name) {
        this.name = name;
    }

}
