package ru.itscan.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itscan.dto.AskDto;
import ru.itscan.service.RagService;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
@Slf4j
public class RagController {

    private final RagService ragService;

    @PostMapping("/ask")
    public ResponseEntity<String> askModel(@RequestBody AskDto prompt) {
        return ResponseEntity.ok().body(ragService.askModel(prompt.prompt()));
    }
}
