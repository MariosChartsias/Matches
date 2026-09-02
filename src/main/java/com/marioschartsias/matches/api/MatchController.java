package com.marioschartsias.matches.api;

import com.marioschartsias.matches.api.dto.MatchRequest;
import com.marioschartsias.matches.api.dto.MatchResponse;
import com.marioschartsias.matches.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public List<MatchResponse> findAll() {
        return matchService.findAll();
    }

    @GetMapping("/{id}")
    public MatchResponse findById(@PathVariable Long id) {
        return matchService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MatchResponse> create(@Valid @RequestBody MatchRequest request) {
        MatchResponse created = matchService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/matches/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    public MatchResponse update(@PathVariable Long id, @Valid @RequestBody MatchRequest request) {
        return matchService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

