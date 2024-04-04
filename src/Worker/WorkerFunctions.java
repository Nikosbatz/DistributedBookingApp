package Worker;

import Entities.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkerFunctions {


    public synchronized static void insert(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){

        if (roomsMap.get(task.getManagerID()) == null){
            roomsMap.put(task.getManagerID(), new ArrayList<>());
        }

        roomsMap.get(task.getManagerID()).add(new AccommodationRoom(task.getJson()));
    }


    public static ArrayList<AccommodationRoom> showManagerRooms(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){
        return roomsMap.get(task.getManagerID());
    }

    public static Socket connectWithReducer() {
        try {
            return new Socket("localhost", 1235);
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

    public static void sendResultToReducer(Socket Reducer, int taskID, ArrayList<AccommodationRoom> result) {
        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(Reducer.getOutputStream());
            objectOut.writeObject(new HashMap<Integer, ArrayList<AccommodationRoom>>().put(taskID, result));

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
