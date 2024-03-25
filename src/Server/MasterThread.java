package Server;

import Entities.MessageData;
import Worker.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MasterThread implements Runnable{
    private Socket client;
    private  ArrayList<Worker> workerslist;


    MasterThread(Socket client, ArrayList<Worker> workersList){
        this.client = client;
        this.workerslist = workersList;
    }

    @Override
    public void run(){

        try {
            PrintWriter out = new PrintWriter(client.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            ObjectInputStream objectInput = new ObjectInputStream(client.getInputStream());

            out.println("Hello are you a renter or a manager?");
            out.println("press (a) for manager OR (b) for renter");
            out.flush();

            // read client response
            String line = ((MessageData)objectInput.readObject()).data;
            System.out.println(line);

            while (line != null){
                System.out.println("Thread:"+Thread.currentThread().threadId()+" Client says: " + line);
                if (line.equals("a")){
                    // Manager interface method call
                    runManagerInterface(out, in, objectInput);
                    break;
                }
                else{
                    // renter interface method call
                    runRenterInterface(out, in);
                    break;
                }
                //line = in.readLine();
            }
        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }


    }

    public void runManagerInterface(PrintWriter out, BufferedReader in, ObjectInputStream objectInput){

        out.println("Welcome to manager interface, would you like to insert a new room?");
        out.flush();
        System.out.println("Welcome to manager interface, would you like to insert a new room?");

        try {
            String response = ((MessageData)objectInput.readObject()).data;
            if (response.equals("y")) {
                out.println("Insert json : ");
                out.flush();

                MessageData message = (MessageData) objectInput.readObject();

                System.out.println(message.data);
            }

        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }

    public void runRenterInterface(PrintWriter out, BufferedReader in){
        out.println("Welcome to renter interface...");
        out.flush();
    }


}
