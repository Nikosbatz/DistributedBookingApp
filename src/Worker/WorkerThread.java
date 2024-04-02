package Worker;

import Entities.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;


public class WorkerThread implements Runnable{

    Socket client;
    HashMap<Integer,AccommodationRoom> roomsMap;

    public WorkerThread(Socket client, HashMap<Integer,AccommodationRoom> roomsList ){
        this.roomsMap = roomsList;
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

                // Reading arguments from Master
            HashMap<String, String> filters =  (HashMap<String, String>) objectIn.readObject();



        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }
}

