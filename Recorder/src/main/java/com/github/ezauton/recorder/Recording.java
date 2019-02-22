package com.github.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ezauton.core.localization.Updateable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Recording implements ISubRecording, Updateable {
    private static int recordingCounter = 0;
    @JsonProperty("recordingData")
    private Map<String, ISubRecording> recordingMap = new HashMap<>();
    @JsonProperty("name")
    private String name;

    public Recording() {
        name = "Recording_" + recordingCounter++;
    }

    public Recording(ISubRecording... recordings) {
        this("Recording_" + recordingCounter++, recordings);
    }

    public Recording(String name, ISubRecording... recordings) {
        this.name = name;
        for (ISubRecording recording : recordings) {
            recordingMap.put(recording.getName(), recording);
        }
    }

    public Recording addSubRecording(ISubRecording subRecording) {
        Optional.ofNullable(subRecording).ifPresent(r -> recordingMap.put(r.getName(), r));
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toJson() {
        return JsonUtils.toStringUnchecked(this);
    }

    public Map<String, ISubRecording> getRecordingMap() {
        return recordingMap;
    }

    @Override
    public boolean update() {
        boolean ret = false;
        for (ISubRecording recording : recordingMap.values()) {
            if (recording instanceof Updateable) {
                // If the update method returns true
                if (((Updateable) recording).update()) {
                    ret = true;
                }
            }
        }
        return ret;
    }

    public void save(String name) throws IOException {
        String homeDir = System.getProperty("user.home");
        java.nio.file.Path filePath = Paths.get(homeDir, ".ezauton", name);
        save(filePath);
    }

    public void save(Path filePath) throws IOException {
        Files.createDirectories(filePath.getParent());

        BufferedWriter writer = Files.newBufferedWriter(filePath);
        String json = this.toJson();

        writer.write(json);

        writer.close();

        JsonUtils.toObject(Recording.class, json);
    }
}
