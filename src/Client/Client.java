package Client;

import Entities.MessageData;

import java.io.IOException;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.*;


public class Client {

    public static void main(String[] args){

        try {
            Socket socket = new Socket("localhost", 1234);

            // writing to server
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);

            // reading from server
            BufferedReader in
                    = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());


            System.out.println(in.readLine());
            System.out.println(in.readLine());

            Scanner scannerIn = new Scanner(System.in);

            String serverReply ;
            while (true){

                System.out.print("Enter message for Master: ");
                // create serializable object to pass through socket
                MessageData message = new MessageData(scannerIn.nextLine());
                // pass the object through the stream
                objectOutput.writeObject(message);
                objectOutput.flush();
                // read server response
                serverReply = in.readLine();
                System.out.println("Server replied: " + serverReply);

                if (serverReply.equals("Insert json : ")){
                    message = new MessageData();
                    // Reads the json file from the selected directory
                    BufferedReader json = new BufferedReader( new FileReader("C:\\Users\\nikos\\Documents\\GitHub\\DistributedBookingApp\\src\\assets\\room.json"));
                    String line ;
                    // read each line of the .json file
                    while ((line = json.readLine()) != null){
                        // concatenating the data from the .json file
                        message.data += line;
                    }

                    System.out.println(message.data);

                }
                // Send message object through tcp to the MasterThread
                objectOutput.writeObject(message);


            }


        }
        catch (IOException  e){
            e.printStackTrace();
        }

    }
}
