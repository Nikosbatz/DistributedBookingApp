package Reducer;

import Entities.AccommodationRoom;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ReducerThread implements Runnable{

    Socket client ;

    public ReducerThread(Socket client, HashMap<Integer, ArrayList<AccommodationRoom>> results ){
        this.client = client;
    }



    public void run()  {

        try {

            ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());

            System.out.println("Reducer Thread Started !!!");

            HashMap<Integer, ArrayList<AccommodationRoom>> result = (HashMap<Integer, ArrayList<AccommodationRoom>>) objectIn.readObject();
            System.out.println(result.keySet());


        }
        catch (IOException| ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
