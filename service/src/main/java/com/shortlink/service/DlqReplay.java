package com.shortlink.service;

import com.shortlink.db.DeadLetterRepository;
import com.shortlink.db.entity.DeadLetter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class DlqReplay  implements CommandLineRunner {
    private final DeadLetterRepository dlqRepo;
    private final ShortLinkService service;

    public DlqReplay(DeadLetterRepository dlqRepo, ShortLinkService service) {
        this.dlqRepo = dlqRepo;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        for (DeadLetter msg : dlqRepo.findAll()) {
            try {
                service.createOrGetShortCode(msg.getOriginalUrl(), msg.getCustomCode(), msg.getTenantId(), msg.getDomain());
                dlqRepo.delete(msg);
            } catch (Exception e) {
                log.warn("Failed to reprocess DLQ entry id={} url={} reason={}",
                        msg.getId(),
                        msg.getOriginalUrl(),
                        e.getMessage(), e);

                msg.setReason("Retry failed, at : " + Instant.now() + ", reason: " + e.getMessage());
                dlqRepo.save(msg);
            }
        }
    }
}
