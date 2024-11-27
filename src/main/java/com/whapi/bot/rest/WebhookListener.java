package com.whapi.bot.rest;

import com.whapi.bot.model.webhook.WebhookPayload;
import com.whapi.bot.service.ListenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookListener {
    private final ListenerService service;

    /**
     * Listens to the whatsapp events. The service processes the request and sends back a response as per defined
     * criteria
     *
     * @param events {@link WebhookPayload}
     */
    @PostMapping("/messages")
    @Async
    public void receive(@RequestBody WebhookPayload events) {
        log.info("Events received \n [{}] \n", events);
        service.processMessages(events.getMessages());
    }


}
