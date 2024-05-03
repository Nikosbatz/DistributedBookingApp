package com.example.myapplication.backend.src.Reducer;

import  com.example.myapplication.backend.src.Entities.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ReducerThread implements Runnable{

    Socket client ;
    HashMap<Integer, ArrayList<AccommodationRoom>> results;
    HashMap<Integer, Integer> taskRepliesCount;
    int WorkersNum;

    public ReducerThread(Socket client, HashMap<Integer, ArrayList<AccommodationRoom>> results, HashMap<Integer, Integer> taskRepliesCount ){
        this.client = client;
        this.results = results;
        this.taskRepliesCount = taskRepliesCount;
    }



    public void run()  {

        try {

            ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());

            System.out.println("Reducer Thread Started !!!");

            // Get the result of an operation from a Worker
            HashMap<Integer, ArrayList<AccommodationRoom>> result = (HashMap<Integer, ArrayList<AccommodationRoom>>) objectIn.readObject();


            synchronized (taskRepliesCount){
                for (Integer taskID: result.keySet()){
                    if (taskRepliesCount.get(taskID) == null){
                        taskRepliesCount.put(taskID, 1);
                    }
                    else {
                        int i = taskRepliesCount.get(taskID) + 1;
                        taskRepliesCount.put(taskID, i);
                    }
                }
            }




            synchronized (results) {
                // Using for to get the key from the HashMap
                for (int key : result.keySet()) {

                    // If this is the first result for this task
                    if (results.get(key) == null) {

                        //put a new key,value pair to the results HashMap where key is the task's uniqueID and value is an ArrayList
                        results.put(key, result.get(key));
                    } else {
                        // Adds the rooms of the result to the results' ArrayList
                        results.get(key).addAll(result.get(key));
                    }
                }
                results.notifyAll();
            }


        }
        catch (IOException| ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
