package com.whapi.bot.service;

import com.whapi.bot.model.webhook.MessagePayload;

import java.util.List;

public interface ListenerService {
    void processMessages(List<MessagePayload> payload);
}
