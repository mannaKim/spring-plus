package org.example.expert.domain.common.log;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(Long userId, String requestUrl, String method, LogStatus responseStatus) {
        Log log = new Log(userId, requestUrl, method, responseStatus);
        logRepository.save(log);
    }
}