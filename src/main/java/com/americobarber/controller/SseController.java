package com.americobarber.controller;

import com.americobarber.service.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/admin/stream")
@RequiredArgsConstructor
@Tag(name = "Server-Sent Events", description = "Endpoints for Real-Time SSE updates via EventSource")
public class SseController {

    private final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Subscribe to SSE events", description = "Clients can subscribe here to receive real-time admin events.")
    public SseEmitter streamEvents() {
        return sseService.subscribe();
    }
}
