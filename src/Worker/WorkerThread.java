package Worker;

import Entities.*;
import java.util.ArrayList;
import java.io.*;
import java.net.*;


public class WorkerThread implements Runnable{

    Socket client;
    ArrayList<AccommodationRoom> roomsList;

    public WorkerThread(Socket client, ArrayList<AccommodationRoom> roomsList ){
        this.roomsList = roomsList;
        this.client = client;
    }

    public void run(){

        try{
            System.out.println("WorkerThread Started!!!");
            // Instantiating object streams.
            ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());

            MessageData type = (MessageData) objectIn.readObject();
            // Reading the method that Client Requested to Master
            String methodRequested = (String) objectIn.readObject();

            // Reading the filter data
            List<String> filter = (List<String>) objectIn.readObject();
            System.out.println("Method requested: " + methodRequested);
            System.out.println(message.data);

            //for each roon in roonList compare filters and return a after filtering list

        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }
}

