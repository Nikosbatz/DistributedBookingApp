package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Manager.ManagerService;
import Worker.Worker;

public class Server {
    public static ArrayList<Worker> workersList = new ArrayList<Worker>();

    public static void main(String[] args){

        for (int i = 0; i < 1; i++){
            workersList.add(new Worker());
        }

        ServerSocket server = null;
        try{
            server = new ServerSocket(1234);
            // Get server port
            System.out.println(server.getLocalPort());
            System.out.println("Waiting for requests...");
            while (true) {
                // Client connection accept
                Socket client = server.accept();
                System.out.println("New client connected" + client.getInetAddress().getHostAddress()+":"+client.getPort());

                // New Master Thread for the client
                ManagerService managerService = new ManagerService("..\\src\\assets\\room.json");
                new Thread(new MasterThread(client, workersList, managerService)).start();
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
