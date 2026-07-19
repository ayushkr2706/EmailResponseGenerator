package com.ayush.EmailResponseGenerator.service;

import com.ayush.EmailResponseGenerator.DTO.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class EmailService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(EmailRequest request){

       //build the prompt
       String prompt = buildPrompt(request);

       //Build the request
       Map<String, Object> requestBody = Map.of(
               "contents", new Object[]{
                       Map.of("parts", new Object[]{
                               Map.of("text", prompt)
                       })
               }
       );

       //Send request and get a response from the Gemini server
        String response = webClient.post()
                .uri(geminiApiUrl +geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

       //Extract the response and Send the response to the user
        return extractResponseContent(response);

   }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch(Exception e){
            return "Error processing request : " + e.getMessage();
        }
    }


    public String buildPrompt(EmailRequest request){

        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate an email response for the following email content. Don't generate the subject. ");
        if(request.getEmailTone() == null || request.getEmailTone().isEmpty()){
            prompt.append("Use a professional tone");
        }
        else if(request.getEmailTone() != null && !request.getEmailTone().isEmpty()){
            prompt.append("Use a ").append(request.getEmailTone()).append(" tone.");
        }
        prompt.append("\n Original email is : \n").append(request.getEmailContent());
        return prompt.toString();
    }
}
