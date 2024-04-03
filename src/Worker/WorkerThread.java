package Worker;

import Entities.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;


public class WorkerThread implements Runnable{

    Socket client;
    public HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap;
    public WorkerThread(Socket client, HashMap<Integer, ArrayList<AccommodationRoom>> roomsMap ){
        this.roomsMap = roomsMap;
        this.client = client;
    }

    public void run(){

        try{
            System.out.println("WorkerThread Started!!!");

            // Instantiating object streams.
            ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());

            // Reads the type of user (Manager OR Renter)
            Boolean isManager =  objectIn.readBoolean();

            // Reading the method that Client Requested to Master
            String methodRequested = (String) objectIn.readObject();

            System.out.println(methodRequested);

            if (isManager){

                switch (methodRequested){
                    case "insert":
                        //TODO
                        break;

                    case "show":
                        //TODO
                        break;

                    default:
                        break;
                }

            }
            else {

                switch (methodRequested) {
                    case "filter":
                        //TODO

                        // Reading arguments from Master
                        HashMap<String, String> filters =  (HashMap<String, String>) objectIn.readObject();
                        break;

                    case "rate":
                        //TODO
                        break;

                    case "book":
                        //TODO
                        break;

                    default:
                        break;
                }
            }


        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }
}

