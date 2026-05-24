package com.americobarber.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(60L * 60L * 1000L);

        this.emitters.add(emitter);

        emitter.onCompletion(() -> {
            log.debug("SSE Completed");
            this.emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            log.debug("SSE Timeout");
            emitter.complete();
            this.emitters.remove(emitter);
        });
        emitter.onError((e) -> {
            log.debug("SSE Error", e);
            emitter.completeWithError(e);
            this.emitters.remove(emitter);
        });

        // Envia um evento inicial de conexão para garantir que a conexão não quebre
        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void broadcast(String eventName, Object data) {
        log.info("Broadcasting event '{}' to {} listeners", eventName, emitters.size());
        
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                log.debug("Error broadcasting to emitter", e);
                deadEmitters.add(emitter);
            }
        }
        
        this.emitters.removeAll(deadEmitters);
    }
}
