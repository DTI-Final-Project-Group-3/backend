package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.entity.EmailVerificationToken;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.users.dto.CreateUserRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.EmailVerificationTokenRepository;
import com.warehub.warehub.infrastructure.users.repository.RolesRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.CreateUserUsecase;
import com.warehub.warehub.usecase.user.EmailVerificationUsecase;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

import static com.warehub.warehub.entity.enums.RoleUtil.roleEnumFromString;

@Service
public class CreateUserUsecaseImpl implements CreateUserUsecase {
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailVerificationUsecase emailVerificationUsecase;

    public CreateUserUsecaseImpl(UsersRepository usersRepository,
                                 RolesRepository rolesRepository,
                                 PasswordEncoder passwordEncoder,
                                 EmailVerificationTokenRepository emailVerificationTokenRepository,
                                 EmailVerificationUsecase emailVerificationUsecase) {
        this.usersRepository = usersRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailVerificationUsecase = emailVerificationUsecase;
    }

    @Transactional
    @Override
    public UserDetailResponseDTO createUser(CreateUserRequestDTO req, String role) {
        RoleType roleType = roleEnumFromString(role, RoleType.NOT_VERIFIED);

        Optional<User> existingUser = usersRepository.findByEmailContainsIgnoreCase(req.getEmail());

        if (existingUser.isPresent()) {
            // Don't register if already registered and verified
            if (!existingUser.get().getRole().getName().equals(RoleType.NOT_VERIFIED.toString())) {
                throw new RuntimeException("Email " + req.getEmail() + " is already registered and verified");
            }

            Optional<EmailVerificationToken> emailVerificationToken = emailVerificationTokenRepository.findByUserId(existingUser.get().getId());

            if (emailVerificationToken.isPresent() &&
                    emailVerificationToken.get().getCreatedAt().isBefore(OffsetDateTime.now().minusMinutes(1))) {
                // Resend email verification link if user registered the same email after 1 minute
                emailVerificationTokenRepository.delete(emailVerificationToken.get());
                emailVerificationUsecase.sendEmailVerificationLink(existingUser.get().getId());
                return new UserDetailResponseDTO().copyFromUser(existingUser.get());
            } else {
                throw new RuntimeException("Wait for 1 minute before requesting a new email verification link");
            }
        }

        // Only create new user if no existing record is found
        User newUser = req.toEntity();
        newUser.setPasswordHash(passwordEncoder.encode(""));
        newUser.setRole(rolesRepository.findByName(roleType.toString()).orElseThrow(() -> new RuntimeException("Role not found")));
        newUser.setIsEmailVerified(roleType != RoleType.NOT_VERIFIED);

        var savedUser = usersRepository.save(newUser);
        emailVerificationUsecase.sendEmailVerificationLink(savedUser.getId());

        return new UserDetailResponseDTO().copyFromUser(savedUser);
    }
}
