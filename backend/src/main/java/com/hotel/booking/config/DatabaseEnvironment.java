package com.hotel.booking.config;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class DatabaseEnvironment {
    private static final List<String> HOSTED_DATABASE_URL_KEYS = List.of(
            "MYSQL_DATABASE_URL",
            "MYSQL_URL",
            "DATABASE_URL",
            "JAWSDB_URL",
            "CLEARDB_DATABASE_URL"
    );

    private DatabaseEnvironment() {}

    public static void configure() {
        if (hasText(System.getenv("SPRING_DATASOURCE_URL")) || hasText(System.getProperty("spring.datasource.url"))) {
            return;
        }

        HOSTED_DATABASE_URL_KEYS.stream()
                .map(System::getenv)
                .filter(DatabaseEnvironment::hasText)
                .findFirst()
                .ifPresent(DatabaseEnvironment::applyDatabaseUrl);
    }

    private static void applyDatabaseUrl(String databaseUrl) {
        if (databaseUrl.startsWith("jdbc:mysql://")) {
            System.setProperty("spring.datasource.url", databaseUrl);
            return;
        }

        if (!databaseUrl.startsWith("mysql://")) {
            return;
        }

        URI uri = URI.create(databaseUrl);
        String database = uri.getPath() == null ? "" : uri.getPath().replaceFirst("^/", "");
        String query = hasText(uri.getQuery()) ? "?" + uri.getQuery() : "";
        String jdbcUrl = "jdbc:mysql://" + uri.getHost() + getPort(uri) + "/" + database + query;

        System.setProperty("spring.datasource.url", jdbcUrl);
        applyCredentials(uri);
    }

    private static String getPort(URI uri) {
        return uri.getPort() == -1 ? "" : ":" + uri.getPort();
    }

    private static void applyCredentials(URI uri) {
        if (!hasText(uri.getUserInfo())) {
            return;
        }

        String[] parts = uri.getUserInfo().split(":", 2);
        if (!hasText(System.getenv("MYSQL_USER")) && !hasText(System.getenv("SPRING_DATASOURCE_USERNAME"))) {
            System.setProperty("spring.datasource.username", decode(parts[0]));
        }
        if (parts.length > 1 && !hasText(System.getenv("MYSQL_PASSWORD")) && !hasText(System.getenv("SPRING_DATASOURCE_PASSWORD"))) {
            System.setProperty("spring.datasource.password", decode(parts[1]));
        }
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            return value;
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
