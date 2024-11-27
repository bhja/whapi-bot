package com.whapi.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whapi.bot.config.Config;
import com.whapi.bot.model.BaseMessage;
import com.whapi.bot.model.ContactMessage;
import com.whapi.bot.model.TextMessage;
import com.whapi.bot.model.webhook.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListenerServiceImpl
        implements ListenerService {
    public static final MediaType JSON = MediaType.parse("application/json");
    private final static Map<String, String> responseMap = Map.of("help", "text1", "command", "text2", "image", "Send " +
                                                                          "image",
                                                                  "video", "Send video", "document", "Send document",
                                                                  "unknown", "Unknown message");

    private final static Map<String, String> fileMap = Map.of("video", "file_example_MP4_480_1_5MG.mp4", "image", "file_example_JPG_100kB.jpg", "vcard", "sample-vcard.txt",
                                                              "document", "file-example_PDF_500_kB.pdf");
    private final Config config;
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    /**
     * Handles the processing of the request and sends the response based on the type.
     *
     * @param messages {@link MessagePayload}
     */
    @Override
    public void processMessages(List<MessagePayload> messages) {
        try {
            for (MessagePayload message : messages) {
                //Ignore message from self i.e. in this case the bot responses.
                if (message.isFromMe())
                    continue;
                String chatId = message.getChatId();
                String response;
                if (message.getType().equals("text")) {
                    String body = ((String) message.getText().get("body")).toLowerCase();
                    switch (body) {
                        //If help/command based on the lookup response is sent to the user.
                        case "help", "command" -> {
                            TextMessage txtMessage =
                                    TextMessage.builder().to(chatId).body(responseMap.get(body)).build();
                            response = postJson(txtMessage, "messages/text");
                        }
                        //If the text is a multimedia request ,  process the request for multimedia
                        case "video", "image", "document" -> {
                            //Request body contains the file contents and the MediaType based on the filename extension
                            try (InputStream stream = getFile(body)) {
                                RequestBody fileBody =
                                        RequestBody.create(MediaType.parse(MediaTypeFactory.
                                                                                   getMediaType(fileMap.get(body)).toString()), stream.readAllBytes());
                                MultipartBody multipartBody = new
                                        MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("to", chatId).addFormDataPart(
                                                                       "media"
                                                                       , fileMap.get(body), fileBody)
                                                               .addFormDataPart("caption",
                                                                                responseMap.get(body)).build();
                                response = postMultipart(multipartBody, "messages/" + body);
                            }
                        }
                        // A contact card is requested by user. Send the sample response
                        case "vcard" -> {
                            try (InputStream stream = getFile(body)) {
                                ContactMessage contactMessage =
                                        ContactMessage.builder().name("Whapi test").to(chatId).vcard(new String(stream.readAllBytes())).build();
                                response = postJson(contactMessage, "messages/contact");
                            }
                        }
                        default -> {
                            //If the command is unknown , we leave it as is.
                            TextMessage message0 =
                                    TextMessage.builder().to(chatId).body(responseMap.get("unknown")).build();
                            response = postJson(message0, "messages/text");

                        }
                    }
                    log.info("Response received [{}]", response);
                } else {
                    // Only text type is handled for the message. Rest of them are not yet handled
                    log.warn("{} type not handled", message.getType());
                }
            }
        } catch (Exception e) {
            log.error("Could not process the message " + e.getMessage());
        }
    }

    /**
     * Dispatches the json message to the sender
     *
     * @param message  Message to be sent
     * @param endpoint endpoint mapping
     * @return String
     */
    protected String postJson(BaseMessage message, String endpoint) {
        try {
            RequestBody body = RequestBody.create(JSON, objectMapper.writeValueAsString(message));
            return execute(body, endpoint);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes the rest call and only if the response is success the response string is returned else
     * exception is thrown.
     *
     * @param requestBody request
     * @param endpoint    endpoint to invoke
     * @return String response
     * @throws IOException thrown when file not found
     */
    protected String execute(RequestBody requestBody, String endpoint) throws IOException {
        Request request = new Request.Builder()
                .url(String.format("%s/%s", config.getWhapiApiUrl(), endpoint))
                .post(requestBody).addHeader("Authorization", "Bearer " + config.getApiToken())
                .build();
        Response response = okHttpClient.newCall(request).execute();
        try (ResponseBody response0 = response.body()) {
            if (response.isSuccessful()) {
                return response0.string();
            } else {
                log.warn("Issue with the request . Response code {} , reason  {}", response.code(), response0);
                throw new RuntimeException(response.message());
            }
        }
    }

    /**
     * Dispatches the multimedia response to the sender
     *
     * @param body     {@link MultipartBody}
     * @param endpoint String
     * @return String response
     */
    protected String postMultipart(MultipartBody body, String endpoint) {
        try {
            return execute(body, endpoint);
        } catch (IOException e) {
            log.error("Could not process the request " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /**
     * Returns the file as filestream
     *
     * @param type String
     * @return {@link InputStream}
     */
    protected InputStream getFile(String type) {
        return getClass().getClassLoader().getResourceAsStream(config.getFilePath() + File.separator + fileMap.get(type));
    }

}
