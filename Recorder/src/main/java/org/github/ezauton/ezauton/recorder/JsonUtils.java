package org.github.ezauton.ezauton.recorder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.github.ezauton.ezauton.serializers.InterpolationMapSerializer;
import org.github.ezauton.ezauton.utils.InterpolationMap;

import java.io.IOException;

/**
 * Json utility functions
 *
 * @author Maxwell Harper
 */
public class JsonUtils
{

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static SimpleModule customSerializers = new SimpleModule();

    static
    {
        // dates should be serialized using ISO pattern
        objectMapper.setDateFormat(new ISO8601DateFormat());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//        objectMapper.enableDefaultTyping();

        customSerializers.addSerializer(InterpolationMap.class, new InterpolationMapSerializer());

        objectMapper.registerModule(customSerializers);
    }

    /**
     * @return (JSON) String representation of the object.
     */
    public static String toString(Object o) throws IOException
    {
        return objectMapper.writeValueAsString(o);
    }

    /**
     * @return (JSON) String representation of the object, or null if an error occurred.
     */
    public static String toStringUnchecked(Object o)
    {
        try
        {
            return toString(o);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Turn JSON back into an object
     *
     * @param clazz   A Class<T> representing the type of object the JSON should become (e.g Banana.class)
     * @param jsonStr The JSON data
     * @param <T>     The type of object this data should become (e.g Banana)
     * @return An object of type T containing all the data that the JSON String did
     */
    public static <T> T toObject(Class<T> clazz, String jsonStr)
    {
        try
        {
            return objectMapper.readValue(jsonStr, clazz);
        }
        catch(IOException e)
        {
            // TODO: throw a more appropriate unchecked exception here
            throw new RuntimeException("problem!!!!", e);
        }
    }
}
