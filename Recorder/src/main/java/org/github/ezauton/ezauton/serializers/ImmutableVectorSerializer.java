package org.github.ezauton.ezauton.serializers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;
import org.github.ezauton.ezauton.utils.InterpolationMap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class ImmutableVectorSerializer extends StdSerializer<ImmutableVector>
{
    public ImmutableVectorSerializer() {
        this(null);
    }
    public ImmutableVectorSerializer(Class<ImmutableVector> t)
    {
        super(t);
    }

    @Override
    public void serialize(ImmutableVector value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException
    {
        try
        {
            jgen.writeStartObject();
            jgen.writeFieldName("elements");
            jgen.writeStartArray();

            value.stream().forEach(n -> {
                try
                {
                    jgen.writeNumber(n);
                }
                catch(IOException e)
                {
                    e.printStackTrace(); // TODO something more meaningful?
                }
            });

            jgen.writeEndArray();
            jgen.writeEndObject();
        }
        catch(Exception e)
        {
            throw new JsonGenerationException("Could not serialize ImmutableVector", e);
        }
    }
}
