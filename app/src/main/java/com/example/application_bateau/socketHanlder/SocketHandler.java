package com.example.application_bateau.socketHanlder;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketHandler {
    private static Socket socket;
    public static ObjectInputStream ois;
    public static ObjectOutputStream oos;

    public static synchronized Socket getSocket(){
        return socket;
    }
    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }
}