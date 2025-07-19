package org.cataract.web.presentation.controller.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.UserService;
import org.cataract.web.domain.exception.UserAlreadyExistsException;
import org.cataract.web.domain.exception.UserNotFoundException;
import org.cataract.web.presentation.dto.ResponseDto;
import org.cataract.web.presentation.dto.requests.CreateUserRequestDto;
import org.cataract.web.presentation.dto.requests.UpdateUserRequestDto;
import org.cataract.web.presentation.dto.requests.UserListRequestDto;
import org.cataract.web.presentation.dto.responses.ErrorResponseDto;
import org.cataract.web.presentation.dto.responses.OffsetPaginationResult;
import org.cataract.web.presentation.dto.responses.UserResponseDto;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<ResponseDto> createUser(@RequestBody @Valid CreateUserRequestDto createUserRequestDto) {
        log.info("ADMIN Received request to create user: {}", createUserRequestDto.getUsername());
        try {
            UserResponseDto userResponseDto = userService.createUser(createUserRequestDto);
            log.info("ADMIN User created successfully: {}", createUserRequestDto.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
        } catch (UserAlreadyExistsException ex) {
            log.error("ADMIN {} username already exists", createUserRequestDto.getUsername(), ex);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("ADMIN Error occurred while creating user: {}", createUserRequestDto.getUsername(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(ex));
        }
    }


    @GetMapping("/user/{id}")
    public ResponseEntity<ResponseDto> getUser(@PathVariable("id") Long id) {
        log.info("ADMIN Received request to get user id: {}", id);
        try {
            UserResponseDto userResponseDto = userService.getUser(id);
            log.info("ADMIN Username: {} info retrieved successfully", userResponseDto.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
        } catch (UserNotFoundException ex) {
            log.error("ADMIN userId: {} not found or retrieved", id, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("ADMIN userId: {} not retrieved for internal server reason", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(ex));
        }
    }

    @PatchMapping("/user/{id}")
    public ResponseEntity<ResponseDto> updateUser(@PathVariable("id") Long id,
                                                  @RequestBody @Valid UpdateUserRequestDto updateUserRequestDto) {
        log.info("ADMIN Received request to update user info by id: {}", id);
        try {
            UserResponseDto userResponseDto = userService.updateUser(id, updateUserRequestDto);
            log.info("ADMIN username: {} updated successfully", userResponseDto.getUsername());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(userResponseDto);
        } catch (UserNotFoundException ex) {
            log.error("ADMIN userId: {} not found or updated", id, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(ex));
        } catch (UserAlreadyExistsException ex) {
            log.error("ADMIN userId: {} already exists", id, ex);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("ADMIN userId: {} update failed", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(ex));
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<ResponseDto> deleteUser(@PathVariable("id") Long id) {
        log.info("ADMIN Received request to delete user by id: {}", id);
        try {
            ErrorResponseDto errorResponseDto = userService.deleteUser(id);
            log.info("ADMIN {}", errorResponseDto);
            return ResponseEntity.ok(errorResponseDto);
        } catch (UserNotFoundException ex) {
            log.error("ADMIN userId: {} deletion failed as user does not exist", id, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(ex));
        } catch (Exception ex) {
            log.error("ADMIN userId: {} deletion failed", id, ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUserList(
            @Size(max = 30, message = "query must be smaller than 30 characters")
            @RequestParam(value = "q", required = false) String query,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) LocalDate startDate,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer page,
            @Range(min = 1, max = 1000, message = "page size needs to be between 1 and 1000")
            @RequestParam(required = false) Integer size,
            @Pattern(
                    regexp = "^(username|createdAt|updatedAt)$",
                    message = "only accepts 'username', 'createdAt', or 'updatedAt' as 'sortBy'"
            )
            @RequestParam(required = false) String sortBy,
            @Pattern(
                    regexp = "^(asc|desc)$",
                    message = "only accepts 'asc' or 'desc' as 'sortDir'"
            )
            @RequestParam(required = false) String sortDir,
            Authentication authentication) {
        String username = authentication.getName();
        try {
            UserListRequestDto userListRequestDto =
                    new UserListRequestDto(query, startDate, endDate, page, size, sortBy, sortDir);
            log.info("ADMIN [{}] Received request to get user list: {} with the following query {}", username, userListRequestDto, query);
            Pageable pageable = (page != null && size != null) ? Pageable.ofSize(size).withPage(page) : Pageable.unpaged();
            var userResponseDtoList = userService.getUserList(userListRequestDto, pageable);
            log.info("ADMIN retrieved user list successfully");
            if (pageable.isPaged())
                return ResponseEntity.ok(new OffsetPaginationResult<>((Page<UserResponseDto>) userResponseDtoList));
            return ResponseEntity.ok(userResponseDtoList);

        } catch (ValidationException e) {
            log.error("ADMIN failed to retrieve user list because of validation", e);
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e));
        } catch (Exception ex) {
            log.error("ADMIN failed to retrieve user list", ex);
            return ResponseEntity.internalServerError().body(new ErrorResponseDto(ex));

        }
    }
}
