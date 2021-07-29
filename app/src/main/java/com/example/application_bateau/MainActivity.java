package com.example.application_bateau;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import ProtocoleIOBREP.ReponseIOBREP;
import ProtocoleIOBREP.RequeteIOBREP;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private String completeLog = null;
    private static final int SERVERPORT = 6666;
    private static final String SERVER_IP = "192.168.1.129";
    private ObjectInputStream ois;
    private ObjectOutputStream oos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button clickButton = (Button) findViewById(R.id.but);
        EditText login = (EditText) findViewById(R.id.login);
        EditText password = (EditText) findViewById(R.id.password);

        clickButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                String logStr = login.getText().toString();
                String passStr =password.getText().toString();
                completeLog = logStr + ":"+passStr;
                new Thread(new ClientThread()).start();


            }
        });
        }
    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                Log.i("run","create socket ok");


                PrintWriter out = null;
                RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.LOGIN, completeLog);
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

                if(rep.getCode() == ReponseIOBREP.LOGIN_OK)
                {

                    Log.i("Contenuerep", rep.getChargeUtile());
                startActivity(new Intent(MainActivity.this,ChoixActivity.class));

                }
                else

                     Log.i("Failcontenu", rep.getChargeUtile());
                if(rep.getCode() != ReponseIOBREP.LOGIN_OK) {
                    socket.close();
                    socket = null;
                }
            }
            catch (ClassNotFoundException e) {
                Log.i(""," *** erreur classe");
            }
            catch (IOException e) {
                Log.i(""," *** erreur reseau 0");
            }
        }

    }

    }



