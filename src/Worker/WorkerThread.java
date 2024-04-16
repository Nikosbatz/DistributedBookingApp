package Worker;

import Entities.*;

import java.io.*;
import java.net.*;
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

            // Declare the Socket to connect with the reducer
            Socket Reducer;

            // If the task is sent from manager interface
            if (task.getIsManager()){

                switch (task.getMethod()){
                    case "insert":
                        if (task.getWorkerID() == this.WorkerID) {
                            // Return to Master "true" if room inserted successfully else "false"
                            objectOut.writeObject(WorkerFunctions.insert(task, roomsMap));
                        }
                        break;

                    case "show":
                    case "countBookings":
                        // Connects With Reducer
                        Reducer = WorkerFunctions.connectWithReducer();
                        ArrayList<AccommodationRoom> rooms = WorkerFunctions.showManagerRooms(task, roomsMap);
                        WorkerFunctions.sendResultToReducer(Reducer, (int)task.getTaskID(), rooms);
                        break;

                    case "totalBookings":

                        Reducer = WorkerFunctions.connectWithReducer();
                        ArrayList<AccommodationRoom> roomsAll = WorkerFunctions.showAllRooms(roomsMap);
                        WorkerFunctions.sendResultToReducer(Reducer,(int) task.getTaskID(),roomsAll);
                        break;

                    case "updateAvailableDates":
                        if (task.getWorkerID() == this.WorkerID) {
                            // Return to Master "true" if dates updated successfully else "false"
                            objectOut.writeObject(WorkerFunctions.updateAvailableDates(task, roomsMap));
                        }
                        break;

                    default:
                        break;
                }
            }
            // If the task is sent from renter interface
            else {
                switch (task.getMethod()) {
                    case "filter":

                        // Connects With Reducer
                        Reducer = WorkerFunctions.connectWithReducer();
                        ArrayList<AccommodationRoom> filteredRooms = WorkerFunctions.filterRooms(task, roomsMap);
                        WorkerFunctions.sendResultToReducer(Reducer, (int) task.getTaskID(), filteredRooms);
                        break;


                    case "rate":

                        // Check if the room about to be reviewed belongs to this Worker's storage
                        for(int managerId: roomsMap.keySet()){
                            ArrayList<AccommodationRoom> rooms = roomsMap.get(managerId);
                            for (AccommodationRoom room: rooms){
                                // If room is stored in this Worker
                                if (room.getName().equals(task.getRoomName())){

                                    // This block is synchronized
                                    synchronized (room) {

                                        // add the review to the room
                                        objectOut.writeObject(room.addReview(task.getStarsFilter()));

                                    }
                                }
                            }
                        }
                        break;


                    case "book":
                        /* Iterates through the values (ArrayList) of every key (managerID)
                           to check if the room with name = task.getRoomName() is in this Worker   */
                        for(int managerId: roomsMap.keySet()){
                            ArrayList<AccommodationRoom> rooms = roomsMap.get(managerId);
                            for (AccommodationRoom room: rooms) {
                                // If room is stored in this Worker
                                if (room.getName().equals(task.getRoomName())) {

                                    objectOut.writeObject(WorkerFunctions.book(task, room));

                                }
                            }
                        }
                        break;

                    case "showAllRooms":

                        Reducer = WorkerFunctions.connectWithReducer();
                        ArrayList<AccommodationRoom> roomsAll = WorkerFunctions.showAllRooms(roomsMap);
                        WorkerFunctions.sendResultToReducer(Reducer,(int) task.getTaskID(),roomsAll);
                        break;

                    default:
                        break;
                }
            }


        }catch (IOException | ClassNotFoundException  e){
            e.printStackTrace();
        }

    }
}

