package com.roman.multi_file_processing.service.implementation;

import com.roman.multi_file_processing.service.EmailService;
import com.roman.multi_file_processing.service.dto.EmailContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void send(final EmailContent content) {
        var baseUrl = "http://localhost:20002/storage/";
        var greeting = """
                Greetings from Spring Batch Email Project
                """;
        String emailBody = content.fileCodes()
                .stream()
                .map(item -> baseUrl.concat(item).concat("\n"))
                .collect(Collectors.joining());

        var simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("roman@google.com");
        simpleMailMessage.setTo("roman.sherstuyk@gmail.com");
        simpleMailMessage.setSubject("IMPORTANT: Uploaded files codes");
        simpleMailMessage.setText(greeting.concat(emailBody));

        log.info("--------------> Sending Email");
        javaMailSender.send(simpleMailMessage);
    }
}
