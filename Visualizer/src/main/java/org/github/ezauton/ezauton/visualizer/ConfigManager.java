package org.github.ezauton.ezauton.visualizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to ease the handling of files
 */
public class ConfigManager
{

    /**
     * The {@link File} object representing the file
     */
    private final Path path;
    private final Map<String, String> values = new HashMap<>();

    public ConfigManager(Path path)
    {
        this.path = path;
    }

    public ConfigManager(String path)
    {
        this(Paths.get(path));
    }

    public void load()
    {
        try
        {
            Files.lines(path)
                 .map(String::trim) // trim whitespace
                 .filter(line -> line.length() > 0 && line.charAt(0) != '#') // Don't look at comments
                 .map(line -> line.split("=")) // split up config key and value
                 .filter(strings -> strings.length == 2) // if the resulting array (key,value), it is WRONG
                 .forEach(strings -> values.put(strings[0], strings[1]));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public int getInt(String path)
    {
        String val = values.get(path);
        return Integer.parseInt(val);
    }

    public double getDouble(String path)
    {
        String val = values.get(path);
        return Double.parseDouble(val);
    }

    public String getString(String path)
    {
        return values.get(path);
    }


}


