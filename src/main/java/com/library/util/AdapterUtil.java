package com.library.util;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdapterUtil {
    static public class SimpleBooleanPropertyAdapter implements JsonSerializer<SimpleBooleanProperty>, JsonDeserializer<SimpleBooleanProperty> {

        @Override
        public JsonElement serialize(SimpleBooleanProperty src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.get());
        }
    
        @Override
        public SimpleBooleanProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SimpleBooleanProperty(json.getAsBoolean());
        }
    }


    static public class SimpleIntegerPropertyAdapter implements JsonSerializer<SimpleIntegerProperty>, JsonDeserializer<SimpleIntegerProperty> {

        @Override
        public JsonElement serialize(SimpleIntegerProperty src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.get());
        }

        @Override
        public SimpleIntegerProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SimpleIntegerProperty(json.getAsInt());
        }
    }

    static public class SimpleStringPropertyAdapter implements JsonSerializer<SimpleStringProperty>, JsonDeserializer<SimpleStringProperty> {

        @Override
        public JsonElement serialize(SimpleStringProperty src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.get());
        }

        @Override
        public SimpleStringProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SimpleStringProperty(json.getAsString());
        }
    }

    static public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString());
        }
    }

    static public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString());
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json == null || json.isJsonNull() ? LocalDateTime.now() : LocalDateTime.parse(json.getAsString());
        }
    }
}