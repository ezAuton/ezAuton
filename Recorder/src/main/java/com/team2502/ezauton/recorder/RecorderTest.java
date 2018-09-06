package com.team2502.ezauton.recorder;

import java.io.*;
import java.util.Collections;

public class RecorderTest
{
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        Recorder recorder = new Recorder();
        Colour colour = new Colour(0, 0, 0, 1);
        RobotState robotState = new RobotState(0, 0, 0, 2, 3);

        RecordFrame recordFrame = new RecordFrame(Collections.emptyList(), colour, robotState);

        recorder.add(recordFrame);
        recorder.add(recordFrame);
        recorder.add(recordFrame);
        recorder.add(recordFrame);

        File file = new File("test.txt");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
        outputStream.writeObject(recorder);

        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
        Recorder readObject= ((Recorder) inputStream.readObject());
        System.out.println(readObject.getRecordFrames().get(0).getTime());
    }
}
