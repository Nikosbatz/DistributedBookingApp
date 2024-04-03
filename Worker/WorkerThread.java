package Worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import Entities.Task;
import Entities.AccommodationRoom;
import org.json.simple.parser.ParseException;
import java.util.Scanner;


public class WorkerThread implements Runnable{

    Socket client;
    HashMap<Integer,AccommodationRoom> roomsMap;

    public WorkerThread(Socket client, HashMap<Integer,AccommodationRoom> roomsList ){
        this.roomsMap = roomsList;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            System.out.println("WorkerThread Started!!!");
            ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());

            // Expecting Task object
            Task task = (Task) objectIn.readObject();

            // Determine action based on the task details
            switch (task.getMethod()) {
                case "insert":

                    System.out.println("Attempting to insert object");
                    // Assuming insert function can handle task directly or modify to pass relevant fields
                    WorkerFunctions.insert();
                    break;
                case "filter":
                    // Handle filtering logic here
                    break;
                // Add cases for other methods as necessary
            }
        } catch (IOException | ClassNotFoundException | ParseException e) {
            e.printStackTrace();
        }
    }

}