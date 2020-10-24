package pl.com.karwowsm.musiqueue.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.com.karwowsm.musiqueue.api.request.RoomCreateRequest;
import pl.com.karwowsm.musiqueue.api.request.RoomUpdateRequest;
import pl.com.karwowsm.musiqueue.persistence.model.Room;
import pl.com.karwowsm.musiqueue.persistence.model.UserAccount;
import pl.com.karwowsm.musiqueue.service.RoomService;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@AuthenticationPrincipal UserAccount userAccount, @RequestBody @Valid RoomCreateRequest request) {
        log.trace("Creating room: {}", request);
        Room room = service.create(userAccount, request);
        log.debug("Created room: {}", room);
        return room;
    }

    @GetMapping
    public Page<Room> findRoom(Pageable pageable) {
        log.trace("Finding room: {}", pageable);
        Page<Room> page = service.find(pageable);
        log.debug("Found rooms: [numberOfElements={}, totalElements={}]", page.getNumberOfElements(), page.getTotalElements());
        return page;
    }

    @GetMapping("/{id}")
    public Room getRoom(@PathVariable UUID id) {
        log.trace("Getting room: id={}", id);
        Room room = service.get(id);
        log.debug("Got room: {}", room);
        return room;
    }

    @PutMapping("/{id}")
    public Room updateRoom(@AuthenticationPrincipal UserAccount userAccount, @PathVariable UUID id, @RequestBody @Valid RoomUpdateRequest request) {
        log.trace("Updating room: [id={}, {}]", id, request);
        Room room = service.update(id, request, userAccount);
        log.debug("Updated room: {}", room);
        return room;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@AuthenticationPrincipal UserAccount userAccount, @PathVariable UUID id) {
        log.trace("Deleting room: id={}", id);
        service.delete(id, userAccount);
        log.debug("Deleted room: id={}", id);
    }
}
