package org.cataract.web.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cataract.web.presentation.dto.requests.ImageStorageCreateRequestDto;

import java.util.List;

@Entity
@Table(name="image_storage")
@Getter
@NoArgsConstructor
public class ImageStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    short imageStorageId;

    private String bucketName;

    private String bucketRegion;

    private String bucketEndpoint;

    @OneToMany(mappedBy = "imageStorage", cascade = CascadeType.ALL)
    private List<Institution> institutions;

    public ImageStorage(ImageStorageCreateRequestDto imageStorageCreateRequestDto) {
        this.bucketName = imageStorageCreateRequestDto.bucketName();
        this.bucketRegion = imageStorageCreateRequestDto.bucketRegion();

    }
}
