package  Worker;



import Entities.AccommodationRoom;
import Server.MasterThread;

import java.io.*;
import java.net.*;
import java.util.*;



public class Worker {

    private static int nextWorkerPort = 1111;

    static ArrayList<AccommodationRoom> roomsList = new ArrayList<AccommodationRoom>();

    public static void main (String[] args){

        try{
            ServerSocket workerSocket = new ServerSocket( getNextWorkerPort());

            while (true){

                Socket client = workerSocket.accept();
                System.out.println("Master IPv4: " + client.getInetAddress());

                new Thread(new WorkerThread(client, roomsList)).start();

            }

        }
        catch (IOException e){
            e.printStackTrace();
        }



    }

    public static int getNextWorkerPort() {
        return nextWorkerPort++;
    }

}
