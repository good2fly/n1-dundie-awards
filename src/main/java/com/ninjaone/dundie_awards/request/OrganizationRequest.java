package com.ninjaone.dundie_awards.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrganizationRequest(
        @NotNull
        @Size(min = 1, max = 512)
        String name
) {
}
