package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Entities.MessageData;
import Manager.ManagerService;
import Manager.RentalListing;
import Worker.Worker;

public class MasterThread implements Runnable {
    private Socket client;
    private final ArrayList<Worker> workerslist;
    ManagerService managerService;

    MasterThread(Socket client, ArrayList<Worker> workersList, ManagerService managerService) {
        this.client = client;
        this.workerslist = workersList;
        this.managerService = new ManagerService("C:\\Users\\Sotir\\Downloads\\DistributedBookingApp-v-Batz\\DistributedBookingApp-v-Batz\\src\\assets\\room.json");
    }

    @Override
    public void run() {
        try (
            ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());
        ) {
            objectOut.writeObject(new MessageData("Hello are you a renter or a manager?\npress (a) for manager OR (b) for renter"));
            objectOut.flush();

            // read client response
            MessageData initialMessage = (MessageData) objectIn.readObject();
            String line = initialMessage.data;
            System.out.println(line);

            if ("a".equals(line)) {
                // Manager interface method call
                runManagerInterface(objectOut, objectIn);
            } else {
                // Renter interface method call
                runRenterInterface(objectOut, objectIn);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("debug1");
            e.printStackTrace();
        }
    }

    public void runManagerInterface(ObjectOutputStream objectOut, ObjectInputStream objectIn) {
        boolean running = true;
        try {
            while (running) {
                objectOut.writeObject(new MessageData("Welcome to manager interface. Choose an option: \n 1. Insert a new room\n 2. Show all listings\n 3. Exit"));
                objectOut.flush();

                MessageData response = (MessageData) objectIn.readObject();

                switch (response.data) {
                    case "1":
                        objectOut.writeObject(new MessageData("Please insert the JSON for the new room:"));
                        objectOut.flush();

                        // Reads JSON data from the client.
                        MessageData message = (MessageData) objectIn.readObject();
                        addNewRoom(message.data, objectOut);
                        break;
                    case "2":
                        // Show all listings
                        managerService.showInfo();
                        break;
                    case "3":
                        running = false;
                        objectOut.writeObject(new MessageData("Exiting manager interface..."));
                        objectOut.flush();
                        break;
                    default:
                        objectOut.writeObject(new MessageData("Invalid option selected. Please try again."));
                        objectOut.flush();
                        break;
                }
                objectOut.reset();
            }
        } catch (IOException | ClassNotFoundException e) {
            try {
                objectOut.writeObject(new MessageData("An error occurred while processing your request."));
                objectOut.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            running = false;
        }
    }

    private void addNewRoom(String jsonData, ObjectOutputStream objectOut) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonData);

            String roomName = (String) json.get("roomName");
            long noOfPersons = (Long) json.get("noOfPersons");
            String area = (String) json.get("area");
            long stars = (Long) json.get("stars");
            long noOfReviews = (Long) json.get("noOfReviews");
            String roomImage = (String) json.get("roomImage");

            // Instead of reading from a file, directly create a new listing from the JSON data received.
            RentalListing newListing = new RentalListing(roomName, (int) noOfPersons, area, (int) stars, (int) noOfReviews, roomImage);
            managerService.addListing(newListing);
            managerService.showInfo();
            objectOut.writeObject(new MessageData("New room inserted successfully."));
            objectOut.flush();
        } catch (ParseException | IOException e) {
            try {
                objectOut.writeObject(new MessageData("Failed to parse the JSON for the new room."));
                objectOut.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public void runRenterInterface(ObjectOutputStream objectOut, ObjectInputStream objectIn) {
        // Implement the renter interface similarly using object streams
    }

    public ArrayList<Socket> connectWithWorkers() {
        // Your existing implementation
        return new ArrayList<>(); // Placeholder return to prevent compilation error
    }
}
