package Reducer;

import Server.MasterThread;
import Worker.Worker;

import java.io.*;
import java.net.*;
import java.util.*;

public class Reducer {

    private static ArrayList<Thread> threads ;

    public static void main(String[] args){

        threads = new ArrayList<>();
        ServerSocket server = null;
        try{

            // Initialize Server Socket and Server Port
            server = new ServerSocket(1235);

            System.out.println("Waiting for requests...");
            while (true) {

                // Client connection accept
                Socket client = server.accept();
                System.out.println("New client connected!");

                // New Reducer Thread for each worker
                new Thread(new ReducerThread(client)).start();
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
