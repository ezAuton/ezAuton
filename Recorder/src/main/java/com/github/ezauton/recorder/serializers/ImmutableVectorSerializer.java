package com.github.ezauton.recorder.serializers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.ezauton.conversion.ScalarVector;

import java.io.IOException;

public class ImmutableVectorSerializer extends StdSerializer<ScalarVector> {
    public ImmutableVectorSerializer() {
        this(null);
    }

    public ImmutableVectorSerializer(Class<ScalarVector> t) {
        super(t);
    }

    @Override
    public void serialize(ScalarVector value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        try {
            jgen.writeStartObject();
            jgen.writeFieldName("elements");
            jgen.writeStartArray();

            value.stream().forEach(n -> {
                try {
                    jgen.writeNumber(n);
                } catch (IOException e) {
                    e.printStackTrace(); // TODO something more meaningful?
                }
            });

            jgen.writeEndArray();
            jgen.writeEndObject();
        } catch (Exception e) {
            throw new JsonGenerationException("Could not serialize ScalarVector", e);
        }
    }
}
