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



    public ReducerDaemonThread(int WorkersNum, HashMap<Integer, ArrayList<AccommodationRoom>> results){
        this.results = results;
        this.WorkersNum = WorkersNum;
    }

    public void run(){

        while(true){
            for (int key: results.keySet()){
                if(results.get(key).size() == WorkersNum){

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
            objectOut.writeObject("Reducer");
            //TODO Need to change MasterThread so Master can handle Reducer Objects

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
