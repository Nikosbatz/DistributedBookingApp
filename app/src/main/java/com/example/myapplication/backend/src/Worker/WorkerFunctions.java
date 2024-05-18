package com.example.myapplication.backend.src.Worker;

import com.example.myapplication.backend.src.Entities.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;
import java.time.LocalDate;
import java.text.ParseException;


public class WorkerFunctions {


    public static boolean insert(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){

        // Synchronize roomsMap structure to safely insert room
        synchronized (roomsMap) {
            if (roomsMap.get(task.getManagerID()) == null) {
                roomsMap.put(task.getManagerID(), new ArrayList<>());
            }
            System.out.println(task.getImageData());
            AccommodationRoom room = new AccommodationRoom(task.getJson(), task.getImageData());
            ArrayList<AccommodationRoom> list = roomsMap.get(task.getManagerID());
            return (list.add(room));
        }
    }


    public static boolean updateAvailableDates(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){

        ArrayList<AccommodationRoom> list = roomsMap.get(task.getManagerID());
        for (AccommodationRoom room: list){
            if (room.getName().equals(task.getRoomName())) {
                // Synchronize AccommodationRoom object to safely update its attributes
                synchronized (room) {
                    return room.setAvailableDates(task.getDateFirst(), task.getDateLast());
                }
            }
        }
        return false;
    }




    public static ArrayList<AccommodationRoom> filterRooms(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap) {
        ArrayList<AccommodationRoom> filteredRooms = new ArrayList<>();

        // Iterate through all the rooms and return those with the desired filters
        for (ArrayList<AccommodationRoom> rooms : roomsMap.values()) {

            for (AccommodationRoom room : rooms) {

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


        return filteredRooms;
    }


    public static ArrayList<AccommodationRoom> showManagerRooms(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){
        return roomsMap.get(task.getManagerID());
    }

    public static ArrayList<AccommodationRoom> showAllRooms(HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap){
        ArrayList<AccommodationRoom> allRooms = new ArrayList<>();
        for (ArrayList<AccommodationRoom> managerRooms : roomsMap.values()) {
            allRooms.addAll(managerRooms);
        }
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


    public static boolean book(Task task, AccommodationRoom room) {
        LocalDate availDateFirst;
        LocalDate availDateLast;
        // Synchronize AccommodationRoom object to safely modify the BookedDates
        synchronized (room) {
            Set<LocalDate> availiableDatesSet = room.getAvailableDates().keySet();

            for (LocalDate date :availiableDatesSet) {
                availDateFirst = date;
                availDateLast = room.getAvailableDates().get(date);

                if (availDateFirst.isEqual(task.getDateFirst()) && availDateLast.isEqual(task.getDateLast())) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailableDates().remove(availDateFirst);
                    return true;
                }
                else if (availDateFirst.isEqual(task.getDateFirst()) && task.getDateLast().isBefore(availDateLast)) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailableDates().remove(availDateFirst);
                    room.getAvailableDates().put(task.getDateLast().plusDays(1), availDateLast);
                    return true;

                }
                else if (task.getDateFirst().isAfter(availDateFirst) && availDateLast.isEqual(task.getDateLast())) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailableDates().remove(availDateFirst);
                    room.getAvailableDates().put(availDateFirst, task.getDateFirst().minusDays(1));
                    return true;
                }
                else if (task.getDateFirst().isAfter(availDateFirst) && task.getDateLast().isBefore(availDateLast)) {
                    room.getBookedDates().put(task.getDateFirst(),task.getDateLast());
                    room.getAvailableDates().remove(availDateFirst);
                    room.getAvailableDates().put(availDateFirst, task.getDateFirst().minusDays(1));
                    room.getAvailableDates().put(task.getDateLast().plusDays(1), availDateLast);
                    return true;
                }
            }
        }
        return false;
    }


    public static ArrayList<AccommodationRoom> showRoomsInDateRange(Task task, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap) {
        ArrayList<AccommodationRoom> filteredRooms = new ArrayList<>();
        LocalDate dateOne = task.getDateFirst();
        LocalDate dateTwo = task.getDateLast();
        // Iterate through all the rooms and return those booked in the desired date range
        for (ArrayList<AccommodationRoom> rooms : roomsMap.values()) {

            for (AccommodationRoom room : rooms) {
                if (roomIsBookedInDateRange(room,dateOne,dateTwo)){
                    filteredRooms.add(room);
                }
            }
        }

        return filteredRooms;
    }

    private static boolean roomIsBookedInDateRange(AccommodationRoom room, LocalDate dateOne, LocalDate dateTwo) {
        int count = 0;
        for (LocalDate date : room.getBookedDates().keySet()){
            if ((date.isEqual(dateOne)||date.isAfter(dateOne)) && ((room.getBookedDates().get(date).isEqual(dateTwo))||room.getBookedDates().get(date).isBefore(dateTwo))){
                count += 1;
            }
        }
        room.setNoOfBookingsInRange(count);
        if (count == 0) {
            return false;
        }else{
            return true;
        }
    }


}
