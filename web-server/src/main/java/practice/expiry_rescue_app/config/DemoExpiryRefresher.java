package practice.expiry_rescue_app.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.expiry_rescue_app.repository.ProductInventoryRepository;

/**
 * Keeps demo data current. Each seeded inventory row stores an {@code expiryOffsetDays}
 * (its intended "days from now"); this job re-anchors every row's expiryDate to
 * (now + offset) on a schedule, so "ending soon" and discount badges stay correct
 * instead of drifting into the past.
 * Active only when app.demo.enabled=true. Scheduled jobs run only while the instance is
 * awake, so on a host that idles when unused this depends on regular traffic to fire.
 */

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.demo", name = "enabled", havingValue = "true")
public class DemoExpiryRefresher {

    private static final long DAY_MS = 86_400_000L;

    private final ProductInventoryRepository inventoryRepository;

    // Every 6 hours; overridable via app.demo.expiry-refresh-cron.
    @Scheduled(cron = "${app.demo.expiry-refresh-cron:0 0 */6 * * *}")
    @Transactional
    public void refreshExpiries() {
        int updated = inventoryRepository.refreshExpiriesRelativeToNow(System.currentTimeMillis(), DAY_MS);
        log.info("Demo: re-anchored expiry dates for {} inventory rows", updated);
    }
}
