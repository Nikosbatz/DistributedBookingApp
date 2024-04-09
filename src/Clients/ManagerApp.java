package Clients;

import Entities.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.Socket;
import java.io.*;
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

            System.out.println("Welcome ...\nChoose an option: \n 1. Insert a new room\n 2. Show all listings\n 3. Exit");
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
                    System.out.println("Waiting for Server...");
                    masterResponse = (String)objectIn.readObject();
                    break;

                // Show Current manager's rooms
                case "2":

                    // Set task Attributes
                    task.setManagerID(1);
                    task.setMethod("show");

                    // Waiting for Master response
                    System.out.println("Waiting for Server...");
                    masterResponse = (String)objectIn.readObject();
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
