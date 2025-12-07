package eu.nicosworld.falloutback.infrastructure.web.controller;

import eu.nicosworld.falloutback.domain.authenticatedUser.AuthenticatedUserService;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.falloutback.infrastructure.web.dto.AuthenticatedUserDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class AuthenticatedUserController {

    private final AuthenticatedUserService authenticatedUserService;

    public AuthenticatedUserController(AuthenticatedUserService authenticatedUserService) {
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping
    public AuthenticatedUserDto getSmoked(@AuthenticationPrincipal UserDetails userDetails) {
        AuthenticatedUser user = this.authenticatedUserService.findByUser(userDetails);
        return AuthenticatedUserDto.mapFromEntity(user);
    }
}
