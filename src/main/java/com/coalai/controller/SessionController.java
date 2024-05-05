package com.coalai.controller;


import com.coalai.dto.MessageHistory;
import com.coalai.service.SessionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.io.PrintWriter;

@RestController
@AllArgsConstructor
@RequestMapping("/session")
@Slf4j
public class SessionController {

    private final StreamingChatClient streamingChatClient;
    private final HttpServletResponse response;
    private final SessionService sessionService;


    /**
     * 完全同步的，阻塞线程. 消耗资源但是足够的简单！
     *
     * @param messageHistory
     * @throws IOException
     * return 不遵守 SSE 协议规范, 直接返回结果！
     */
    @PostMapping(value = "/sync/stream")
    public void syncStream2(@RequestBody MessageHistory messageHistory) throws IOException {

        sessionService.setResponseHeader();

        // Get the PrintWriter to write the response
        PrintWriter writer = response.getWriter();

        Message[] messages = sessionService.getMessages(messageHistory);

        streamingChatClient.stream(messages).map(message -> {
            // writer.write("data:" + message + "\n\n"); 这是遵守 SSE 规范的写法. 这里不遵守。
            writer.write(message);
            writer.flush(); // Make sure to flush after each message
            return message;

        }).blockLast(); // 阻塞线程！！！
    }


    /**
     * 完全异步的，不阻塞线程.
     *
     * @param messageHistory
     * @return 不遵守 SSE 协议规范, 直接返回结果！
     */
    @PostMapping(value = "/async/stream")
    public ResponseBodyEmitter stream2(@RequestBody MessageHistory messageHistory) {

        sessionService.setResponseHeader();

        final ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        // 模型名称, 按需处理
        String model = messageHistory.getModel();

        Message[] messages = sessionService.getMessages(messageHistory);
        streamingChatClient.stream(messages)
                .subscribe(
                        message -> {
                            try {
                                // emitter.send("data:" + message + "\n\n"); 这是遵守 SSE 规范的写法.这里不遵守。
                                emitter.send(message);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        error -> emitter.completeWithError(error),
                        () -> emitter.complete()
                );

        return emitter;
    }
}
