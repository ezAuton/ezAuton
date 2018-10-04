package com.team2502.guitools.ppsimulator;

/**
 * Represents all the data for an entire recording. All data for sub-recordings can be accessed by name
 */
public interface IData
{
    /**
     *
     * @param name The name of the recording
     * @return The JSON for that recording
     */
    String getJSON(String name);
}
