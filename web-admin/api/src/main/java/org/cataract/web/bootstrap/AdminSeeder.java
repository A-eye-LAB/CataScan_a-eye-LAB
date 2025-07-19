package org.cataract.web.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.cataract.web.domain.ImageStorage;
import org.cataract.web.domain.Institution;
import org.cataract.web.domain.Role;
import org.cataract.web.domain.User;
import org.cataract.web.infra.ImageStorageRepository;
import org.cataract.web.infra.InstitutionRepository;
import org.cataract.web.infra.UserRepository;
import org.cataract.web.presentation.dto.requests.CreateUserRequestDto;
import org.cataract.web.presentation.dto.requests.ImageStorageCreateRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${app.admin.username}")
    String adminUser;
    @Value("${app.admin.password}")
    String adminUserPassword;
    @Value("${app.admin.email}")
    String adminUserEmail;
    @Value("${app.admin.institution}")
    String adminUserInstitution;
    @Value("${app.image.bucket.name}")
    String defaultBucketName;
    @Value("${app.image.bucket.region}")
    String defaultBucketRegion;

    public AdminSeeder(UserRepository userRepository,
                       InstitutionRepository institutionRepository,
                       ImageStorageRepository imageStorageRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.imageStorageRepository = imageStorageRepository;
        this.passwordEncoder = passwordEncoder;

    }

    private final UserRepository userRepository;

    private final InstitutionRepository institutionRepository;
    private final ImageStorageRepository imageStorageRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
    }

    private void createSuperAdministrator() {
        CreateUserRequestDto userDto = new CreateUserRequestDto();
        userDto.setUsername(adminUser);
        userDto.setPassword(adminUserPassword);
        userDto.setEmail(adminUserEmail);

        Optional<User> optionalUser = userRepository.findByUsernameAndRoleNot(userDto.getUsername(), Role.DELETED);

        if (optionalUser.isPresent()) {
            log.info("admin user {} already exists ", adminUser);
            return;
        }
        ImageStorage imageStorage = new ImageStorage(new ImageStorageCreateRequestDto(defaultBucketName, defaultBucketRegion));
        Optional<ImageStorage> optionalImageStorage = imageStorageRepository.findByBucketName(defaultBucketName);
        ImageStorage imageStorage1 = optionalImageStorage.orElse(imageStorageRepository.save(imageStorage));
        Institution adminInstitution = institutionRepository.findByName(adminUserInstitution)
                .orElse(new Institution(adminUserInstitution));
        adminInstitution.setImageStorage(imageStorage1);
        institutionRepository.save(adminInstitution);

        var user = new User(userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()),
                Role.ADMIN, adminInstitution, userDto.getEmail());

        userRepository.save(user);
        log.info("admin user {} created ", adminUser);
    }

}