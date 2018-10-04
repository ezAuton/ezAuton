package com.team2502.ezauton.recorder;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class RecorderTest
{
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
//        Recorder recorder = new Recorder();
//        Colour colour = new Colour(0, 0, 0, 1);
//        RobotState robotState = new RobotState(0, 0, 0, 2, 3);

//        List<RobotState> robotStates = Arrays.asList(new RobotState(1, 2, 3, 4, 5), new RobotState(0, 23, 1, 6, 8), new RobotState(123, 4, 127, 3, 1));

        ObjectMapper objectMapper = new ObjectMapper();

//        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(robotStates);

//        System.out.println(json);

        File file = new File("test.txt");

//        RobotState[] states = objectMapper.readValue(json, RobotState[].class);
//        for(RobotState state : states)
//        {
//            System.out.println(state);
//        }
    }
}
