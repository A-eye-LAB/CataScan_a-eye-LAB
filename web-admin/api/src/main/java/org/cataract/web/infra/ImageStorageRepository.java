package org.cataract.web.infra;

import org.cataract.web.domain.ImageStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageStorageRepository extends JpaRepository<ImageStorage, Short> {

    Optional<ImageStorage> findByBucketName(String bucketName);
}
