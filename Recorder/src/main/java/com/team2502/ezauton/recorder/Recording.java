package com.team2502.ezauton.recorder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Recording
{

    Map<String,ISubRecording> recordingMap = new HashMap<>();

    void add(ISubRecording subRecording)
    {
        Optional.ofNullable(subRecording).ifPresent(r -> recordingMap.put(r.getName(),r));
    }

    String getJSON()
    {
        recordingMap.entrySet()
                    .forEach(entry -> {

                    });
    }
//    /**
//     *
//     * @param name The name of the recording
//     * @return The JSON for that recording
//     */
//    String getJSON(String name);
}
