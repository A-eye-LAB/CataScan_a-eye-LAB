package org.cataract.web.presentation.dto.responses;

import lombok.Getter;
import org.cataract.web.domain.User;
import org.cataract.web.presentation.dto.ResponseDto;

import java.time.LocalDateTime;

@Getter
public class UserResponseDto implements ResponseDto {

    private Long id;
    private String username;
    private String email;

    private String role;
    private String institutionName;
    private String bucketName;
    private String bucketRegion;
    private String createdDate;
    private String updatedDate;


    public static UserResponseDto toDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.id = user.getUserId();
        userResponseDto.username = user.getUsername();
        userResponseDto.email = user.getEmail();
        userResponseDto.role = user.getRole().toString();
        userResponseDto.institutionName = user.getInstitution().getName();
        userResponseDto.bucketName = user.getInstitution().getImageStorage().getBucketName();
        userResponseDto.bucketRegion = user.getInstitution().getImageStorage().getBucketRegion();
        userResponseDto.createdDate = LocalDateTime.now().toString();
        userResponseDto.updatedDate = LocalDateTime.now().toString();
        if (user.getCreatedAt() != null) {
            userResponseDto.createdDate = user.getCreatedAt().toString();
            userResponseDto.updatedDate = user.getUpdatedAt().toString();
        }
        return userResponseDto;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
