package com.moptra.invoice.authentication.provider;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.user.entities.rd.UserIdentity;
import com.moptra.invoice.user.repository.rd.UserIdentityRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserIdentityRepository userIdentityRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserIdentityRepository userIdentityRepository, PasswordEncoder passwordEncoder) {
        this.userIdentityRepository = userIdentityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String rawPassword = Objects.requireNonNull(authentication.getCredentials()).toString();

        UserIdentity identity = userIdentityRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, identity.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        CustomUserDetails principal = getCustomUserDetails(identity);

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }

    private static @NonNull CustomUserDetails getCustomUserDetails(@NonNull UserIdentity identity) {
        String tenantId = "master";
        String companyId = null;

        if (identity.getCompany() != null) {
            companyId = identity.getCompany().getId();
            String sanitizedCode = identity.getCompany().getCode()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "_");
            tenantId = "tenant_" + sanitizedCode;
        }

        return new CustomUserDetails(
                identity.getEmail(),
                identity.getPasswordHash(),
                tenantId,
                companyId,
                identity.getRole()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}