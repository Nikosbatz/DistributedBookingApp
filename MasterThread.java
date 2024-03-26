package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import org.json.JSONObject;

public class MasterThread implements Runnable {
    private Socket client;
    private ArrayList<Worker> workerslist;
    private ManagerService managerService;
    private BlockingQueue<Task> taskQueue; // Add a reference to ManagerService

    // Modify constructor to accept ManagerService instance
    public MasterThread(Socket client, ArrayList<Worker> workerslist, ManagerService managerService, BlockingQueue<Task> taskQueue) {
        this.client = client;
        this.workerslist = workerslist;
        this.managerService = managerService;
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.println("Hello are you a renter or a manager?");
            out.println("Press (a) for manager OR (b) for renter");
            
            String line = in.readLine();

            if ("a".equals(line)) {
                // Call manager interface method
                runManagerInterface(out, in);
            } else if ("b".equals(line)) {
                // Call renter interface method
                runRenterInterface(out, in);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runManagerInterface(PrintWriter out, BufferedReader in) throws IOException {
    out.println("Welcome to manager interface...");
    out.println("Enter command (add, show):");
    out.flush();

    String command = in.readLine();
    JSONObject payload = new JSONObject();

    switch (command) {
        case "add":
            out.println("Enter listing details in JSON format:");
            out.flush();
            String jsonInput = in.readLine();
            payload = new JSONObject(jsonInput);
            try {
                taskQueue.put(new ManagerOperationTask(ManagerOperationTask.OperationType.ADD_LISTING, payload));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            break;
        case "show":
            try {
                taskQueue.put(new ManagerOperationTask(ManagerOperationTask.OperationType.SHOW_LISTINGS, null));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // Payload not needed for showing listings
            break;
        default:
            out.println("Unknown command.");
            break;
    }
    out.flush();
}


    private void runRenterInterface(PrintWriter out, BufferedReader in) {
        out.println("Welcome to renter interface...");
        // Implement renter operations similarly
    }
}

