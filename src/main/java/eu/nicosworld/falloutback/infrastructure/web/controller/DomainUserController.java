package eu.nicosworld.falloutback.infrastructure.web.controller;

import eu.nicosworld.falloutback.domain.domainUser.DomainUserService;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.web.dto.DomainUserDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class DomainUserController {

    private final DomainUserService domainUserService;

    public DomainUserController(DomainUserService domainUserService) {
        this.domainUserService = domainUserService;
    }

    @GetMapping
    public DomainUserDto getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        DomainUser user = this.domainUserService.findByUser(userDetails);
        System.out.println("DEBUG");
        System.out.println(DomainUserDto.mapFromEntity(user));
        return DomainUserDto.mapFromEntity(user);
    }
}
