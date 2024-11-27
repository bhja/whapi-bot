package com.whapi.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.whapi.bot.config.Config;
import com.whapi.bot.model.BaseMessage;
import com.whapi.bot.model.ContactMessage;
import com.whapi.bot.model.TextMessage;
import com.whapi.bot.model.webhook.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
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
    private final Config config;
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    /**
     * Handles the processing of the request and sends the response based on the type.
     *
     * @param messages {@link Message}
     */
    @Override
    public void processMessages(List<Message> messages) {
        try {
            for (Message message : messages) {
                //Ignore message from self i.e. in this case the bot responses.
                if (message.isFromMe())
                    continue;
                String chatId = message.getChatId();
                String response ;
                if (message.getType().equals("text")) {
                    //Extract the message sent irrespective of the character case.
                    String body = ((String) message.getText().get("body")).toLowerCase();
                    String caption = responseMap.get(body);
                    switch (body) {
                        //If help/command based on the lookup response is sent to the user.
                        case "help", "command" -> {
                            TextMessage txtMessage =
                                    TextMessage.builder().to(chatId).body(caption).build();
                            response = postJson(txtMessage, "messages/text");
                        }
                        //If the text is a multimedia request ,  process the request for multimedia
                        case "video", "image", "document" -> {
                                File file = getFile(body);
                                //Request body contains the file contents and the MediaType based on the filename extension
                                try(InputStream stream= new FileInputStream(file)) {
                                    RequestBody fileBody =
                                            RequestBody.create(MediaType.parse(MediaTypeFactory.getMediaType(file.getName()).toString()), stream.readAllBytes());
                                    MultipartBuilder multipartBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                                                                                           .addFormDataPart("to",
                                                                                                            chatId)
                                                                                           .addFormDataPart("media", file.getName(), fileBody)
                                                                                           .addFormDataPart("caption",
                                                                                                            caption);
                                    response = postMultipart(multipartBody, "messages/" + body);
                                }
                        }
                        case "vcard" -> {
                            File file = getFile(body);
                            try(InputStream stream = new FileInputStream(file)){
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
     * @param message Message to be sent
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
     * @param endpoint endpoint to invoke
     * @return String response
     * @throws IOException thrown when file not found
     */
    protected String execute(RequestBody requestBody, String endpoint) throws IOException {
        Request request = new Request.Builder()
                .url(String.format("%s/%s", config.getWhapiApiUrl(), endpoint))
                .post(requestBody).addHeader("Authorization", "Bearer " + config.getApiToken())
                .build();
        Response response = okHttpClient.newCall(request).execute();
        try(ResponseBody response0 = response.body()){
            if (response.isSuccessful()) {
                return response0.string();
            } else {
                log.warn("Issue with the request . Response code {} , reason  {}", response.code(),response0);
                throw new RuntimeException(response.message());
            }
        }
    }

    /**
     * Dispatches the multimedia response to the sender
     *
     * @param builder {@link MultipartBuilder}
     * @param endpoint String
     * @return String response
     */
    protected String postMultipart(MultipartBuilder builder, String endpoint) {
        try {
            return execute(builder.build(), endpoint);
        } catch (IOException e) {
            log.error("Could not process the request " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the file based on the type requested. Eg: video,image ,vcard.
     *
     * @param type String type of file
     * @return {@link File}
     * @throws IOException  an exception is thrown if file processing issues /type is not handled
     */
    protected File getFile(String type) throws IOException {
        return switch (type) {
            case "image" -> new ClassPathResource(config.getFilePath() + File.separator + "file_example_JPG_100kB" +
                                                          ".jpg").getFile();
            case "video" ->
                    new ClassPathResource(config.getFilePath() + File.separator + "file_example_MP4_480_1_5MG.mp4").getFile();
            case "vcard" -> new ClassPathResource(config.getFilePath() + File.separator + "sample-vcard.txt").getFile();
            case "document" ->
                    new ClassPathResource(config.getFilePath() + File.separator + "file-example_PDF_500_kB.pdf").getFile();
            default -> throw new IOException("Not handled file type " + type);
        };
    }

}
