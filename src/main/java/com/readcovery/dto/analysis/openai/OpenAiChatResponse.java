package com.readcovery.dto.analysis.openai;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OpenAiChatResponse {

    private String id;
    private String model;
    private List<Choice> choices;

    @Getter
    @NoArgsConstructor
    public static class Choice {
        private Integer index;
        private Message message;
        private String finishReason;
    }

    @Getter
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}
