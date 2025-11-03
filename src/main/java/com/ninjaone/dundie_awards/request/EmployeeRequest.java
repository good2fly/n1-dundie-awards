package com.ninjaone.dundie_awards.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmployeeRequest(
        @Size(min = 0, max = 512)
        String firstName,

        @Size(min = 1, max = 512)
        String lastName,

        @NotNull
        long organizationId
) {
}
