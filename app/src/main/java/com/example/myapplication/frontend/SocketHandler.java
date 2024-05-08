package com.example.myapplication.frontend;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketHandler {

    private static Socket socket;
    private static ObjectInputStream objectIn;
    private static ObjectOutputStream objectOut;


    public static Socket getSocket(){
        return socket;
    }
    public static void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }

    public static ObjectInputStream getObjectIn() {
        return objectIn;
    }

    public static void setObjectIn(ObjectInputStream objectIn) {
        SocketHandler.objectIn = objectIn;
    }

    public static ObjectOutputStream getObjectOut() {
        return objectOut;
    }

    public static void setObjectOut(ObjectOutputStream objectOut) {
        SocketHandler.objectOut = objectOut;
    }
}
