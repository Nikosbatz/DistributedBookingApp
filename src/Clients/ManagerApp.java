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

            // Initializing formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            boolean isRunning = true;
            while (isRunning) {
                System.out.println("""
                        Choose an option:\s
                         1. Insert a new room
                         2. Show all rooms
                         3. Update available room dates
                         4. Bookings of your rooms
                         5. See how many total bookings were made per region in a specific time period
                         6. Exit""");
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

                        System.out.print("Insert initial availability date (dd-mm-yy): ");
                        task.setDateFirst(LocalDate.parse(scannerIn.nextLine(), formatter));
                        System.out.print("Insert final availability date (dd-mm-yy): ");
                        task.setDateLast(LocalDate.parse(scannerIn.nextLine(), formatter));

                        // Send Task to Master
                        objectOut.writeObject(task);

                        System.out.println("Waiting for Server...");
                        // Print to client the operation's confirmation
                        if ((boolean)objectIn.readObject()){
                            System.out.println("Dates updated successfully!");
                        }
                        else{
                            System.out.println("Dates update failed...");
                        }

                        break;

                    // Show the bookings of each room that manager owns
                    case "4":
                        task.setMethod("countBookings");
                        task.setManagerID(1);

                        // Send Task to Master
                        objectOut.writeObject(task);

                        System.out.println("Waiting for Server...");

                        ArrayList<AccommodationRoom> bookingCount = (ArrayList<AccommodationRoom>) objectIn.readObject();

                        for (AccommodationRoom room : bookingCount) {
                            System.out.println("\nRoom: " + room.getName() + " has in total: " + room.getBookedDates().size() + " bookings.");
                            System.out.println("The booked dates of the room: " + room.getName() + " are: ");
                            String str = "";
                            for (LocalDate tempDate : room.getBookedDates().keySet()) {
                                String date1 = tempDate.format(formatter);
                                String date2 = room.getBookedDates().get(tempDate).format(formatter);
                                str += date1 + " -> " + date2 + "\n";
                            }
                            System.out.println(str);
                        }
                        break;

                    case "5":
                        task.setMethod("totalBookings");
                        task.setManagerID(1);

                        // Asking user to insert the desired dates
                        System.out.print("Insert first date of the period (dd-mm-yy): ");
                        LocalDate dateOne = LocalDate.parse(scannerIn.nextLine(), formatter);
                        System.out.print("Insert final date of the period (dd-mm-yy): ");
                        LocalDate dateTwo = LocalDate.parse(scannerIn.nextLine(), formatter);


                        // Send Task to Master
                        objectOut.writeObject(task);

                        System.out.println("Waiting for Server...");


                        ArrayList<AccommodationRoom> rooms = (ArrayList<AccommodationRoom>) objectIn.readObject();

                        printAllBookings(rooms,dateOne,dateTwo);

                        break;


                    // Exit the manager interface
                    case "6":

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
    private static void printAllBookings(ArrayList<AccommodationRoom> rooms, LocalDate dateOne, LocalDate dateTwo) {
        HashMap<String,Integer> totalBookings = new HashMap<>();
        for (AccommodationRoom room : rooms) {
            int count = 0;
            for (LocalDate date : room.getBookedDates().keySet()){

                if ((date.isEqual(dateOne)||date.isAfter(dateOne)) && ((room.getBookedDates().get(date).isEqual(dateTwo))||room.getBookedDates().get(date).isBefore(dateTwo))){

                    count += 1;
                }
            }

            if (totalBookings.containsKey(room.getArea())) {
                totalBookings.replace(room.getArea(), totalBookings.get(room.getArea()) + count);
            }
            else{
                totalBookings.put(room.getArea(), count);
            }
        }

        for (String area: totalBookings.keySet()) {
            String value = String.valueOf(totalBookings.get(area));
            System.out.println(area + " : " + value);
        }
    }




}
