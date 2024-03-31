package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import Entities.MessageData;

public class Client {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1234);
             ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {
             
            // Read the initial message from the server
            MessageData messageFromServer = (MessageData) objectInput.readObject();
            System.out.println(messageFromServer.data);

            while (true) {
                System.out.print("Enter a message for master: ");
                String userInput = scanner.nextLine();

                // Check if the user wants to quit
                if ("quit".equalsIgnoreCase(userInput)) {
                    break; // Exit the loop, effectively closing the client
                }

                try {
                    // Create a serializable object to pass through the socket
                    MessageData messageToSend = new MessageData(userInput);
                    objectOutput.writeObject(messageToSend);
                    objectOutput.reset(); // Reset the stream to clear any object caching
                    objectOutput.flush();

                    // Attempt to read a server response
                    messageFromServer = (MessageData) objectInput.readObject();
                    System.out.println("Server replied: " + messageFromServer.data);
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error during communication: " + e.getMessage());
                    break; // Exit on communication error
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Initialization error: " + e.getMessage());
        }
    }
}
