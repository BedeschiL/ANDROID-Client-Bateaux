package com.example.application_bateau;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.application_bateau.socketHanlder.SocketHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Locale;

import ProtocoleIOBREP.ReponseIOBREP;
import ProtocoleIOBREP.RequeteIOBREP;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    public SocketHandler sHandler;
    private String completeLog = null;
    private static  int SERVERPORT = 6666;
    private static  String SERVER_IP = "192.168.1.129";
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private static String langage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            langage = b.getString("lg");
            if (langage == null) {
                Log.i("value", "NULLL");
            } else {
                Log.i("value", langage);
                Locale myLocale = new Locale(langage);
                Resources res = super.getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
                Intent refresh = new Intent(this, MainActivity.class);
                finish();
                startActivity(refresh);

            }
        }
        setContentView(R.layout.activity_main);
        Button clickButton = (Button) findViewById(R.id.but);
        EditText login = (EditText) findViewById(R.id.login);
        EditText password = (EditText) findViewById(R.id.password);
        EditText ip = (EditText) findViewById(R.id.ip);
        EditText port = (EditText) findViewById(R.id.port);
        clickButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SERVER_IP=ip.getText().toString();
                SERVERPORT=Integer.parseInt(port.getText().toString());
                String logStr = login.getText().toString();
                String passStr = password.getText().toString();
                completeLog = logStr + ":" + passStr;
                new Thread(new ClientThread()).start();
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(MainActivity.this, Settings.class));
        finish();
        return true;
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {
            Log.i("run0", "create socket ok");

            InetAddress serverAddr = null;
            try {
                serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            sHandler.setSocket(socket);
                PrintWriter out = null;
                RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.LOGIN, completeLog);
                try {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    sHandler.oos=oos;
                    oos.writeObject(req);
                    oos.flush();
                } catch (IOException e) {
                    Log.i("Erreur réseau ?", e.getMessage());
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                }
                ReponseIOBREP rep = null;
                try {
                    ois = new ObjectInputStream(socket.getInputStream());
                    sHandler.ois=ois;
                    rep = (ReponseIOBREP) ois.readObject();

                    if (rep.getCode() == ReponseIOBREP.LOGIN_OK) {
                        if (langage != null) {

                            Intent intent = new Intent(MainActivity.this, ChoixActivity.class);
                            Bundle b = new Bundle();
                            b.putString("lg", langage);

                            intent.putExtras(b); //Put your id to your next Intent
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(MainActivity.this, ChoixActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else
                        Log.i("Failcontenu", rep.getChargeUtile());

                } catch (ClassNotFoundException e) {
                    Log.i("", " *** erreur classe");
                } catch (IOException e) {
                    Log.i("", " *** erreur reseau 0");
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                }
            }



    }
}



