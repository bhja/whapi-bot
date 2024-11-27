package com.whapi.bot.service;

import com.whapi.bot.model.webhook.Message;

import java.util.List;

public interface ListenerService {
    void processMessages(List<Message> payload);
}
