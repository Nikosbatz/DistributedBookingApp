package com.example.myapplication.backend.src.Clients;

import com.example.myapplication.backend.src.Entities.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.IOException;
import java.net.Socket;
import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

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

            boolean isRunning = true;
            while (isRunning) {
                /*System.out.println(""
                        Choose an option:\n
                        1. Filter the rooms.\s
                        2. Make a booking\s
                        3. Rate a room\s
                        4. Show all available rooms
                        \n5. Exit"");*/
                System.out.print("Enter your choice: ");
                String response = scannerIn.nextLine();

                // Instantiating new Task object
                Task task = new Task();

                String masterResponse;

                switch (response) {
                    case "1": {

                        // Setting task method
                        task.setMethod("filter");

                        // Asking client to insert filters
                        insertFilters(task, scannerIn);

                        // Send Task to Master
                        objectOut.writeObject(task);

                        // Waiting for Master response
                        System.out.println("Waiting for Server...");

                        ArrayList<AccommodationRoom> result = (ArrayList<AccommodationRoom>) objectIn.readObject();
                        for (AccommodationRoom room : result) {
                            System.out.println(room.toString());
                        }
                        break;
                    }
                    case "2" : {
                        // Asking user to input the name of the room he wishes to book
                        System.out.println("Which room do you wish to book?");

                        // Reading user's input and setting the filter
                        task.setRoomName(scannerIn.nextLine());


                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        // Asking user to input the check-IN date he wishes for
                        System.out.println("Enter the check-in date ( in form of dd-mm-yyyy )");
                        String reply = scannerIn.nextLine();
                        LocalDate dateFirst = LocalDate.parse(reply, formatter);
                        task.setDateFirst(dateFirst);

                        // Asking user to input the check-OUT date he wishes for
                        System.out.println("Enter the check-out date ( in form of dd-mm-yyyy )");
                        reply = scannerIn.nextLine();
                        LocalDate dateLast = LocalDate.parse(reply, formatter);
                        task.setDateLast(dateLast);

                        // Set method
                        task.setMethod("book");

                        // Send Task to Master
                        objectOut.writeObject(task);

                        // Waiting for Master response
                        System.out.println("Waiting for Server...");

                        // Print to client the operation's confirmation
                        if ((boolean)objectIn.readObject()){
                            System.out.println("Your booking has been confirmed!");
                        }
                        else{
                            System.out.println("Booking process failed...");
                        }
                        break;

                    }
                    case "3" : {
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

                        // Print to client the operation's confirmation
                        if ((boolean)objectIn.readObject()){
                            System.out.println("your review inserted successfully!");
                        }
                        else{
                            System.out.println("Review insertion failed...");
                        }
                        break;


                    }
                    case "4" :{
                        task.setMethod("showAllRooms");

                        objectOut.writeObject(task);

                        System.out.println("Waiting for Server...");

                        ArrayList<AccommodationRoom> result = (ArrayList<AccommodationRoom>) objectIn.readObject();
                        for (AccommodationRoom room : result) {
                            System.out.println(room.toString());
                        }
                        break;

                    }
                    case "5" : {
                        task.setMethod("exit");
                        objectOut.writeObject(task);
                        isRunning = false;
                        System.out.println("Exiting renter interface...");
                        break;
                    }
                }
            }


        }
        catch (IOException | ClassNotFoundException | ParseException e){
            e.printStackTrace();
        }
    }


    public static void insertFilters(Task task, Scanner scannerIn) throws ParseException {


        // AREA
        System.out.println("Enter the location (if you don't have a preference, type 'null'): ");
        task.setAreaFilter(scannerIn.nextLine());

        // STARTING DATE
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        System.out.println("Enter the first date in form of dd-mm-yyyy (if you don't have a preference, type 'null'): ");
        String reply = scannerIn.nextLine();
        if (reply.equals("null")){
            task.setDateFirst(null);
        }
        else{
            LocalDate dateFirst = LocalDate.parse(reply, formatter);
            task.setDateFirst(dateFirst);
        }

        // LAST DATE
        System.out.println("Enter the last date in form of dd-mm-yyyy (if you don't have a preference, type 'null'): ");
        reply = scannerIn.nextLine();
        if (reply.equals("null")){
            task.setDateLast(null);
        }
        else{
            LocalDate dateLast = LocalDate.parse(reply, formatter);
            task.setDateLast(dateLast);
        }

        // CAPACITY
        System.out.println("Enter the number of people (if you don't have a preference, type '0'): ");
        task.setCapacityFilter( Integer.parseInt(scannerIn.nextLine()));

        // PRICE
        System.out.println("Enter the price (if you don't have a preference, type '0'): ");
        task.setPriceFilter( Integer.parseInt(scannerIn.nextLine()));

        // RATING
        System.out.println("Enter the number of rating stars of the room (if you don't have a preference, type '0'): ");
        task.setStarsFilter( Integer.parseInt(scannerIn.nextLine()));

        System.out.println("Filtering...");


    }


}
