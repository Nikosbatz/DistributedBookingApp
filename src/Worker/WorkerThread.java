package Worker;

import Entities.*;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;


public class WorkerThread implements Runnable{

    Socket client;
    int WorkerID;
    public HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap;
    public WorkerThread(int WorkerID, Socket client, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap ){
        this.WorkerID = WorkerID;
        this.roomsMap = roomsMap;
        this.client = client;
    }

    public void run(){

        try{
            System.out.println("WorkerThread Started!!!");

            // Instantiating object streams.
            ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());

            // Reads the task from Master
            Task task = (Task) objectIn.readObject();



            System.out.println("method: " + task.getMethod());

            // If the task is sent from manager interface
            if (task.getIsManager()){

                switch (task.getMethod()){
                    case "insert":
                        if (task.getWorkerID() == this.WorkerID) {
                            WorkerFunctions.insert(task, roomsMap);


                        }

                        break;

                    case "show":
                        // Connects With Reducer
                        Socket Reducer = WorkerFunctions.connectWithReducer();
                        ArrayList<AccommodationRoom> rooms = WorkerFunctions.showManagerRooms(task, roomsMap);
                        WorkerFunctions.sendResultToReducer(Reducer, (int)task.getTaskID(), rooms);
                        break;

                    default:
                        break;
                }
            }
            // If the task is sent from renter interface
            else {
                switch (task.getMethod()) {
                    case "filter":
                        try {
                            // Connects With Reducer
                            Socket Reducer = WorkerFunctions.connectWithReducer();
                            ArrayList<AccommodationRoom> filteredRooms = WorkerFunctions.filterRooms(task, roomsMap);
                            WorkerFunctions.sendResultToReducer(Reducer, (int) task.getTaskID(), filteredRooms);
                            break;
                        }
                        catch (java.text.ParseException e){
                            e.printStackTrace();
                        }

                    case "rate":

                        // Check if the room about to be reviewed belongs to this Worker's storage
                        for(int managerId: roomsMap.keySet()){
                            ArrayList<AccommodationRoom> rooms = roomsMap.get(managerId);
                            for (AccommodationRoom room: rooms){
                                // If room is stored in this Worker
                                if (room.getName().equals(task.getRoomName())){
                                    // This block is synchronized
                                    synchronized (this) {
                                        System.out.println("Before adding" + room.getStars());
                                        // add the review to the room
                                        room.addReview(task.getStarsFilter());
                                        System.out.println("After adding" + room.getStars());
                                    }
                                }

                            }
                        }
                        break;

                    case "book":

                        for(int managerId: roomsMap.keySet()){
                            ArrayList<AccommodationRoom> rooms = roomsMap.get(managerId);
                            for (AccommodationRoom room: rooms) {
                                // If room is stored in this Worker
                                if (room.getName().equals(task.getRoomName())) {
                                    WorkerFunctions.bookAroom(task, room);
                                }
                            }
                        }
                        break;

                    default:
                        break;
                }
            }


        }catch (IOException | ClassNotFoundException | ParseException e){
            e.printStackTrace();
        }

    }
}

