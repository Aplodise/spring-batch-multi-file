package com.roman.multi_file_processing.dto;

import java.io.File;

public record UploadFileDTO(String fileName, File file) {
}
