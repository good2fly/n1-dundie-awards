package com.ninjaone.dundie_awards.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmployeeRequest(
        @NotNull
        @Size(min = 1, max = 512)
        String firstName,

        @NotNull
        @Size(min = 1, max = 512)
        String lastName,

        long organizationId
) {
}
