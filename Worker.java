package com.example;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.json.JSONObject;

public class Worker extends Thread {
    private BlockingQueue<Task> taskQueue;
    private ManagerService managerService;

    public Worker(BlockingQueue<Task> taskQueue, ManagerService managerService) {
        this.taskQueue = taskQueue;
        this.managerService = managerService;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Task task = taskQueue.take();

                if (task instanceof ManagerOperationTask) {
                    ManagerOperationTask managerTask = (ManagerOperationTask) task;

                    switch (managerTask.getOperationType()) {
                        case ADD_LISTING:
                        JSONObject details = managerTask.getPayload();
                        RentalListing listing = new RentalListing(
                                details.getString("roomName"),
                                details.getInt("noOfPersons"),
                                details.getString("area"),
                                details.getInt("stars"),
                                details.getInt("noOfReviews"),
                                details.getString("roomImage"));
                        managerService.addListing(listing);
                        System.out.println("Listing added: " + listing);
                        break;
                    case SHOW_LISTINGS:
                        List<RentalListing> listings = managerService.getListings();
                        System.out.println("Current listings:");
                        for (RentalListing l : listings) {
                            System.out.println(l);
                        }
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}