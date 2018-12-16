package org.github.ezauton.ezauton.serializers;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.github.ezauton.ezauton.utils.InterpolationMap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class InterpolationMapSerializer extends StdSerializer<InterpolationMap>
{
    public InterpolationMapSerializer() {
        this(null);
    }
    public InterpolationMapSerializer(Class<InterpolationMap> t)
    {
        super(t);
    }

    @Override
    public void serialize(InterpolationMap value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException
    {
        try
        {
            Field tableField = InterpolationMap.class.getDeclaredField("table");
            tableField.setAccessible(true);
            Map<Double, Double> underlyingTable = (Map<Double, Double>) tableField.get(value);
            jgen.writeObject(underlyingTable);
        }
        catch(Exception e)
        {
            throw new JsonGenerationException("Could not serialize InterpolationMap", e);
        }
    }
}
