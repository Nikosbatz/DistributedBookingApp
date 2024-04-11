package Reducer;

import Entities.AccommodationRoom;

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

    public void run(){

        while(true){
            for (int key: results.keySet()){
                System.out.println(results.size());
                if(taskRepliesCount.get(key) == WorkersNum){

                    sendResultToMaster(key);
                    // Remove the results from this Task synchronized
                    synchronized (results){
                        results.remove(key);
                    }
                    // Remove the reply count of this task synchronized
                    synchronized (taskRepliesCount){
                        taskRepliesCount.remove(key);
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