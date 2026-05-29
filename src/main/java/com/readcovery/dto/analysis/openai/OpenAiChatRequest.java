package com.readcovery.dto.analysis.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAiChatRequest {

    private String model;
    private List<Message> messages;

    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    private Double temperature;

    @Getter
    @Builder
    public static class Message {
        private String role;
        private String content;
    }

    @Getter
    @Builder
    public static class ResponseFormat {
        private String type;
    }
}
