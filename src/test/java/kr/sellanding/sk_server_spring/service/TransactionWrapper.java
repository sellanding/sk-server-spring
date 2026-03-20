package kr.sellanding.sk_server_spring.service;

import java.util.UUID;
import kr.sellanding.sk_server_spring.repository.UsageCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionWrapper {

    @Autowired
    private UsageCounterRepository usageCounterRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increment(UUID userId) {
        usageCounterRepository.incrementInkCount(userId);
    }
}
