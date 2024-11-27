package com.whapi.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextMessage
        extends BaseMessage {
    @Nonnull
    private String body;
    @JsonProperty("typing_time")
    private Integer typingTime;
    @JsonProperty("no_link_preview")
    private Boolean noLinkPreview;

    @Builder
    public TextMessage(String to, String quoted, Integer ephemeral, String edit, List<String> mentions, Boolean viewOnce,
                       Boolean noLinkPreview, String body, Integer typingTime) {
        super(to, quoted, ephemeral, edit, mentions, viewOnce);
        this.body = body;
        this.noLinkPreview = noLinkPreview;
        this.typingTime = typingTime;
    }

}
