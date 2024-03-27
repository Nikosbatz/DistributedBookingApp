package Server;

import Entities.MessageData;
import Worker.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class MasterThread implements Runnable{
    private Socket client;
    private  final ArrayList<Worker> workerslist;


    MasterThread(Socket client, ArrayList<Worker> workersList){
        this.client = client;
        this.workerslist = workersList;
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

                // The method that Master will request from Workers
                String methodRequest = "insert";

                // Asking client to insert .json file
                out.println("Insert json : ");
                out.flush();

                // Reads .json data from Client.
                MessageData message = (MessageData) objectIn.readObject();

                // Establishes connection with Workers
                ArrayList<Socket> sockets = connectWithWorkers();

                // Instantiate JSON parser
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(message.data);

                // Choosing Worker Node based on hashCode
                long hashCode = (long) json.get("roomName").hashCode();
                int nodeId =(int) hashCode % workerslist.size();
                //System.out.println(nodeId);



                for (Socket socket : sockets) {
                    ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                    // Sends to the worker the method that client requests
                    objectOut.writeObject(methodRequest);
                    objectOut.writeObject(message);
                }

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
