package com.example;

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
                            // Convert JSONObject to RentalListing and add to managerService...
                            break;
                        case SHOW_LISTINGS:
                            // Fetch listings from managerService and perhaps store somewhere
                            break;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


