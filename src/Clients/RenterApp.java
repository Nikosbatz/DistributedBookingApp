package Clients;

import Entities.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.Socket;
import java.io.*;
import java.util.*;
public class RenterApp {

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost", 1234);


            // Reading objects from Server
            ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());

            // Writing objects to Server
            ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

            // Instantiating Client input object
            Scanner scannerIn = new Scanner(System.in);

            // Declaring to Master to use the manager interface
            objectOut.writeObject("renter");

            System.out.println("Welcome\nChoose an option: \n1. Filter the rooms. \n2. Make a booking \n3. Rate a room");
            String response = scannerIn.nextLine();

            // Instantiating new Task object
            Task task = new Task();

            String masterResponse;

            switch (response) {
                case "1" -> {

                    // Setting task method
                    task.setMethod("filter");

                    // Asking client to insert filters
                    insertFilters(task,scannerIn);

                    // Send Task to Master
                    objectOut.writeObject(task);

                    // Waiting for Master response
                    System.out.println("Waiting for Server...");
                    masterResponse = (String)objectIn.readObject();


                }
                case "2" -> {
                    //TODO
                }
                case "3" -> {
                    // Asking user to input the name of the room he wishes to review
                    System.out.println("Which room do you wish to leave a review for?");

                    // Reading user's input and setting the filter
                    task.setRoomName(scannerIn.nextLine());

                    // Asking user to input his review of the room
                    System.out.println("Enter your review (0-5)");
                    task.setStarsFilter(Integer.parseInt(scannerIn.nextLine()));

                    // Set method
                    task.setMethod("rate");

                    // Send Task to Master
                    objectOut.writeObject(task);

                    // Waiting for Master response
                    System.out.println("Waiting for Server...");
                    masterResponse = (String)objectIn.readObject();



                }
            }



        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }


    public static void insertFilters(Task task, Scanner scannerIn){



        System.out.println("Enter the location (if you don't have a preference, type 'null'): ");


        task.setAreaFilter(scannerIn.nextLine());


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

        System.out.println("Enter the number of people (if you don't have a preference, type 'null'): ");


        task.setCapacityFilter( Integer.parseInt(scannerIn.nextLine()));

        System.out.println("Enter the price (if you don't have a preference, type 'null'): ");

        task.setPriceFilter( Integer.parseInt(scannerIn.nextLine()));

        System.out.println("Enter the number of rating stars of the room (if you don't have a preference, type 'null'): ");

        task.setStarsFilter( Integer.parseInt(scannerIn.nextLine()));

        System.out.println("Filtering...");


    }


}
