package typeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {
    public void write(JsonWriter out, Duration duration) throws IOException {
        if (duration == null) {
            out.nullValue();
        } else {
            out.value(duration.toMinutes());
        }
    }

    public Duration read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        long seconds = in.nextLong();
        return Duration.ofMinutes(seconds);
    }
}