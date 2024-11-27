package com.whapi.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ContactMessage
        extends BaseMessage {
    private String vcard;
    private String name;

    @Builder
    public ContactMessage(String to, String quoted, Integer ephemeral, String edit, List<String> mentions,
                          Boolean viewOnce, String vcard, String name) {
        super(to, quoted, ephemeral, edit, mentions, viewOnce);
        this.name = name;
        this.vcard = vcard;
    }
}
