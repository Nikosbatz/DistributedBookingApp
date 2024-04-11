
package Clients;

import Entities.MessageData;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.Socket;
import java.io.*;
import java.util.*;


public class Client {

    public static void main(String[] args){

        try {
            Socket socket = new Socket("localhost", 1234);


            // Reading objects from Server
            ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());

            // Writing objects to Server
            ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

            // Reading Server messages
            System.out.println(objectIn.readObject());
            System.out.println(objectIn.readObject());

            // Instantiating Client input object
            Scanner scannerIn = new Scanner(System.in);

            String serverReply ;
            String tempReply;
            while (true){

                System.out.print("Enter message for Master: ");


                // pass the object through the stream
                objectOut.writeObject(scannerIn.nextLine());
                objectOut.flush();

                // read server response
                serverReply = (String) objectIn.readObject();

                while (true){

                    // Checks if Server is waiting for JSON file
                    if (serverReply.equals("Please insert the JSON for the new room:")){

                        insertJSONFile(objectOut, objectIn, scannerIn);
                    }

                    // Reads server Responses until server replies with null
                    System.out.println("Server replied: " + serverReply);
                    tempReply = (String)objectIn.readObject();
                    if ( tempReply != null ){
                        serverReply = tempReply;

                    }
                    else {
                        break;
                    }
                }


            }

        }
        catch (IOException | ClassNotFoundException| NullPointerException e){
            e.printStackTrace();
        }

    }



    public static void  insertJSONFile (ObjectOutputStream objectOut, ObjectInputStream objectIn, Scanner scannerIn){
        try {
            String jsonData = "";

            System.out.print("Enter .JSON file name: ");
            String jsonPath = scannerIn.nextLine();

            // Reads the json file from the selected directory
            BufferedReader jsonFile = new BufferedReader(new FileReader("C:\\Users\\nikos\\Documents\\GitHub\\DistributedBookingApp_main\\src\\assets\\" + jsonPath + ".json"));
            String line;

            // read each line of the .json file
            while ((line = jsonFile.readLine()) != null) {

                // concatenating the data from the .json file
                jsonData += line;
            }

            // Instantiate JSON parser
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonData);
            System.out.println(json.get("capacity"));

            // Make new MessageData with JSONObject
            MessageData messageJSON = new MessageData((JSONObject) json);

            // Send MessageData object containing .json file data to the server.
            objectOut.writeObject(messageJSON);
            objectOut.flush();


        }
        catch (IOException | ParseException e){
            e.printStackTrace();
        }

    }


}
