package Worker;

import Entities.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
                    if ((task.getAreaFilter() == "null" || room.getArea().equals(task.getAreaFilter())
                            && (task.getCapacityFilter() == 0 || room.getCapacity() >= task.getCapacityFilter())
                            && (task.getPriceFilter() == 0 || room.getPrice() <= task.getPriceFilter())
                            && (task.getStarsFilter() == 0 || room.getStars() >= task.getStarsFilter())
                            && (task.getDateFirst() == null || task.getDateLast() == null || room.isAvailiable(task.getDateFirst(), task.getDateLast()))
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


    public static void bookARoom(Task task, AccommodationRoom room) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate availDateFirst;
        LocalDate availDateLast;
        synchronized (room) {
            for (String date : room.getAvailiableDates().keySet()) {
                availDateFirst = LocalDate.parse(date,formatter);
                availDateLast = LocalDate.parse(room.getAvailiableDates().get(date),formatter);

                if (availDateFirst.isEqual(task.getDateFirst()) && availDateLast.isEqual(task.getDateLast())) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailiableDates().remove(availDateFirst);
                }
                else if (availDateFirst.compareTo(task.getDateFirst()) == 0 && task.getDateLast().isBefore(availDateLast)) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailiableDates().remove(availDateFirst);
                    room.getAvailiableDates().put(task.getDateLast().plusDays(1).format(formatter), availDateLast.format(formatter));

                }
                else if (task.getDateFirst().isAfter(availDateFirst) && availDateLast.isEqual(task.getDateLast())) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailiableDates().remove(availDateFirst);
                    room.getAvailiableDates().put(availDateFirst.format(formatter), task.getDateFirst().minusDays(1).format(formatter));
                }
                else if (task.getDateFirst().isAfter(availDateFirst) && task.getDateLast().isBefore(availDateLast)) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailiableDates().remove(availDateFirst);
                    room.getAvailiableDates().put(availDateFirst.format(formatter), task.getDateFirst().minusDays(1).format(formatter));
                    room.getAvailiableDates().put(task.getDateLast().plusDays(1).format(formatter), availDateLast.format(formatter));
                }
            }
        }
    }
}
