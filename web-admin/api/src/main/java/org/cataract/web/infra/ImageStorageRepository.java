package org.cataract.web.infra;

import org.cataract.web.domain.ImageStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageStorageRepository extends JpaRepository<ImageStorage, Short> {

    @Query(value = "SELECT * from image_storage where bucket_name like '%:bucketName%'", nativeQuery = true)
    Optional<ImageStorage> findByBucketName(@Param("bucketName") String bucketName);
}
