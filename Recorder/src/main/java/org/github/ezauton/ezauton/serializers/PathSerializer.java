package org.github.ezauton.ezauton.serializers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.github.ezauton.ezauton.pathplanning.IPathSegment;
import org.github.ezauton.ezauton.pathplanning.Path;
import org.github.ezauton.ezauton.recorder.JsonUtils;
import org.github.ezauton.ezauton.utils.InterpolationMap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class PathSerializer extends StdSerializer<Path>
{
    public PathSerializer() {
        this(null);
    }
    public PathSerializer(Class<Path> t)
    {
        super(t);
    }

    @Override
    public void serialize(Path value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException
    {
        try
        {
            List<IPathSegment> pathSegments = value.getPathSegments();
            double length = value.getLength();

            jgen.writeStartObject();
            jgen.writeFieldName("pathSegments");
            jgen.writeObject(pathSegments);
            jgen.writeFieldName("length");
            jgen.writeNumber(length);
            jgen.writeEndObject();
        }
        catch(Exception e)
        {
            throw new JsonGenerationException("Could not serialize Path", e);
        }
    }
}
