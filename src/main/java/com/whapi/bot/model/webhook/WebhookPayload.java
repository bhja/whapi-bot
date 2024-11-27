package com.whapi.bot.model.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Represents the webook message payload
 * <pre>
 * <code>
 *     {
 *     "messages": [
 *         {
 *             "id": "PrA_p7qJLvsHZas-gL7VjWNnag",
 *             "from_me": false,
 *             "type": "text",
 *             "chat_id": "XXXXXXXXXX@s.whatsapp.net",
 *             "timestamp": 1732611456,
 *             "source": "web",
 *             "device_id": 4,
 *             "text": {
 *                 "body": "hi"
 *             },
 *             "from": "XXXXXXXXXX",
 *             "from_name": "~ user"
 *         }
 *     ],
 *     "channel_id": "channel-id"
 * }
 *
 * </code></pre>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class WebhookPayload {
    @JsonProperty("channel_id")
    private String channelId;
    private List<MessagePayload> messages;
}
