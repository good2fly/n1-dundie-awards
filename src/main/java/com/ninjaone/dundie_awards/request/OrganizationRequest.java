package com.ninjaone.dundie_awards.request;

import jakarta.validation.constraints.Size;

public record OrganizationRequest(
        @Size(min = 1, max = 512)
        String name
) {
}
