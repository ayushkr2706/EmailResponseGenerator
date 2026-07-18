package com.ayush.EmailResponseGenerator.DTO;

import lombok.Data;

@Data
public class EmailRequest {

    private String emailContent;
    private String emailTone;
}
