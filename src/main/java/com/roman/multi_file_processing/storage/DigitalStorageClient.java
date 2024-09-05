package com.roman.multi_file_processing.storage;


import com.roman.multi_file_processing.storage.dto.FileToUpload;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.nio.file.Files;

@Component
public class DigitalStorageClient {

    private final RestClient restClient = RestClient.create();

    @SneakyThrows
    public String upload(File file){
        var fileToUpload = new FileToUpload(file.getName(), Files.readAllBytes(file.toPath()));
        return restClient
                .post()
                .uri("http://localhost:20002/storage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fileToUpload)
                .retrieve()
                .body(String.class);
    }
}
