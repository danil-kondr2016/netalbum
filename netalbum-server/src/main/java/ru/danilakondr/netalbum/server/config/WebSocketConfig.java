package ru.danilakondr.netalbum.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import ru.danilakondr.netalbum.server.controller.NetAlbumHandler;

@Configuration
@EnableWebSocket
@ComponentScan(basePackages="ru.danilakondr.netalbum.server")
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(netAlbumHandler(), "/api")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOriginPatterns("*");
    }
    @Bean
    public WebSocketHandler netAlbumHandler() {
        return new NetAlbumHandler();
    }
    
    @Bean
    public ServletServerContainerFactoryBean serverContainer() {
        ServletServerContainerFactoryBean b = new ServletServerContainerFactoryBean();
        b.setMaxBinaryMessageBufferSize(2091752);
        b.setMaxTextMessageBufferSize(2796204);
        b.setMaxSessionIdleTimeout(Long.valueOf(60000));
        return b;
    }
}