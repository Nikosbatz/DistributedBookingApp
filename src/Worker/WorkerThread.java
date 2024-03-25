package Worker;

import Entities.AccommodationRoom;

import java.util.ArrayList;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class WorkerThread implements Runnable{




    ArrayList<AccommodationRoom> roomsList;
    public WorkerThread(Socket client, ArrayList<AccommodationRoom> roomsList ){
        this.roomsList = roomsList;

    }

    public void run(){
        Scanner scannerIn = new Scanner(System.in);

        System.out.println("Connection with Worker established...");
        if (roomsList.isEmpty()){
            System.out.println("There are no rooms to show...");
        }
        System.out.println("Would you like to insert a new room?");
        if (scannerIn.nextLine().equals("y")){
            System.out.println("Insert .json for the room you want to register: ");
            String json = scannerIn.nextLine();
            System.out.println("PRINTING JSON FOR TESTING PURRPOSES:");
            System.out.println(json);
        }

    }
}
