package ru.practicum.configuration;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.format(FORMATTER)); // Сохраняем LocalDateTime как строку ISO-8601
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in == null || in.peek() == null) {
            return null;
        }
        return LocalDateTime.parse(in.nextString(), FORMATTER); // Парсим строку ISO-8601 в LocalDateTime
    }
}