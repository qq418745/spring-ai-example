package com.coalai.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/simple")
@Slf4j
public class SimpleStreamController {

    private final OpenAiChatClient chatClient;

    private final StreamingChatClient streamingChatClient;


    /**
     * 同步的 call
     *
     * @param message
     * @return
     */
    @GetMapping(value = "/call")
    public String call(@RequestParam(value = "message", defaultValue = "说个笑话") String message) {
        return chatClient.call(new Prompt(message))
                .getResults().stream()
                .map(r -> r.getOutput().getContent())
                .collect(Collectors.joining());
    }


    /**
     * 异步简单 Stream 写法 1
     *
     * @param message
     * @return
     */
    @GetMapping(value = "/stream")
    public Flux<String> stream(@RequestParam(value = "message", defaultValue = "说个猫咪笑话") String message) {
        return chatClient.stream(new Prompt(message))
                .flatMap(chatResponse -> Flux.fromIterable(chatResponse.getResults()))
                .flatMap(content -> {
                            String c = content.getOutput().getContent();
                            return c != null ? Mono.just(c) : Mono.empty();
                        }
                );
    }

    /**
     * 异步简单 Stream 写法 2
     *
     * @param message
     * @return
     */
    @GetMapping(value = "/stream2")
    public Flux<String> stream2(@RequestParam(value = "message", defaultValue = "说个狗子笑话") String message) {
        return streamingChatClient.stream(message);
    }


}