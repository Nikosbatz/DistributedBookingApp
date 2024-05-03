package com.example.myapplication.backend.src.Reducer;

import  com.example.myapplication.backend.src.Entities.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ReducerDaemonThread implements Runnable{

    int WorkersNum;
    HashMap<Integer, ArrayList<AccommodationRoom>> results;
    HashMap<Integer, Integer> taskRepliesCount;



    public ReducerDaemonThread(int WorkersNum, HashMap<Integer, ArrayList<AccommodationRoom>> results, HashMap<Integer, Integer> taskRepliesCount){
        this.results = results;
        this.WorkersNum = WorkersNum;
        this.taskRepliesCount = taskRepliesCount;
    }

    public void run() {

        while(true){
            synchronized (results) {
                try {
                    results.wait();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // key == taskID
                for (int key: results.keySet()){

                    // If all the Workers replied for this taskID
                    if(taskRepliesCount.get(key) == WorkersNum){
                        sendResultToMaster(key);
                        results.remove(key);

                        // Safely remove this task's replies
                        synchronized (taskRepliesCount){
                            taskRepliesCount.remove(key);
                        }
                    }
                }
            }
        }
    }


    public void sendResultToMaster(int taskID){

        ArrayList<AccommodationRoom> result = results.get(taskID);

        try {
            Socket socket = new Socket("localhost", 1234);
            ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

            // Declare to Master that this object is sent from the Reducer
            objectOut.writeObject("reducer");

            // Send taskID and the Task's result to master
            objectOut.writeObject(taskID);
            objectOut.writeObject(result);

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
