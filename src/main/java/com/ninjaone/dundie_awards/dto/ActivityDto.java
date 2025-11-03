package com.ninjaone.dundie_awards.dto;

import java.time.LocalDateTime;

public record ActivityDto(long id, String event, LocalDateTime occurredAt) {
}
