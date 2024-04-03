package Server;

import Entities.MessageData;
import Entities.Task;
import Worker.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class MasterThread implements Runnable{
    private Socket client;
    private  final ArrayList<Worker> workerslist;


    MasterThread(Socket client, ArrayList<Worker> workersList, HashMap<Integer, Task> taskMap){
        this.client = client;
        this.workerslist = workersList;
    }

    @Override
    public void run(){

        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());


            objectOut.writeObject("Hello are you a renter or a manager?");
            objectOut.writeObject("press (a) for manager OR (b) for renter");
            objectOut.flush();

            // read client response
            String line = (String)objectIn.readObject();

            while (line != null){
                if (line.equals("a")){
                    // Manager interface method call
                    runManagerInterface(objectOut, objectIn);
                    break;
                }
                else{
                    // renter interface method call
                    runRenterInterface(objectOut, objectIn);
                    break;
                }
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
            objectOut.writeObject("Welcome to manager interface. Choose an option: \n 1. Insert a new room\n 2. Show all listings\n 3. Exit");
            objectOut.writeObject(null);
            objectOut.flush();


            // Read the operation that client wants the server to do.
            String response = (String) objectIn.readObject();

            // Switch for different responses of the client.
            switch (response) {

                // Insert Room
                case "1":
                    objectOut.writeObject("Please insert the JSON for the new room:");
                    objectOut.writeObject(null);
                    objectOut.flush();

                    // Reads JSON data from the client.
                    MessageData message = (MessageData) objectIn.readObject();

                    // Instantiate new Task object with unique taskID
                    Task task = new Task();
                    task.setMethod("insert");
                    task.setJson(message.json);

                    break;

                // Show Current manager's rooms
                case "2":
                    // Show all listings

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



        // Establishes connection with Workers
        HashMap<Socket, ObjectOutputStream> sockets = connectWithWorkers();



    }


    public void runRenterInterface(ObjectOutputStream objectOut, ObjectInputStream objectIn){
        try {
            Boolean isManager = false;
            objectOut.writeObject("Welcome to renter interface...");
            objectOut.writeObject("Choose one of the following options.");
            objectOut.writeObject("1. Filter the rooms. \n2. Make a reservation \n3. Rate a room");
            objectOut.writeObject(null);
            objectOut.flush();


            String response =(String) objectIn.readObject();
            switch (response) {
                case "1" -> {

                    // Instantiate new Task object with unique ID
                    Task task = new Task();

                    // The method that Master will request from Workers
                    String methodRequest = "filter";
                    task.setMethod(methodRequest);

                    // Asking client to insert filters
                    objectOut.writeObject("Choose filters : ");
                    insertFilters(task, objectOut, objectIn);

                    // Establishes connection with Workers
                    HashMap<Socket, ObjectOutputStream> sockets = connectWithWorkers();

                    // Sends the Task to every worker Master is aware of
                    sendTaskToWorkers(task, sockets);


                }
                case "2" -> {
                    //TODO
                }
                case "3" -> {
                    //TODO
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
            for (Worker w : workerslist) {

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
    public static void insertFilters(Task task, ObjectOutputStream objectOut, ObjectInputStream objectIn){

        try {

            objectOut.writeObject("Enter the location (if you don't have a preference, type 'null'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setAreaFilter((String)objectIn.readObject());


            /*objectOut.writeObject("Enter the first date (if you don't have a preference, type 'null'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            //task.setAreaFilter((String)objectIn.readObject());

            //LocalDate dateStart = LocalDate.parse(tempDate);
            objectOut.writeObject("Enter the last date (if you don't have a preference, type 'null'): ");
            objectOut.writeObject(null);
            objectOut.flush();
            task.setAreaFilter((String)objectIn.readObject());*/

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
