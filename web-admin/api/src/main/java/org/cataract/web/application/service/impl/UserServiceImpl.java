package org.cataract.web.application.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.UserService;
import org.cataract.web.domain.Institution;
import org.cataract.web.domain.Role;
import org.cataract.web.domain.SessionToken;
import org.cataract.web.domain.User;
import org.cataract.web.domain.exception.UserAlreadyExistsException;
import org.cataract.web.domain.exception.UserNotFoundException;
import org.cataract.web.infra.InstitutionRepository;
import org.cataract.web.infra.SessionTokenRepository;
import org.cataract.web.infra.UserRepository;
import org.cataract.web.presentation.dto.requests.CreateUserRequestDto;
import org.cataract.web.presentation.dto.requests.UpdateUserRequestDto;
import org.cataract.web.presentation.dto.requests.UserListRequestDto;
import org.cataract.web.presentation.dto.responses.ErrorResponseDto;
import org.cataract.web.presentation.dto.responses.UserResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionTokenRepository sessionTokenRepository;

    @Value("${app.admin.username}")
    String adminUser;

    public UserServiceImpl(UserRepository userRepository,
    InstitutionRepository institutionRepository,
    PasswordEncoder passwordEncoder,
    SessionTokenRepository sessionTokenRepository) {

       this.userRepository = userRepository;
       this.institutionRepository = institutionRepository;
       this.passwordEncoder = passwordEncoder;
       this.sessionTokenRepository = sessionTokenRepository;

    }

    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto createUserRequestDto) {

        Institution userInstitution;
        Optional<Institution> institution = institutionRepository.findByName(createUserRequestDto.getInstitutionName());
        if (institution.isEmpty()) {
            userInstitution = new Institution(createUserRequestDto.getInstitutionName());
            institutionRepository.save(userInstitution);
            log.debug("institution {} created for user {}", createUserRequestDto.getInstitutionName(), createUserRequestDto.getUsername());
        } else {
            userInstitution = institution.get();
        }
        Optional<User> existingUser = userRepository.findByUsernameAndRoleNot(createUserRequestDto.getUsername(), Role.DELETED);
        if (existingUser.isPresent())
            throw new UserAlreadyExistsException();

        User user = new User(createUserRequestDto.getUsername(), passwordEncoder.encode(createUserRequestDto.getPassword()),
                Role.USER, userInstitution, createUserRequestDto.getEmail());
        userRepository.save(user);
        log.debug("user {} created", createUserRequestDto.getUsername());
        UserResponseDto userResponseDto = UserResponseDto.toDto(user);

        return userResponseDto;


    }

    @Transactional(readOnly = true)
    public Object getUserList(UserListRequestDto userListRequestDto, Pageable pageable) {

        Specification<User> spec = Specification.where(null);
        spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.notLike(criteriaBuilder.lower(root.get("username")), adminUser)
        );

        if (Objects.nonNull(userListRequestDto.getQuery()) && !userListRequestDto.getQuery().isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + userListRequestDto.getQuery().toLowerCase() + "%")
            );
        }
        if (Objects.nonNull(userListRequestDto.getStartDate())) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), userListRequestDto.getStartDate())
            );
        }
        if (Objects.nonNull(userListRequestDto.getEndDate())) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), userListRequestDto.getEndDate())
            );
        }


        if (pageable.isPaged()) {
            Sort.Direction direction = Sort.Direction.fromString(userListRequestDto.getSortDir());
            pageable = PageRequest.of(userListRequestDto.getPage(), userListRequestDto.getSize(),
                    Sort.by(direction, userListRequestDto.getSortBy()));
            Page<UserResponseDto> userResponseDtoPage = userRepository.findAll(spec, pageable).map(UserResponseDto::toDto);
            log.debug("user list retrieved");
            return userResponseDtoPage;
        } else {
            List<UserResponseDto> userResponseDtoList = userRepository.findAll(spec).stream().map(UserResponseDto::toDto).toList();
            log.debug("user list retrieved");
            return userResponseDtoList;
        }

    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        UserResponseDto userResponseDto = UserResponseDto.toDto(user);
        log.debug("userId : {} info retrieved", id);
        return userResponseDto;
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UpdateUserRequestDto updateUserRequestDto) {

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (updateUserRequestDto.getUsername() != null && !user.getUsername().equals(updateUserRequestDto.getUsername())) {
            Optional<User> duplicateUser = userRepository.findByUsernameAndRoleNot(updateUserRequestDto.getUsername(), Role.DELETED);
            if (duplicateUser.isEmpty())
                user.setUsername(updateUserRequestDto.getUsername());
            else
                throw new UserAlreadyExistsException();
        }

        if (updateUserRequestDto.getPassword() != null)
            user.setPassword(passwordEncoder.encode(updateUserRequestDto.getPassword()));

        if (updateUserRequestDto.getEmail() != null)
            user.setEmail(updateUserRequestDto.getEmail());

        Institution userInstitution;
        Optional<Institution> optionalInstitution = institutionRepository.findByName(updateUserRequestDto.getInstitutionName());

        if (optionalInstitution.isEmpty()) {
            userInstitution = new Institution(updateUserRequestDto.getInstitutionName());
            institutionRepository.save(userInstitution);
            user.setInstitution(userInstitution);
        }
        userRepository.save(user);
        UserResponseDto userResponseDto = UserResponseDto.toDto(user);
        log.debug("userId : {} info updated", id);

        return userResponseDto;
    }

    @Transactional
    public ErrorResponseDto deleteUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        StringBuilder sb = new StringBuilder(user.getUsername()).append(" user of " )
                .append(user.getInstitution().getName()).append(" deleted");
        List<SessionToken> sessionTokenList = sessionTokenRepository.findAllByUser(user);
        sessionTokenRepository.deleteAllInBatch(sessionTokenList);
        userRepository.delete(user);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(sb.toString());
        log.debug("userId : {} token and account deleted", id);
        return errorResponseDto;

    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsernameAndRoleNot(username, Role.DELETED)
                .orElseThrow(UserNotFoundException::new);
        UserResponseDto userResponseDto = UserResponseDto.toDto(user);
        userResponseDto.setId(0L);
        log.debug("user info retrieved by username {}", username);
        return userResponseDto;

    }

    @Transactional
    public ErrorResponseDto updateSelfUser(String username, UpdateUserRequestDto updateUserRequestDto) {

        User user = userRepository.findByUsernameAndRoleNot(username, Role.DELETED)
                .orElseThrow(UserNotFoundException::new);

        if (updateUserRequestDto.getPassword() != null)
            user.setPassword(passwordEncoder.encode(updateUserRequestDto.getPassword()));

        if (updateUserRequestDto.getEmail() != null)
            user.setEmail(updateUserRequestDto.getEmail());

        userRepository.save(user);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(username + " updated");

        log.debug("user info updated by user itself {}", username);

        return errorResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Institution getInstitution(String username) {
        User user = userRepository.findByUsernameAndRoleNot(username, Role.DELETED)
                .orElseThrow(UserNotFoundException::new);

        return user.getInstitution();
    }
}
