package  Worker;



import Entities.AccommodationRoom;

import java.io.*;
import java.net.*;
import java.util.*;



public class Worker {

    private static int nextWorkerPort = 1111;
    final private int port ;
    private static int nextWorkerId = 0;
    final private int id;

    public HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap;

    // Default Constructor
    public Worker(){
        this.port = getNextWorkerPort();
        this.id = getNextWorkerId();
        roomsMap = new HashMap<>();
        startWorker();
    }

    public void startWorker (){

        // Initialize Worker's ServerSocket
        // Using Threads to accept connections asynchronous
        new Thread(() -> {

            try {

                // Opening a ServerSocket to wait for connections
                ServerSocket workerSocket = new ServerSocket(getPort());

                while (true) {
                    // Waiting for incoming connections...
                    Socket client = workerSocket.accept();

                    // Initializing a new thread to handle the MasterThread
                    new Thread(new WorkerThread(id, client, roomsMap)).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public static int getNextWorkerPort() {
        return nextWorkerPort++;
    }

    public static int getNextWorkerId() {
        return nextWorkerId++;
    }

    public int getId() {
        return id;
    }

    public int getPort() {
        return port;
    }
}
