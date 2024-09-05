package com.roman.multi_file_processing.service.dto;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public record EmailContent(List<String> fileCodes) {
    public EmailContent {
        Objects.requireNonNull(fileCodes, "file codes cannot be null");
        if(fileCodes.isEmpty()){
            log.warn("No content for email have been provided");
        }
    }
}
