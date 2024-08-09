package com.sparta.itsmine.global.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.activemq.user:itsmine1010}")
    private String activeUser;
    @Value("${spring.activemq.password:itsmine101010}")
    private String activePwd;
    @Value("${spring.activemq.broker-url}")
    private String activeHost;
    @Value("${activemq.stomp-host}")
    private String stompHost;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //배포 했을때 주석한 부분 해제
//        registry.enableStompBrokerRelay("/topic", "/queue")
//                .setRelayHost(stompHost)
//                .setClientLogin(activeUser)
//                .setClientPasscode(activePwd)
//                .setRelayPort(61614);

        registry.enableSimpleBroker("/topic", "/queue"); // 배포 시 이부분만 주석
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://52.79.213.8");

        // 주소 : ws://localhost:8080/ws
    }
}