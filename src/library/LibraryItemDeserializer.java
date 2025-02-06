package library;

import com.google.gson.*;

import java.lang.reflect.Type;

public class LibraryItemDeserializer implements JsonDeserializer<LibraryItem> {
    @Override
    public LibraryItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String type = obj.get("type").getAsString();
        if (type.equals("Book")) {
            return context.deserialize(json, Book.class);
        } else if (type.equals("Magazine")) {
            return context.deserialize(json, Magazine.class);
        }
        return null;
    }
}
