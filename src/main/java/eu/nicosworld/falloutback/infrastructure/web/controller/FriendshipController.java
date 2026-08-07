package eu.nicosworld.falloutback.infrastructure.web.controller;

import eu.nicosworld.falloutback.domain.friendship.FriendshipService;
import eu.nicosworld.falloutback.infrastructure.web.dto.friendship.FriendRequestDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.friendship.FriendshipResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PostMapping("/request")
    public ResponseEntity<FriendshipResponseDto> sendFriendRequest(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody FriendRequestDto requestDto) {
        return ResponseEntity.ok(friendshipService.sendFriendRequest(userDetails.getUsername(), requestDto.username()));
    }

    @PostMapping("/request/{friendshipId}/accept")
    public ResponseEntity<FriendshipResponseDto> acceptRequest(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long friendshipId) {
        return ResponseEntity.ok(friendshipService.respondToFriendRequest(userDetails.getUsername(), friendshipId, true));
    }

    @PostMapping("/request/{friendshipId}/decline")
    public ResponseEntity<FriendshipResponseDto> declineRequest(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long friendshipId) {
        return ResponseEntity.ok(friendshipService.respondToFriendRequest(userDetails.getUsername(), friendshipId, false));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendshipResponseDto>> getPendingRequests(
        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendshipService.getPendingRequests(userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<FriendshipResponseDto>> getFriends(
        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendshipService.getFriendsList(userDetails.getUsername()));
    }
}