package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
public class Server {
    private static ArrayList<Worker> workersList = new ArrayList<>();
    private static ManagerService managerService; // Reference ManagerService

    public static void main(String[] args) {
        BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
        managerService = new ManagerService("path/to/your/listings.json");
        //managerService = new ManagerService();
        //MockDataLoader.loadMockData(managerService);
        // Initialize workers
        for (int i = 0; i < 4; i++) {
            Worker worker = new Worker(taskQueue, managerService);
            new Thread(worker).start(); // Start each worker in its own thread
            workersList.add(worker);
        }

        // Initialize ManagerService with the path to the JSON file
        //managerService = new ManagerService("path/to/your/listings.json");

        try (ServerSocket server = new ServerSocket(1234)) {
            System.out.println("Server is running on port " + server.getLocalPort());
            System.out.println("Waiting for requests...");

            while (true) {
                // Accept client connections
                Socket client = server.accept();
                System.out.println("New client connected: " + client.getInetAddress().getHostAddress());

                // Create and start a new MasterThread for each client connection
                new Thread(new MasterThread(client, workersList, managerService, taskQueue)).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
