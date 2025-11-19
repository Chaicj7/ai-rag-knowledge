package com.chaicj.http;

import com.chaicj.api.IAiService;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/ollama")
public class OllamaController implements IAiService {

    @Autowired
    private OllamaChatClient ollamaChatClient;

    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    @Override
    public ChatResponse generate(@RequestParam String model, @RequestParam String message) {
        return ollamaChatClient.call(new Prompt(message, OllamaOptions.create().withModel(model)));
    }

    @RequestMapping(value = "/generate_stream", method = RequestMethod.GET)
    @Override
    public Flux<ChatResponse> generateStream(String model, String message) {
        return ollamaChatClient.stream(new Prompt(message, OllamaOptions.create().withModel(model)));
    }
}
