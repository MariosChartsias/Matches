package com.marioschartsias.matches.api;

import com.marioschartsias.matches.api.dto.MatchOddRequest;
import com.marioschartsias.matches.api.dto.MatchOddResponse;
import com.marioschartsias.matches.service.MatchOddService;
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
@RequestMapping("/api/v1/matches/{matchId}/odds")
public class MatchOddController {

    private final MatchOddService matchOddService;

    public MatchOddController(MatchOddService matchOddService) {
        this.matchOddService = matchOddService;
    }

    @GetMapping
    public List<MatchOddResponse> findAll(@PathVariable Long matchId) {
        return matchOddService.findAll(matchId);
    }

    @GetMapping("/{oddId}")
    public MatchOddResponse findById(@PathVariable Long matchId, @PathVariable Long oddId) {
        return matchOddService.findById(matchId, oddId);
    }

    @PostMapping
    public ResponseEntity<MatchOddResponse> create(
            @PathVariable Long matchId,
            @Valid @RequestBody MatchOddRequest request
    ) {
        MatchOddResponse created = matchOddService.create(matchId, request);
        return ResponseEntity
                .created(URI.create("/api/v1/matches/" + matchId + "/odds/" + created.id()))
                .body(created);
    }

    @PutMapping("/{oddId}")
    public MatchOddResponse update(
            @PathVariable Long matchId,
            @PathVariable Long oddId,
            @Valid @RequestBody MatchOddRequest request
    ) {
        return matchOddService.update(matchId, oddId, request);
    }

    @DeleteMapping("/{oddId}")
    public ResponseEntity<Void> delete(@PathVariable Long matchId, @PathVariable Long oddId) {
        matchOddService.delete(matchId, oddId);
        return ResponseEntity.noContent().build();
    }
}

