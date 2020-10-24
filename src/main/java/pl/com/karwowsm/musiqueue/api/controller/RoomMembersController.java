package pl.com.karwowsm.musiqueue.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;
import pl.com.karwowsm.musiqueue.service.RoomMembersService;
import pl.com.karwowsm.musiqueue.service.RoomService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/rooms/{id}/members")
@RequiredArgsConstructor
public class RoomMembersController {

    private final RoomService roomService;

    private final RoomMembersService service;

    @GetMapping
    public Page<UserAccount> findRoomMember(@PathVariable UUID id, Pageable pageable) {
        log.trace("Finding room member: roomId={}", id);
        Page<UserAccount> members = service.findUserAccount(roomService.get(id), pageable);
        log.debug("Found room members: {}", members);
        return members;
    }

    @PatchMapping("/me")
    public Room joinRoom(@AuthenticationPrincipal UserAccount userAccount, @PathVariable UUID id) {
        log.trace("Joining room: [id={}, {}]", id, userAccount);
        Room room = service.join(roomService.get(id), userAccount);
        log.debug("Joined room: {}", room);
        return room;
    }

    @DeleteMapping("/me")
    public Room leaveRoom(@AuthenticationPrincipal UserAccount userAccount, @PathVariable UUID id) {
        log.trace("Leaving room: [id={}, {}]", id, userAccount);
        Room room = service.leave(roomService.get(id), userAccount);
        log.debug("Leaved room: {}", room);
        return room;
    }
}
