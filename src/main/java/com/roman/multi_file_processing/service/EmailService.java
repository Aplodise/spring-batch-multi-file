package com.roman.multi_file_processing.service;

import com.roman.multi_file_processing.service.dto.EmailContent;

public interface EmailService {
    void send(EmailContent content);

}
