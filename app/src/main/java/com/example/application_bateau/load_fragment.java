package com.example.application_bateau;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import ProtocoleIOBREP.ReponseIOBREP;
import ProtocoleIOBREP.RequeteIOBREP;

public class load_fragment extends Fragment implements View.OnClickListener {
    private Socket socket;
    private String completeLog = null;
    private static final int SERVERPORT = 6666;
    private static final String SERVER_IP = "192.168.1.129";
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_load, container, false);
        Button clickButton = (Button) view.findViewById(R.id.handlein);
        clickButton.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        new Thread(new load_fragment.ClientThread()).start();
    }
    class ClientThread implements Runnable {
        @Override
        public void run() {
            InetAddress serverAddr = null;
            try {
                serverAddr = InetAddress.getByName(SERVER_IP);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                socket = new Socket(serverAddr, SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("run","create socket HANDLE_CONTAINER_IN");
            PrintWriter out = null;
            completeLog="pasutil";
            RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.HANDLE_CONTAINER_IN, completeLog);
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(req);
                oos.flush();
            }
            catch (IOException e) {
                Log.i("HANDLE_CONT_INerreur" ,e.getMessage());
            }
            ReponseIOBREP rep = null;
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                rep = (ReponseIOBREP)ois.readObject();
                if(rep.getCode() == ReponseIOBREP.HANDLE_CONTAINER_IN)
                {

                    Log.i("HANDLE_CONTAINER_IN", rep.getChargeUtile());
                }
                else


                    Log.i("HANDLE_CONTAINER_IN",  rep.getChargeUtile());
                if(rep.getCode() != ReponseIOBREP.HANDLE_CONTAINER_IN) {
                    socket.close();
                    socket = null;
                }
            }
            catch (ClassNotFoundException e) {
                Log.i("HANDLE_CONTAINER_IN"," *** erreur classe");
            }
            catch (IOException e) {
                Log.i("HANDLE_CONTAINER_IN"," *** erreur reseau 0");
            }
        }
    }
}
