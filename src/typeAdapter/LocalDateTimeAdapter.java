package typeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    public void write(JsonWriter out, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            out.nullValue();
        } else {
            out.value(localDateTime.toString());
        }
    }

    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String stringLocalDateTime = in.nextString();
        return LocalDateTime.parse(stringLocalDateTime);
    }
}