package Server;

import Entities.AccommodationRoom;
import Entities.Task;
import Worker.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Master {
    public static ArrayList<Worker> workersList = new ArrayList<Worker>();
    public static HashMap<Integer, Task> taskMap = new HashMap<>();
    public static HashMap<Integer, ArrayList<AccommodationRoom>> completedTasks = new HashMap<>();

    public static void main(String[] args){

        // For development phase only -------
        if (args.length == 0) {

            for (int i = 0; i < 1; i++) {
                workersList.add(new Worker());
            }

        }
        // --------------------------------

        // If app is running distributed
        else {

            for (int i = 1; i <= Integer.parseInt(args[0]); i++ ){
                String[] str = args[i].split("\\s+");
                workersList.add(new Worker(str[0], Integer.parseInt(str[1]), Integer.parseInt(str[2])));
            }

        }
        //---------------------------------
        ServerSocket server = null;
        try{

            // Initialize Server Socket and Server Port
            server = new ServerSocket(1234);


            System.out.println("Waiting for requests...");
            while (true) {

                // Client connection accept
                Socket client = server.accept();
                System.out.println("New client connected!");

                // New Master Thread for the client
                new Thread(new MasterThread(client, workersList, taskMap, completedTasks)).start();
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
