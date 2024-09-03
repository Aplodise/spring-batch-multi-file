package com.roman.multi_file_processing.dto;

public record VehicleDTO(Long referenceNumber,
                         String model,
                         String type,
                         String customerFullName) {
}
