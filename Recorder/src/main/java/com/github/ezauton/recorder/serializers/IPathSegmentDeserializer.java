package com.github.ezauton.recorder.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.ezauton.core.pathplanning.IPathSegment;

import java.io.IOException;


public class IPathSegmentDeserializer extends StdDeserializer<IPathSegment>
{
    public IPathSegmentDeserializer()
    {
        this(null);
    }

    public IPathSegmentDeserializer(Class<IPathSegment> t)
    {
        super(t);
    }


    @Override
    public IPathSegment deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
    {

//        System.out.println("node.getClass() = " + node.getClass());
        ObjectMapper mp = new ObjectMapper();

        mp.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObjectNode node = jp.getCodec().readTree(jp);
        try
        {
            Class<IPathSegment> clazz = (Class<IPathSegment>) Class.forName(node.get("@class").textValue());
            node.remove("@class");
            return mp.readValue(node.toString(), clazz);
        }
        catch(Exception e)
        {
            System.err.println(node.toString());
            throw new IOException("Cannot deserialize alleged IPathSegment", e);
        }

    }

}