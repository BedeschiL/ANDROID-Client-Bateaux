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

public class unload_fragment extends Fragment implements View.OnClickListener{
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
       Log.i("Unloadfrag","Unloadfrag");
        View view = inflater.inflate(R.layout.fragment_unload, container, false);
        Button clickButton = (Button) view.findViewById(R.id.cont);
        Button clickButtonHangdle = (Button) view.findViewById(R.id.contoff);
        Button clickButtonHandout = (Button) view.findViewById(R.id.endout);
        clickButtonHandout.setOnClickListener(this);
       clickButton.setOnClickListener(this);
        clickButtonHangdle.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.cont:
                new Thread(new unload_fragment.ClientThread1()).start();
                break;
            case R.id.contoff:
                new Thread(new unload_fragment.ClientThread2()).start();
                break;
            case R.id.endout:
                new Thread(new unload_fragment.ClientThread3()).start();
                break;
        }
    }
    class ClientThread1 implements Runnable {
        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                Log.i("run","create socketGET_CONTAINER");
                PrintWriter out = null;
                completeLog="Lourdes:First";
                RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.GET_CONTAINERS, completeLog);
                try {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();
                }
                catch (IOException e) {
                    Log.i("Erreur réseauGET_CONT" ,e.getMessage());
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ReponseIOBREP rep = null;
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                rep = (ReponseIOBREP)ois.readObject();

                if(rep.getCode() == ReponseIOBREP.GET_CONTAINER)
                {
                    if(rep.getChargeUtile()!=null)
                    Log.i("GET_CONTAINER", rep.getChargeUtile());
                    else
                        Log.i("GET_CONTAINER", "Pas de reponse serveur");
                }
                else

                Log.i("GET_CONTAINERfail", rep.getChargeUtile());
                if(rep.getCode() != ReponseIOBREP.GET_CONTAINER) {
                    socket.close();
                    socket = null;
                }
            }
            catch (ClassNotFoundException e) {
                Log.i("GET_CONTAINER"," *** erreur classe");
            }
            catch (IOException e) {
                Log.i("GET_CONTAINER"," *** erreur reseau 0");
            }
        }
    }
    class ClientThread2 implements Runnable {

        @Override
        public void run() {
            Log.i("run2"," *r2");
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                Log.i("run","create socket HANDLE_CONTAINER_OUT");
                completeLog="AAAA-CHARL-A1B2C4:NOORDER";
                RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.HANDLE_CONTAINER_OUT, completeLog);
                try {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();
                }
                catch (IOException e) {
                    Log.i("Erreur réseau ?" ,e.getMessage());
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ReponseIOBREP rep = null;
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                rep = (ReponseIOBREP)ois.readObject();
                if(rep.getCode() == ReponseIOBREP.HANDLE_CONTAINER_OUT)
                {

                    Log.i("HANDLE_CONTAINER_OUT", rep.getChargeUtile());
                }
                else


                Log.i("CONTAINER_OUTFail", rep.getChargeUtile());
                if(rep.getCode() != ReponseIOBREP.HANDLE_CONTAINER_OUT) {
                    socket.close();
                    socket = null;
                }
            }
            catch (ClassNotFoundException e) {
                Log.i("HANDLE_CONTAINER_OUT"," *** erreur classe");
            }
            catch (IOException e) {
                Log.i("HANDLE_CONTAINER_OUT"," *** erreur reseau 0");
            }
        }
    }
    class ClientThread3 implements Runnable {

        @Override
        public void run() {
            Log.i("run3"," END_CONTAINER_OUT");
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                Log.i("run","create socket END_CONTAINER_OUT");
                completeLog="noneed";
                RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.END_CONTAINER_OUT, completeLog);
                try {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();
                }
                catch (IOException e) {
                    Log.i("ErreurEND_CONTAINER_Out" ,e.getMessage());
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            ReponseIOBREP rep = null;
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                rep = (ReponseIOBREP)ois.readObject();
                if(rep.getCode() == ReponseIOBREP.END_CONTAINER_OUT)
                {

                    Log.i("END_CONTAINER_OUT", rep.getChargeUtile());
                }
                else


                  Log.i("END_CONTAINER_OUT", rep.getChargeUtile());
                if(rep.getCode() != ReponseIOBREP.END_CONTAINER_OUT) {
                    socket.close();
                    socket = null;
                }
            }
            catch (ClassNotFoundException e) {
                Log.i("END_CONTAINER_OUT"," *** erreur classe");
            }
            catch (IOException e) {
                Log.i("END_CONTAINER_OUT"," *** erreur reseau 0");
            }
        }
    }
}
