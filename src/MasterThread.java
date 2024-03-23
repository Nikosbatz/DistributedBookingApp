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

            out.println("Hello are you a renter or a manager?");
            out.println("press (a) for manager OR (b) for renter");
            out.flush();

            String line = null;
            line = in.readLine();

            while (line != null){
                System.out.println("Thread:"+Thread.currentThread().threadId()+" Client says: " + line);
                if (line.equals("a")){
                    // Manager interface method call
                    runManagerInterface(out, in);
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
        catch (IOException e){
            e.printStackTrace();
        }


    }

    public void runManagerInterface(PrintWriter out, BufferedReader in){
        out.println("Welcome to manager interface...");
        out.flush();
    }

    public void runRenterInterface(PrintWriter out, BufferedReader in){
        out.println("Welcome to renter interface...");
        out.flush();
    }


}
