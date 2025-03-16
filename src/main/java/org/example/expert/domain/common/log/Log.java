package org.example.expert.domain.common.log;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "logs")
public class Log {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String requestUrl;
    private String method;
    @Enumerated(EnumType.STRING)
    private LogStatus responseStatus;
    private LocalDateTime requestTime;

    public Log(Long userId, String requestUrl, String method, LogStatus responseStatus) {
        this.userId = userId;
        this.requestUrl = requestUrl;
        this.method = method;
        this.responseStatus = responseStatus;
        this.requestTime = LocalDateTime.now();
    }
}
