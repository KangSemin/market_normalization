package no.gunbang.market.common;

import java.util.Arrays;

public enum Status {
    ON_SALE,
    COMPLETED,
    CANCELLED;

    public static Status of(String insertedStatus) {
        return Arrays.stream(Status.values())
            .filter(
                status -> status
                    .name()
                    .equalsIgnoreCase(insertedStatus))
            .findFirst()
            .orElseThrow();
    }
}