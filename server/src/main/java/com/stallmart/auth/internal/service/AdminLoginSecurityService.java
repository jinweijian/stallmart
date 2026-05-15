package com.stallmart.auth.internal.service;

import com.stallmart.auth.dto.AdminCaptchaDTO;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class AdminLoginSecurityService {

    private static final int CAPTCHA_THRESHOLD = 3;
    private static final Duration FAILURE_TTL = Duration.ofMinutes(15);
    private static final Duration CAPTCHA_TTL = Duration.ofMinutes(5);

    private final SecureRandom random = new SecureRandom();
    private final Map<String, FailureState> failures = new ConcurrentHashMap<>();
    private final Map<String, CaptchaState> captchas = new ConcurrentHashMap<>();

    public AdminCaptchaDTO createCaptcha() {
        int left = random.nextInt(9) + 1;
        int right = random.nextInt(9) + 1;
        String captchaId = UUID.randomUUID().toString();
        captchas.put(captchaId, new CaptchaState(
                String.valueOf(left + right),
                Instant.now().plus(CAPTCHA_TTL)
        ));
        return new AdminCaptchaDTO(captchaId, left + " + " + right + " = ?");
    }

    public boolean isCaptchaRequired(String account, String ipAddress) {
        FailureState state = activeFailureState(key(account, ipAddress));
        return state != null && state.count >= CAPTCHA_THRESHOLD;
    }

    public int recordFailure(String account, String ipAddress) {
        String key = key(account, ipAddress);
        Instant now = Instant.now();
        FailureState state = failures.compute(key, (ignored, current) -> {
            if (current == null || current.expiresAt.isBefore(now)) {
                return new FailureState(1, now.plus(FAILURE_TTL));
            }
            return new FailureState(current.count + 1, now.plus(FAILURE_TTL));
        });
        return state.count;
    }

    public void clearFailures(String account, String ipAddress) {
        failures.remove(key(account, ipAddress));
    }

    public boolean verifyCaptcha(String captchaId, String answer) {
        if (captchaId == null || captchaId.isBlank() || answer == null || answer.isBlank()) {
            return false;
        }
        CaptchaState state = captchas.remove(captchaId);
        return state != null
                && !state.expiresAt.isBefore(Instant.now())
                && state.answer.equals(answer.trim());
    }

    private FailureState activeFailureState(String key) {
        FailureState state = failures.get(key);
        if (state == null) {
            return null;
        }
        if (state.expiresAt.isBefore(Instant.now())) {
            failures.remove(key);
            return null;
        }
        return state;
    }

    private String key(String account, String ipAddress) {
        String normalizedAccount = account == null ? "" : account.trim().toLowerCase(Locale.ROOT);
        return normalizedAccount + "|" + (ipAddress == null ? "" : ipAddress);
    }

    private record FailureState(int count, Instant expiresAt) {
    }

    private record CaptchaState(String answer, Instant expiresAt) {
    }
}
