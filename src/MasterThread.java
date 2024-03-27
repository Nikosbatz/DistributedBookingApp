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
        out.println("Choose one of the following options.");
        out.println("1. Filter the rooms. \n2. Make a reservation \n3. Rate a room");
        out.flush();

        String command = in.readLine();
        Renter renter = new Renter();
        switch (command){
            case "1":
        
                out.println("Enter the location (if you don't have a preferance, type 'null'): ");
                String location = in.readLine();
                out.println("Enter the first date (if you don't have a preferance, type 'null'): ");
                String dateStart = in.readLine();
                //LocalDate dateStart = LocalDate.parse(tempDate);
                out.println("Enter the last date (if you don't have a preferance, type 'null'): ");
                String dateEnd = in.readLine();
                //LocalDate dateEnd = LocalDate.parse(tempDate);
                out.println("Enter the number of people (if you don't have a preferance, type '0'): ");
                int noPeople = Integer.parseInt(in.readLine());
                out.println("Enter the price (if you don't have a preferance, type '0'): ");
                double price = Double.parseDouble(in.readLine());
                out.println("Enter the number of rating stars of the room (if you don't have a preferance, type '0'): ");
                int stars = Integer.parseInt(in.readLine());
                out.println("Filtering...");

                renter.filterAccommodations(location, dateStart, dateEnd, noPeople, price, stars);
                break;
            case "2":
                //display rooms
                out.println("Choose one of the following rooms to reserve by typing its name: ");
                String roomName = in.readLine();
                renter.bookAccommodation(roomName);
                break;
            case "3":
                out.println("Rate the accomodation");
                out.println("Choose one of the following rooms to rate by typing its name: ");
                roomName = in.readLine();
                out.println("Enter the stars you want to give: ");
                stars = Integer.parseInt(in.readLine());
                renter.rateAccommodation(roomName,stars);
                break;
            default: 
                out.println("Unknown command.");
                break;
        }

    }
}

