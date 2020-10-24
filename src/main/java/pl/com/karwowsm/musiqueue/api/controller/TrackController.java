package pl.com.karwowsm.musiqueue.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.karwowsm.musiqueue.persistence.model.Track;
import pl.com.karwowsm.musiqueue.service.TrackService;

@Slf4j
@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService service;

    @GetMapping
    public Page<Track> findTrack(Pageable pageable) {
        log.trace("Finding track: {}", pageable);
        Page<Track> page = service.find(pageable);
        log.debug("Found tracks: [numberOfElements={}, totalElements={}]", page.getNumberOfElements(), page.getTotalElements());
        return page;
    }
}
