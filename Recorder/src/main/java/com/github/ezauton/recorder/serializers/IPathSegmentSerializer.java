package com.github.ezauton.recorder.serializers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.ezauton.core.pathplanning.PathSegment;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IPathSegmentSerializer extends StdSerializer<PathSegment> {
    public IPathSegmentSerializer() {
        this(null);
    }

    public IPathSegmentSerializer(Class<PathSegment> t) {
        super(t);
    }

    @Override
    public void serialize(PathSegment value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        try {
            jgen.writeStartObject();

            jgen.writeStringField("@class", value.getClass().getName());

            List<Field> fields = new ArrayList<>();
            for (Class clazz = value.getClass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
                fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            }
            for (Field field : fields) {
                field.setAccessible(true);
                jgen.writeFieldName(field.getName());
                jgen.writeObject(field.get(value));
            }

            jgen.writeEndObject();
        } catch (Exception e) {
            throw new JsonGenerationException("Could not serialize " + value.getClass().getName(), e);
        }
    }
}
