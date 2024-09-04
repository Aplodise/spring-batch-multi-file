package com.roman.multi_file_processing.dto;

public record VehicleJsonDTO(Long referenceNumber,
                             String brand,
                             String model,
                             String type,
                             String customerFullName,
                             Double price) {
}
