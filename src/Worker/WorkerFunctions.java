package Worker;

import Entities.*;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkerFunctions {


    public static void insert(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){

        if (roomsMap.get(task.getManagerID()) == null){
            roomsMap.put(task.getManagerID(), new ArrayList<>());
        }

        roomsMap.get(task.getManagerID()).add(new AccommodationRoom(task.getJson()));
    }


    public static ArrayList<AccommodationRoom> showManagerRooms(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){
        return roomsMap.get(task.getManagerID());
    }



}
