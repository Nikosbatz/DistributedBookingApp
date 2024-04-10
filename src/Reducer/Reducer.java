package Reducer;

import Entities.AccommodationRoom;
import Server.MasterThread;
import Worker.Worker;

import java.io.*;
import java.net.*;
import java.util.*;

public class Reducer {


    private static HashMap<Integer, ArrayList<AccommodationRoom>> results;
    private static HashMap<Integer, Integer> taskRepliesCount = new HashMap<>();


    public static void main(String[] args){

        // Initializing variables
        results = new HashMap <>();
        ServerSocket server = null;

        // Number Of Worker servers
        int workersNum = 1;

        // Instantiate and start a Daemon Thread
        Thread daemon = new Thread(new ReducerDaemonThread(workersNum, results, taskRepliesCount ));
        daemon.setDaemon(true);
        daemon.start();


        try{

            // Initialize Server Socket and Server Port
            server = new ServerSocket(1235);

            System.out.println("Waiting for requests...");
            while (true) {

                // Client connection accept
                Socket client = server.accept();
                System.out.println("New client connected!");

                // New Reducer Thread for each worker
                new Thread(new ReducerThread(client, results, taskRepliesCount)).start();
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
