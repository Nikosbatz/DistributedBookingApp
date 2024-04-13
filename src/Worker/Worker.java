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

    private String ip = "localhost";

    public static HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap;

    // Default Constructor
    public Worker(){
        this.port = getNextWorkerPort();
        this.id = getNextWorkerId();
        roomsMap = new HashMap<>();
        startWorker();
    }

    public Worker(String ip, int port, int id){
        this.ip = ip;
        this.port = port;
        this.id = id;
    }


    public static void main (String[] args){

        roomsMap = new HashMap<>();

        try {

            // Opening a ServerSocket to wait for connections
            ServerSocket workerSocket = new ServerSocket(1111);

            while (true) {
                // Waiting for incoming connections...
                Socket client = workerSocket.accept();

                // Initializing a new thread to handle the MasterThread
                new Thread(new WorkerThread(0, client, roomsMap)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String getIp() {return ip;}
}
