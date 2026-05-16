package com.stallmart.auth.internal.service;

import com.stallmart.auth.dto.AdminCaptchaDTO;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
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

    private final Map<String, FailureState> failures = new ConcurrentHashMap<>();
    private final Map<String, CaptchaState> captchas = new ConcurrentHashMap<>();

    public AdminCaptchaDTO createCaptcha() {
        SpecCaptcha captcha = new SpecCaptcha(130, 44, 4);
        captcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        String captchaId = UUID.randomUUID().toString();
        captchas.put(captchaId, new CaptchaState(
                captcha.text(),
                Instant.now().plus(CAPTCHA_TTL)
        ));
        return new AdminCaptchaDTO(captchaId, captcha.toBase64());
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
