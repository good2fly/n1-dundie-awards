package com.ninjaone.dundie_awards.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ActivityRequest(
        @Size(min = 1, max = 1024)
        String event,

        @NotNull
        LocalDateTime occurredAt
) {
}
