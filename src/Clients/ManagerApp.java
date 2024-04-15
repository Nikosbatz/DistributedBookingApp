package Clients;

import Entities.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.Socket;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
public class ManagerApp {



    public static void main(String[] args){
        try {
            Socket socket = new Socket("localhost", 1234);


            // Reading objects from Server
            ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());

            // Writing objects to Server
            ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

            // Instantiating Client input object
            Scanner scannerIn = new Scanner(System.in);

            // Declaring to Master to use the manager interface
            objectOut.writeObject("manager");

            boolean isRunning = true;
            while (isRunning) {
                System.out.println("Welcome ...\nChoose an option: \n 1. Insert a new room\n 2. Show all listings\n 3. Update available dates\n 4. See how many bookings each room has\n 5. Exit");
                System.out.print("Enter your choice: ");
                String response = scannerIn.nextLine();

                // Instantiating new Task object
                Task task = new Task();
                task.setIsManager(true);


                switch (response) {

                    // Insert Room
                    case "1":

                        // Create JSON object from local file
                        JSONObject json = insertJSONFile(scannerIn);

                        // Set task Attributes
                        task.setManagerID(1);
                        task.setMethod("insert");
                        task.setJson(json);

                        // Send Task to Master
                        objectOut.writeObject(task);

                        // Waiting for Master response
                        System.out.println("Waiting for Server...\n");

                        // Print to client the operation's confirmation
                        if ((boolean)objectIn.readObject()){
                            System.out.println("Room inserted successfully!");
                        }
                        else{
                            System.out.println("Room insertion failed...");
                        }
                        break;

                    // Show Current manager's rooms
                    case "2":

                        // Set task Attributes
                        task.setManagerID(1);
                        task.setMethod("show");

                        // Send Task to Master
                        objectOut.writeObject(task);

                        // Waiting for Master response
                        System.out.println("Waiting for Server...");

                        ArrayList<AccommodationRoom> result = (ArrayList<AccommodationRoom>) objectIn.readObject();
                        for (AccommodationRoom room : result) {
                            System.out.println(room.toString());
                        }
                        break;

                    // update available room dates
                    case "3":
                        task.setMethod("updateAvailableDates");
                        task.setManagerID(1);

                        System.out.print("Insert the room name: ");
                        task.setRoomName(scannerIn.nextLine());

                        // Asking user to insert the desired dates
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        System.out.print("Insert initial availability date (dd-mm-yy): ");
                        task.setDateFirst(LocalDate.parse(scannerIn.nextLine(), formatter));
                        System.out.print("Insert final availability date (dd-mm-yy): ");
                        task.setDateLast(LocalDate.parse(scannerIn.nextLine(), formatter));

                        // Send Task to Master
                        objectOut.writeObject(task);

                        System.out.print("Waiting for Server...");
                        // Print to client the operation's confirmation
                        if ((boolean)objectIn.readObject()){
                            System.out.println("Dates updated successfully!");
                        }
                        else{
                            System.out.println("Dates update failed...");
                        }

                        break;

                    case "4":
                        task.setMethod("countBookings");
                        task.setManagerID(1);

                        // Send Task to Master
                        objectOut.writeObject(task);

                        System.out.print("Waiting for Server...");

                        ArrayList<AccommodationRoom> bookingCount = (ArrayList<AccommodationRoom>) objectIn.readObject();
                        for (AccommodationRoom room : bookingCount) {
                            System.out.println("\nRoom: " + room.getName() + " has in total: "  + room.getBookedDates().size() + " bookings.");
                        }
                        break;


                    // Exit the manager interface
                    case "5":

                        task.setMethod("exit");
                        // Inform Master that the client is killing the process.
                        objectOut.writeObject(task);
                        isRunning = false;
                        System.out.println("Exiting manager interface...");
                        break;

                    // Default option
                    default:
                        objectOut.writeObject("Invalid option selected. Please try again.");
                        objectOut.writeObject(null);
                        objectOut.flush();
                        break;
                }
            }
        }
        catch (IOException | ClassNotFoundException |NullPointerException e){
            e.printStackTrace();
        }
    }

    public static JSONObject  insertJSONFile (Scanner scannerIn){
        try {
            String jsonData = "";

            System.out.print("Enter .JSON file name: ");
            String jsonPath = scannerIn.nextLine();

            // Reads the json file from the selected directory
            BufferedReader jsonFile = new BufferedReader(new FileReader("C:\\Users\\nikos\\Documents\\GitHub\\DistributedBookingApp_main\\src\\assets\\" + jsonPath + ".json"));
            String line;

            // read each line of the .json file
            while ((line = jsonFile.readLine()) != null) {

                // concatenating the data from the .json file
                jsonData += line;
            }

            // Instantiate JSON parser
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonData);

            return json;



        }
        catch (IOException | ParseException e){
            e.printStackTrace();
            return null;
        }



    }




}
