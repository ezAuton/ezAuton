package com.github.ezauton.recorder.serializers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.ezauton.core.pathplanning.IPathSegment;
import com.github.ezauton.core.pathplanning.Path;

import java.io.IOException;
import java.util.List;

public class PathSerializer extends StdSerializer<Path> {
    public PathSerializer() {
        this(null);
    }

    public PathSerializer(Class<Path> t) {
        super(t);
    }

    @Override
    public void serialize(Path value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        try {
            List<IPathSegment> pathSegments = value.getPathSegments();
            double length = value.getLength();

            jgen.writeStartObject();
            jgen.writeFieldName("pathSegments");
            jgen.writeObject(pathSegments);
            jgen.writeFieldName("length");
            jgen.writeNumber(length);
            jgen.writeEndObject();
        } catch (Exception e) {
            throw new JsonGenerationException("Could not serialize Path", e);
        }
    }
}
