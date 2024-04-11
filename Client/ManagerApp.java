package Client;

import Entities.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.Socket;
import java.io.*;
import java.util.*;
public class ManagerApp {


    private static ObjectInputStream objectIn;
    private static ObjectOutputStream objectOut;
    private static Socket socket;
    public static void main(String[] args){
        try {
            socket = new Socket("localhost", 1234);


            // Reading objects from Server
            objectIn = new ObjectInputStream(socket.getInputStream());

            // Writing objects to Server
            objectOut = new ObjectOutputStream(socket.getOutputStream());

            // Instantiating Client input object
            Scanner scannerIn = new Scanner(System.in);

            // Declaring to Master to use the manager interface
            objectOut.writeObject("manager");
            boolean isRunning=true;
            while (isRunning) {
                System.out.println("Welcome ...\nChoose an option: \n 1. Insert a new room\n 2. Show all listings\n 3. Exit");
                System.out.print("Enter your choice: ");
                String response = scannerIn.nextLine();

                // Instantiating new Task object
                Task task = new Task();

                String masterResponse;

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

                    // Exit the manager interface
                    case "3":
                        task.setMethod("exit");
                        objectOut.writeObject(task);
                        System.out.println("Exiting manager interface...");
                        isRunning = false;
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
        }finally {
            closeResources();
        }
    }

    private static void closeResources(){
        try{
            if(objectOut!= null) objectOut.close();
            if(objectIn != null) objectIn.close();
            if(socket!=null) socket.close();
        }catch (IOException e){
            System.err.println("Error when closing resources: "+e.getMessage());
        }
    }

    public static JSONObject  insertJSONFile (Scanner scannerIn){
        try {
            String jsonData = "";

            System.out.print("Enter .JSON file name: ");
            String jsonPath = scannerIn.nextLine();

            // Reads the json file from the selected directory
            BufferedReader jsonFile = new BufferedReader(new FileReader("C:\\Users\\Sotir\\Downloads\\DistributedBookingApp-v-Batz\\DistributedBookingApp-v-Batz\\src\\assets\\" + jsonPath + ".json"));
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