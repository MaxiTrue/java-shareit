package ru.practicum.shareit.comment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.baseclient.BaseClient;
import ru.practicum.shareit.comment.dto.CommentDto;

@Component
public class CommentClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public CommentClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> create(String path, long userId, CommentDto commentDto) {
        return post(path, userId, commentDto);
    }


}
