package org.cataract.web.application.service;

import org.cataract.web.domain.Institution;
import org.cataract.web.presentation.dto.requests.CreateUserRequestDto;
import org.cataract.web.presentation.dto.requests.UpdateUserRequestDto;
import org.cataract.web.presentation.dto.requests.UserListRequestDto;
import org.cataract.web.presentation.dto.responses.ErrorResponseDto;
import org.cataract.web.presentation.dto.responses.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDto createUser(CreateUserRequestDto createUserRequestDto);

    Object getUserList(UserListRequestDto userListRequestDto, Pageable pageable);

    UserResponseDto getUser(Long id);

    UserResponseDto updateUser(Long id, UpdateUserRequestDto updateUserRequestDto);

    ErrorResponseDto deleteUser(Long id);

    UserResponseDto getUserByUsername(String username);

    ErrorResponseDto updateSelfUser(String username, UpdateUserRequestDto updateUserRequestDto);

    Institution getInstitution(String username);

}
