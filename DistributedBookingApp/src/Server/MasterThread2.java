package Server;

import Entities.AccommodationRoom;
import Entities.MessageData;
import Entities.Task;
import Worker.*;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;


public class MasterThread2 implements Runnable{
    private Socket client;
    private  final ArrayList<Worker> workersList;
    private HashMap<Integer, Task> taskMap;
    HashMap<Integer, ArrayList<AccommodationRoom>> completedTasks;


    public MasterThread2(Socket client, ArrayList<Worker> workersList, HashMap<Integer,
                        Task> taskMap, HashMap<Integer, ArrayList<AccommodationRoom>> completedTasks)
    {
        this.client = client;
        this.workersList = workersList;
        this.taskMap = taskMap;
        this.completedTasks = completedTasks;
    }

    @Override
    public void run(){

        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());


            // read client category
            String line = (String)objectIn.readObject();


            if (line.equals("manager")){
                // MANAGER interface method call
                runManagerInterface(objectOut, objectIn);

            }
            else if (line.equals("renter")){
                // RENTER interface method call
                runRenterInterface(objectOut, objectIn);

            }
            else if (line.equals("reducer")){
                // REDUCER interface method call
                //TODO
            }
        }

        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }


    public void runManagerInterface(ObjectOutputStream objectOut, ObjectInputStream objectIn){

        try {

            // Receive Task from Client
            Task task = (Task)objectIn.readObject();

            // Add task to the queue of pending tasks
            taskMap.put((int)task.getTaskID(), task);

            // If method of Task is "insert"
            if ( task.getMethod().equals("insert")) {

                // Choosing Worker Node based on hashCode
                long hashCode = (long) task.getJson().get("roomName").hashCode();
                int WorkerId = (int) hashCode % workersList.size();
                task.setWorkerID(WorkerId);
                for (Worker w : workersList){
                    if (w.getId() == task.getWorkerID()){
                        // Send insert Task to specific Worker based on Hashing
                       Socket socket = new Socket("localhost", w.getPort());
                       ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                       out.writeObject(task);
                       out.close();
                    }
                }
            }
            else {

                // sockets connecting Master with Workers
                HashMap<Socket, ObjectOutputStream> sockets = connectWithWorkers();

                // Send task to all the workers that Master is connected to
                sendTaskToWorkers(task, sockets);
            }

            ArrayList<AccommodationRoom> results;
            while(true) {
                synchronized (completedTasks) {

                    // Wait for a new entry on the completedTasks HashMap
                    completedTasks.wait();

                    // If there is a key == taskID then break loop
                    if (completedTasks.get((int) task.getTaskID()) != null) {
                        results = completedTasks.get((int) task.getTaskID());
                        completedTasks.remove((int)task.getTaskID());
                        break;
                    }
                }
            }
            objectOut.writeObject(results);

        }
        catch (IOException | ClassNotFoundException | InterruptedException e){
            e.printStackTrace();
        }
    }


    public void runRenterInterface(ObjectOutputStream objectOut, ObjectInputStream objectIn){
        try {
            Boolean isManager = false;
            objectOut.writeObject("Welcome to renter interface...");
            objectOut.writeObject("Choose one of the following options.");
            objectOut.writeObject("1. Filter the rooms. \n2. Make a reservation \n3. Rate a room");
            objectOut.writeObject(null);
            objectOut.flush();

            // Instantiate new Task object with unique ID
            Task task = new Task();

            // Establishes connection with Workers
            HashMap<Socket, ObjectOutputStream> sockets = connectWithWorkers();

            String response =(String) objectIn.readObject();
            switch (response) {
                case "1" -> {

                    // The method that Master will request from Workers
                    String methodRequest = "filter";
                    task.setMethod(methodRequest);

                    // Asking client to insert filters
                    objectOut.writeObject("Choose filters : ");
                    insertFilters(task, objectOut, objectIn);


                    // Sends the Task to every worker Master is aware of
                    sendTaskToWorkers(task, sockets);


                }
                case "2" -> {
                    // Send
                    sendTaskToWorkers(task, sockets);

                    objectOut.writeObject("We are completing your reservation...");
                }
                case "3" -> {
                    // Asking user to input the name of the room he wishes to review
                    objectOut.writeObject("Enter your review (0-5)");
                    objectOut.writeObject(null);
                    objectOut.flush();

                    // Reading user's input and setting the filter
                    task.setRoomName((String)objectIn.readObject());

                    // Asking user to input his review of the room
                    objectOut.writeObject("Enter your review (0-5)");
                    objectOut.writeObject(null);
                    objectOut.flush();

                    // method
                    task.setMethod("rate");

                    // Reading user's input and setting the filter
                    task.setStarsFilter((int)objectIn.readObject());

                    // Send
                    sendTaskToWorkers(task, sockets);


                    objectOut.writeObject("We are uploading your review...");


                }
            }
        }
        catch (IOException| ClassNotFoundException e){
            e.printStackTrace();
        }
    }


    // Establish connection with each Worker that Master has.
    public HashMap<Socket, ObjectOutputStream> connectWithWorkers(){

        // Map of all the connections with Workers and the OutputStreams to communicate with each one
        HashMap<Socket, ObjectOutputStream> sockets = new HashMap<>();
        Socket socket;
        try {
            for (Worker w : workersList) {

                // Establish a new connection with each of the Workers
                socket = new Socket("localhost", w.getPort());
                sockets.put(socket, new ObjectOutputStream(socket.getOutputStream()));
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return sockets;
    }



    public void sendTaskToWorkers(Task task,HashMap<Socket, ObjectOutputStream> sockets){
        // add task to the queue of pending tasks
        this.taskMap.put((int)task.getTaskID(), task);

        // Send task to each Worker
        for (Socket socket : sockets.keySet()) {
            try {
                // Sends to the worker the method that client requests
                sockets.get(socket).writeObject(task);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void insertFilters(Task task, ObjectOutputStream objectOut, ObjectInputStream objectIn){

        try {

            objectOut.writeObject("Enter the location (if you don't have a preference, type 'null'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setAreaFilter((String)objectIn.readObject());

            objectOut.writeObject("Enter the first date in form of dd-mm-yyyy (if you don't have a preference, type 'null'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setDateFirst((String)objectIn.readObject());

            objectOut.writeObject("Enter the last date in form of dd-mm-yyyy (if you don't have a preference, type 'null'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setDateLast((String)objectIn.readObject());

            objectOut.writeObject("Enter the number of people (if you don't have a preference, type '0'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setCapacityFilter( Integer.parseInt((String)objectIn.readObject()));

            objectOut.writeObject("Enter the price (if you don't have a preference, type '0'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setPriceFilter( Integer.parseInt((String)objectIn.readObject()));

            objectOut.writeObject("Enter the number of rating stars of the room (if you don't have a preference, type '0'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setStarsFilter( Integer.parseInt((String)objectIn.readObject()));

            objectOut.writeObject("Filtering...");
            objectOut.flush();


        }
        catch (IOException | ClassNotFoundException | ParseException e){
            e.printStackTrace();
        }
    }
}
