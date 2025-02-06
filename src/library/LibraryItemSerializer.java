package library;

import com.google.gson.*;

import java.lang.reflect.Type;

public class LibraryItemSerializer implements JsonSerializer<LibraryItem> {
    @Override
    public JsonElement serialize(LibraryItem src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = context.serialize(src).getAsJsonObject();
        if (src instanceof Book) {
            obj.addProperty("type", "Book");
        } else if (src instanceof Magazine) {
            obj.addProperty("type", "Magazine");
        }
        return obj;
    }
}
