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


            System.out.println(in.readLine());
            System.out.println(in.readLine());

            Scanner sc = new Scanner(System.in);
            String line = null;

            while (!"exit".equals(line)){
                System.out.print("Enter message for Master: ");
                line = sc.nextLine();
                out.println(line);
                out.flush();
                System.out.println("Server replied: "+ in.readLine());


            }
            sc.close();

        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
