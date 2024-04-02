package Server;

import Entities.MessageData;
import Worker.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class MasterThread implements Runnable{
    private Socket client;
    private  final ArrayList<Worker> workerslist;


    MasterThread(Socket client, ArrayList<Worker> workersList){
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
            String line = ((MessageData)objectIn.readObject()).data;
            System.out.println(line);

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
                //line = in.readLine();
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
            MessageData response = (MessageData) objectIn.readObject();

            // Switch for different responses of the client.
            switch (response.data) {

                // Insert Room
                case "1":
                    objectOut.writeObject("Please insert the JSON for the new room:");
                    objectOut.writeObject(null);
                    objectOut.flush();

                    // Reads JSON data from the client.
                    MessageData message = (MessageData) objectIn.readObject();
                    System.out.println(message.json);
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


        System.out.println("asdasdasdasd");
        // Establishes connection with Workers
        ArrayList<Socket> sockets = connectWithWorkers();



    }


    public void runRenterInterface(ObjectOutputStream objectOut, ObjectInputStream objectIn){
        try {
            Boolean isManager = false;
            objectOut.writeObject("Welcome to renter interface...");
            //
            objectOut.writeObject("Choose one of the following options.");
            objectOut.writeObject("1. Filter the rooms. \n2. Make a reservation \n3. Rate a room");
            objectOut.writeObject(null);
            objectOut.flush();


            String response = ((MessageData)objectIn.readObject()).data;
            if (response.equals("1")) {

                // The method that Master will request from Workers
                String methodRequest = "filter";

                // Asking client to insert .json file
                objectOut.writeObject("Choose filters : ");
                objectOut.writeObject(null);
                objectOut.flush();

                // Reads filter data from Client.
                HashMap<String, String> filters = (HashMap<String, String>) objectIn.readObject();


                // Establishes connection with Workers
                ArrayList<Socket> sockets = connectWithWorkers();


                for (Socket socket : sockets) {
                    // Sends to the worker the method that client requests
                    objectOut.writeBoolean(isManager);
                    objectOut.writeObject(methodRequest);
                    objectOut.writeObject(filters);
                }

            }

            else if(response.equals("2")){
                //TODO
            }

            else if(response.equals("3")){
                //TODO
            }



        }
        catch (IOException| ClassNotFoundException e){
            e.printStackTrace();
        }




    }




    // Establish connection with each Worker that Master has.
    public ArrayList<Socket> connectWithWorkers(){

        // List of all the connections with Workers
        ArrayList<Socket> sockets = new ArrayList<Socket>();

        try {
            for (Worker w : workerslist) {

                // Establish a new connection with each of the Workers
                sockets.add(new Socket("localhost", w.getPort()));
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return sockets;
    }


}
