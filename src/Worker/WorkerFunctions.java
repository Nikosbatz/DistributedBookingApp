package Worker;

import Entities.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;

public class WorkerFunctions {


    public synchronized static void insert(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){

        if (roomsMap.get(task.getManagerID()) == null){
            roomsMap.put(task.getManagerID(), new ArrayList<>());
        }

        roomsMap.get(task.getManagerID()).add(new AccommodationRoom(task.getJson()));
    }



    public static ArrayList<AccommodationRoom> filterRooms(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap) throws java.text.ParseException {
        ArrayList<AccommodationRoom> filteredRooms = new ArrayList<>();
        synchronized (roomsMap) {
            for (ArrayList<AccommodationRoom> rooms : roomsMap.values()) {
                for (AccommodationRoom room : rooms) {
                    // This block is synchronized
                    if ((task.getAreaFilter().equals("null") || room.getArea().equals(task.getAreaFilter())
                            && (task.getCapacityFilter() == 0 || room.getCapacity() >= task.getCapacityFilter())
                            && (task.getPriceFilter() == 0 || room.getPrice() <= task.getPriceFilter())
                            && (task.getStarsFilter() == 0 || room.getStars() >= task.getStarsFilter())
                            && (task.getDateFirst() == null || task.getDateLast() == null || room.isAvailable(task.getDateFirst(), task.getDateLast()))
                    )) {
                        filteredRooms.add(room);
                    }
                }
            }
        }

        return filteredRooms;
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
            HashMap<Integer, ArrayList<AccommodationRoom>> map = new HashMap<>();
            map.put(taskID, result);
            System.out.println("TaskID: " + taskID);
            objectOut.writeObject(map);

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void bookAroom(Task task, AccommodationRoom room) throws ParseException {
        LocalDate availDateFirst;
        LocalDate availDateLast;
        synchronized (room) {
            for (LocalDate date : room.getAvailableDates().keySet()) {
                availDateFirst = date;
                availDateLast = room.getAvailableDates().get(date);

                if (availDateFirst.isEqual(task.getDateFirst()) && availDateLast.isEqual(task.getDateLast())) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailableDates().remove(availDateFirst);
                }
                else if (availDateFirst.isEqual(task.getDateFirst()) && task.getDateLast().isBefore(availDateLast)) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailableDates().remove(availDateFirst);
                    room.getAvailableDates().put(task.getDateLast().plusDays(1), availDateLast);

                }
                else if (task.getDateFirst().isAfter(availDateFirst) && availDateLast.isEqual(task.getDateLast())) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailableDates().remove(availDateFirst);
                    room.getAvailableDates().put(availDateFirst, task.getDateFirst().minusDays(1));
                }
                else if (task.getDateFirst().isAfter(availDateFirst) && task.getDateLast().isBefore(availDateLast)) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailableDates().remove(availDateFirst);
                    room.getAvailableDates().put(availDateFirst, task.getDateFirst().minusDays(1));
                    room.getAvailableDates().put(task.getDateLast().plusDays(1), availDateLast);
                }
            }
        }
    }
}
