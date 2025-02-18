package ru.practicum.configuration;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString()); // Сохраняем Duration как ISO-8601 строку
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        if (in == null || in.peek() == null) {
            return null;
        }
        return Duration.parse(in.nextString()); // Парсим строку в объект Duration
    }
}