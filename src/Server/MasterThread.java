package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Entities.MessageData;
import Manager.ManagerService;
import Manager.RentalListing;
import Worker.Worker;


public class MasterThread implements Runnable{
    private Socket client;
    private  final ArrayList<Worker> workerslist;
    ManagerService managerService = new ManagerService("...\\src\\assets");
    

    MasterThread(Socket client, ArrayList<Worker> workersList,ManagerService managerService){
        this.client = client;
        this.workerslist = workersList;
        this.managerService=managerService;
    }

    @Override
    public void run(){

        try {
            PrintWriter out = new PrintWriter(client.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());


            out.println("Hello are you a renter or a manager?");
            out.println("press (a) for manager OR (b) for renter");
            out.flush();

            // read client response
            String line = ((MessageData)objectIn.readObject()).data;
            System.out.println(line);

            while (line != null){

                if (line.equals("a")){
                    // Manager interface method call
                    runManagerInterface(out, in, objectIn);
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

    public void runManagerInterface(PrintWriter out, BufferedReader in, ObjectInputStream objectIn){

        out.println("Welcome to manager interface, would you like to insert a new room?");
        out.flush();

        try {
            String response = ((MessageData)objectIn.readObject()).data;
            if (response.equals("y")) {

                out.println("Please insert the JSON for the new room:");
                out.flush();

                // Reads JSON data from the client.
                MessageData message = (MessageData) objectIn.readObject();
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(message.data);

                // Extracting information from the JSONObject and creating a new listing.
                String roomName = (String) json.get("roomName");
                long noOfPersons = (Long) json.get("noOfPersons");
                String area = (String) json.get("area");
                long stars = (Long) json.get("stars");
                long noOfReviews = (Long) json.get("noOfReviews");
                String roomImage = (String) json.get("roomImage");

                // Assuming ManagerService has a method to add a new listing that takes these parameters.
                managerService.addListing(new RentalListing(roomName, (int)noOfPersons, area, (int)stars, (int)noOfReviews, roomImage));
            
                out.println("New room inserted successfully.");
                out.flush();

            }

        }
        catch (IOException | ClassNotFoundException | ParseException e){
            e.printStackTrace();
        }
    }

    public void runRenterInterface(PrintWriter out, BufferedReader in){
        out.println("Welcome to renter interface...");
        out.flush();
    }

    public ArrayList<Socket> connectWithWorkers(){

        // List of all the connections with Workers
        ArrayList<Socket> sockets = new ArrayList<Socket>();

        try {
            for (Worker w : workerslist) {

                // Establish a new connection with each of the Workers
                sockets.add(new Socket("localhost", w.getPort()));
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return sockets;
    }


}
