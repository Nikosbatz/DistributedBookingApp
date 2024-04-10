/*
package Server;


import Entities.Task;
import Worker.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;


public class MasterThread implements Runnable{
    private Socket client;
    private  final ArrayList<Worker> workersList;
    private HashMap<Integer, Task> taskMap;


    public MasterThread(Socket client, ArrayList<Worker> workersList, HashMap<Integer, Task> taskMap){
        this.client = client;
        this.workersList = workersList;
        this.taskMap = taskMap;
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
            Boolean isManager = true;
            // Prints initial messages to client
            objectOut.writeObject("Welcome to Manager interface...");
            objectOut.writeObject("Choose an option: \n 1. Insert a new room\n 2. Show all listings\n 3. Exit");
            objectOut.writeObject(null);
            objectOut.flush();

            // Instantiate new Task object with unique taskID
            Task task = new Task();
            task.setIsManager(true);

            // sockets connecting Master with Workers
            HashMap<Socket, ObjectOutputStream> sockets = connectWithWorkers();

            // Read the operation that client wants the server to do.
            String response = (String) objectIn.readObject();

            // Switch for different responses of the client.
            switch (response) {

                // Insert Room
                case "1":
                    objectOut.writeObject("Please insert the JSON for the new room:");
                    objectOut.flush();

                    // Reads JSON data from the client.
                    MessageData message = (MessageData) objectIn.readObject();



                    // Choosing Worker Node based on hashCode
                    long hashCode = (long) message.json.get("roomName").hashCode();
                    int nodeId =(int) hashCode % workersList.size();

                    // Set task Attributes
                    task.setManagerID(1);
                    task.setMethod("insert");
                    task.setJson(message.json);
                    task.setWorkerID(nodeId);

                    // Add task to the queue of pending tasks
                    taskMap.put((int)task.getTaskID(), task);

                    // Send task to all the workers that Master is connected to
                    sendTaskToWorkers(task, sockets);

                    objectOut.writeObject("Waiting for room to be inserted...");
                    objectOut.flush();
                    break;

                // Show Current manager's rooms
                case "2":

                    int managerID = 1;
                    // Instantiate new Task object with unique taskID
                    task.setManagerID(managerID);
                    task.setMethod("show");

                    // Send task to all the workers that Master is connected to
                    sendTaskToWorkers(task, sockets);
                    break;

                // Exit the manager interface
                case "3":

                    objectOut.writeObject("Exiting manager interface...");
                    objectOut.writeObject(null);
                    objectOut.flush();
                    break;

                // Default option
                default:
                    objectOut.writeObject("Invalid option selected. Please try again.");
                    objectOut.writeObject(null);
                    objectOut.flush();
                    break;
            }
        }
        catch (IOException | ClassNotFoundException e){
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
                    //TODO
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


            */
/*objectOut.writeObject("Enter the first date (if you don't have a preference, type 'null'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            //task.setAreaFilter((String)objectIn.readObject());

            //LocalDate dateStart = LocalDate.parse(tempDate);
            objectOut.writeObject("Enter the last date (if you don't have a preference, type 'null'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setAreaFilter((String)objectIn.readObject());*//*


            //LocalDate dateEnd = LocalDate.parse(tempDate);

            objectOut.writeObject("Enter the number of people (if you don't have a preference, type 'null'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setCapacityFilter( Integer.parseInt((String)objectIn.readObject()));

            objectOut.writeObject("Enter the price (if you don't have a preference, type 'null'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setPriceFilter( Integer.parseInt((String)objectIn.readObject()));

            objectOut.writeObject("Enter the number of rating stars of the room (if you don't have a preference, type 'null'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setStarsFilter( Integer.parseInt((String)objectIn.readObject()));

            objectOut.writeObject("Filtering...");
            objectOut.flush();


        }
        catch (IOException| ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
*/
