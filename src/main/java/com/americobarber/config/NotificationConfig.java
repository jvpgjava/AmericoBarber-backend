package com.americobarber.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableConfigurationProperties(WhatsAppProperties.class)
public class NotificationConfig implements AsyncConfigurer {

    @Bean(name = "whatsAppTaskExecutor")
    public Executor whatsAppTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("whatsapp-");
        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return whatsAppTaskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new LoggingAsyncUncaughtExceptionHandler();
    }

    @Slf4j
    static class LoggingAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            log.error("Erro não tratado em execução assíncrona de {}: {}", method.getName(), ex.getMessage(), ex);
        }
    }
}
