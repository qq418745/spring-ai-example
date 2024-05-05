package com.coalai.service;


import com.coalai.dto.MessageHistory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.messages.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class SessionService {

    private final HttpServletResponse response;

    /**
     * 给当前请求设置响应头
     */
    public void setResponseHeader() {
        // Set the content type to text/event-stream
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE + "; charset=UTF-8");
        response.addHeader("cache-control", "no-cache");
        response.addHeader("connection", "keep-alive");
    }

    public Message[] getMessages(MessageHistory messageHistory) {

        ArrayList<Message> messages = new ArrayList<>();

        for (MessageHistory.Message message : messageHistory.getMessages()) {
            String role = message.getRole();

            for (MessageHistory.Message.Content content : message.getContent()) {

                String type = content.getType();
                // type 先不管 默认都是 text
                if (!"text".equals(type)) {
                    throw new RuntimeException("Content type error: " + type);
                }

                if (MessageType.USER.getValue().equals(role)) {
                    String text = content.getText();
                    UserMessage userMessage = new UserMessage(text);
                    messages.add(userMessage);
                } else if (MessageType.SYSTEM.getValue().equals(role)) {
                    String text = content.getText();
                    SystemMessage systemMessage = new SystemMessage(text);
                    messages.add(systemMessage);
                } else if (MessageType.ASSISTANT.getValue().equals(role)) {
                    String text = content.getText();
                    AssistantMessage assistantMessage = new AssistantMessage(text);
                    messages.add(assistantMessage);
                } else {
                    throw new RuntimeException("Role error: " + role);
                }
            }
        }

        return messages.toArray(new Message[messages.size()]);
    }
}
