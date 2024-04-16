package Server;

import Entities.AccommodationRoom;
import Entities.Task;
import Worker.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;


public class MasterThread implements Runnable{
    private Socket client;
    private  final ArrayList<Worker> workersList;
    private HashMap<Integer, Task> taskMap;
    HashMap<Integer, ArrayList<AccommodationRoom>> completedTasks;


    public MasterThread(Socket client, ArrayList<Worker> workersList, HashMap<Integer,
                        Task> taskMap, HashMap<Integer, ArrayList<AccommodationRoom>> completedTasks)
    {
        this.client = client;
        this.workersList = workersList;
        this.taskMap = taskMap;
        this.completedTasks = completedTasks;
    }

    @Override
    public void run(){

        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());


            // read client category
            String line = (String)objectIn.readObject();


            if (line.equals("manager")){
                // MANAGER interface method call
                runManagerInterface(objectOut, objectIn);

            }
            else if (line.equals("renter")){
                // RENTER interface method call
                runRenterInterface(objectOut, objectIn);

            }
            else if (line.equals("reducer")){
                // REDUCER interface method call
                runReducerInterface(objectOut, objectIn);
            }
        }

        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }



    // INTERFACE TYPES IMPLEMENTATIONS BELOW ( MANAGER, RENTER, REDUCER ) =========================
    public void runManagerInterface(ObjectOutputStream objectOut, ObjectInputStream objectIn){
        while (true){
            try {
                // Receive Task from Client
                Task task = (Task) objectIn.readObject();

                // If client wants to close connection
                if(task.getMethod().equals("exit")){
                    break;
                }


                // If method of Task is "insert"
                if (task.getMethod().equals("insert") || task.getMethod().equals("updateAvailableDates")) {

                    // Choosing Worker Node based on hashCode
                    long hashCode;
                    if (task.getJson() == null) {
                        hashCode = (long) task.getRoomName().hashCode();
                    }
                    else {
                        hashCode = (long) task.getJson().get("roomName").hashCode();
                    }
                    int WorkerId = (int) hashCode % workersList.size();
                    task.setWorkerID(WorkerId);

                    ObjectInputStream workerIn = null;
                    ObjectOutputStream workerOut = null;
                    for (Worker w : workersList) {
                        if (w.getId() == task.getWorkerID()) {
                            // Send insert Task to specific Worker based on Hashing
                            Socket socket = new Socket(w.getIp(), w.getPort());

                            workerOut = new ObjectOutputStream(socket.getOutputStream());
                            workerIn = new ObjectInputStream(socket.getInputStream());

                            workerOut.writeObject(task);

                        }
                    }
                    // return to client whether the operation was successful or not
                    if (workerIn != null ){
                        objectOut.writeObject((boolean) workerIn.readObject());
                    }

                } else {

                    System.out.println(task.getMethod());

                    // sockets connecting Master with Workers
                    HashMap<Socket, ObjectOutputStream> sockets = connectWithWorkers();

                    // Send task to all the workers that Master is connected to
                    sendTaskToWorkers(task, sockets);

                    // Wait for the Task to complete
                    ArrayList<AccommodationRoom> result = waitForResult(task);

                    // Return result back to user
                    objectOut.writeObject(result);
                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public void runRenterInterface(ObjectOutputStream objectOut, ObjectInputStream objectIn){
        while (true) {

            try {

                // Receive Task from Client
                Task task = (Task) objectIn.readObject();
                task.setIsManager(false);

                // If client wants to close connection
                if(task.getMethod().equals("exit")){
                    break;
                }

                if ( task.getMethod().equals("book") || task.getMethod().equals("rate")) {

                    // Choosing Worker Node based on hashCode
                    long hashCode;
                    if (task.getJson() == null) {
                        hashCode = (long) task.getRoomName().hashCode();
                    }
                    else {
                        hashCode = (long) task.getJson().get("roomName").hashCode();
                    }
                    int WorkerId = (int) hashCode % workersList.size();
                    task.setWorkerID(WorkerId);

                    ObjectInputStream workerIn = null;
                    ObjectOutputStream workerOut = null;
                    for (Worker w : workersList) {
                        if (w.getId() == task.getWorkerID()) {
                            // Send insert Task to specific Worker based on Hashing
                            Socket socket = new Socket(w.getIp(), w.getPort());

                            workerOut = new ObjectOutputStream(socket.getOutputStream());
                            workerIn = new ObjectInputStream(socket.getInputStream());

                            workerOut.writeObject(task);
                            break;

                        }
                    }

                    // return to client whether the operation was successful or not
                    if (workerIn != null ){
                        objectOut.writeObject((boolean) workerIn.readObject());
                    }


                }
                else {
                    // Establishes connection with Workers
                    HashMap<Socket, ObjectOutputStream> sockets = connectWithWorkers();

                    // Send task to all the workers that Master is connected to
                    sendTaskToWorkers(task, sockets);


                    // Wait for the Task to complete
                    ArrayList<AccommodationRoom> result = waitForResult(task);

                    // Return result back to user
                    objectOut.writeObject(result);
                }



            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void runReducerInterface(ObjectOutputStream objectOut, ObjectInputStream objectIn) {

        try {

            // Read taskID and result of the completed Task from reducer
            int taskID = (int) objectIn.readObject();
            ArrayList<AccommodationRoom> result = (ArrayList<AccommodationRoom>) objectIn.readObject();

            // Insert {taskID, result} to completedTasks and notifyAll threads waiting for new insertions
            synchronized (completedTasks){
                completedTasks.put(taskID, result);
                notifyAll();
            }


        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }










    // METHODS USED IN INTERFACES BELOW ============================


    // Establish connection with each Worker that Master is aware of.
    public HashMap<Socket, ObjectOutputStream> connectWithWorkers(){

        // Map of all the connections with Workers and the OutputStreams to communicate with each one
        HashMap<Socket, ObjectOutputStream> sockets = new HashMap<>();
        Socket socket;
        try {
            for (Worker w : workersList) {

                // Establish a new connection with each of the Workers
                socket = new Socket(w.getIp(), w.getPort());
                sockets.put(socket, new ObjectOutputStream(socket.getOutputStream()));
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return sockets;
    }



    public void sendTaskToWorkers(Task task,HashMap<Socket, ObjectOutputStream> sockets){

        // Send task to each Worker
        for (Socket socket : sockets.keySet()) {
            try {
                // Sends to the worker the method that client requests
                sockets.get(socket).writeObject(task);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public ArrayList<AccommodationRoom> waitForResult(Task task){

        // Thread constantly looking if the task that is sent to Worker is completed
        while (true) {

            synchronized (completedTasks) {

                // If there is a key == taskID then return this {key, value} pair
                if (completedTasks.get((int) task.getTaskID()) != null) {
                    ArrayList<AccommodationRoom> result = completedTasks.get((int) task.getTaskID());
                    completedTasks.remove((int) task.getTaskID());
                    return result;
                }
            }
        }
    }


}
