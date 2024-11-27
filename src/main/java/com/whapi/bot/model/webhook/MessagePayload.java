package com.whapi.bot.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@ToString
public class MessagePayload {
    private String id;
    @JsonProperty("from_me")
    private boolean fromMe;

    @JsonProperty("type")
    private String type;
    @JsonProperty("chat_id")
    private String chatId;
    @JsonProperty("timestamp")
    private long timestamp;
    @JsonProperty("text")
    private Map<String, Object> text;
    @JsonProperty("from_name")
    private String fromName;
    @JsonProperty
    private String from;
    @JsonProperty
    private String source;
    @JsonProperty("device_id")
    private String deviceId;
}
