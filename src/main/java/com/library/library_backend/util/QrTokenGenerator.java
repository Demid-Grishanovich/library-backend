package com.library.library_backend.util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Генератор URL-safe токенов для QR.
 * Статический утилитарный класс.
 */
public final class QrTokenGenerator {
    private static final SecureRandom RND = new SecureRandom();

    private QrTokenGenerator() { }

    /** Генерирует случайный URL-safe токен (без паддинга). */
    public static String generate() {
        byte[] bytes = new byte[24]; // ~32 символа Base64URL без '='
        RND.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
