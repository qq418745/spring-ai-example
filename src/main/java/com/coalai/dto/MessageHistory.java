package com.coalai.dto;

import lombok.Data;

import java.util.List;


@Data
public class MessageHistory {

    List<Message> messages;

    String model;

    @Data
    public static class Message {
        List<Content> content;
        String role;

        @Data
        public static class Content {
            String type;
            String text;
        }
    }
}
