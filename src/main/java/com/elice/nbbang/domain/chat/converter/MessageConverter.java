package com.elice.nbbang.domain.chat.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import com.elice.nbbang.domain.chat.dto.Message;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class MessageConverter implements AttributeConverter<List<Message>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // 중요: 날짜를 문자열로 변환

    @Override
    public String convertToDatabaseColumn(List<Message> messages) {
        try {
            return objectMapper.writeValueAsString(messages);
        } catch(JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public List<Message> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Message.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to messages", e);
        }
    }
}
