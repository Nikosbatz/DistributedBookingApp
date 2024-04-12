package Worker;

import Entities.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.text.ParseException;


public class WorkerFunctions {


    public synchronized static void insert(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){

        if (roomsMap.get(task.getManagerID()) == null){
            roomsMap.put(task.getManagerID(), new ArrayList<>());
        }

        AccommodationRoom room = new AccommodationRoom(task.getJson());
        ArrayList<AccommodationRoom> list = roomsMap.get(task.getManagerID());
        list.add(room);

        System.out.println("list size: " + list.size());

    }


    public static void updateAvailableDates(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){

        ArrayList<AccommodationRoom> list = roomsMap.get(task.getManagerID());
        for (AccommodationRoom room: list){
            if (room.getName().equals(task.getRoomName())){
                room.setAvailableDates(task.getDateFirst(), task.getDateLast());
            }
        }

    }




    public static ArrayList<AccommodationRoom> filterRooms(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap) throws java.text.ParseException {
        ArrayList<AccommodationRoom> filteredRooms = new ArrayList<>();

        synchronized (roomsMap) {
            for (ArrayList<AccommodationRoom> rooms : roomsMap.values()) {
                for (AccommodationRoom room : rooms) {

                    // This block is synchronized
                    if ((task.getAreaFilter().equals("null") || room.getArea().equals(task.getAreaFilter()))
                            && (task.getCapacityFilter() == 0 || room.getCapacity() >= task.getCapacityFilter())
                            && (task.getPriceFilter() == 0 || room.getPrice() <= task.getPriceFilter())
                            && (task.getStarsFilter() == 0 || room.getStars() >= task.getStarsFilter())
                            && (room.isAvailable(task.getDateFirst(), task.getDateLast()))
                    ) {
                        filteredRooms.add(room);
                    }
                }
            }
        }

        return filteredRooms;
    }


    public static ArrayList<AccommodationRoom> showManagerRooms(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){
        System.out.println("list to return size: " + roomsMap.get(task.getManagerID()).size());
        return roomsMap.get(task.getManagerID());
    }

    public static ArrayList<AccommodationRoom> showAllRooms(HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){
        ArrayList<AccommodationRoom> allRooms = new ArrayList<>();
        for (ArrayList<AccommodationRoom> managerRooms : roomsMap.values()) {
            allRooms.addAll(managerRooms);
        }
        System.out.println(allRooms.size() + "----------");
        return allRooms;
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
