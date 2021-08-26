package com.example.application_bateau;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application_bateau.socketHanlder.SocketHandler;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ProtocoleIOBREP.ReponseIOBREP;
import ProtocoleIOBREP.RequeteIOBREP;

public class home_fragment extends Fragment implements View.OnClickListener {
    private Socket socket;
    private String completeLog = null;
    private static final int SERVERPORT = 6666;
    private static final String SERVER_IP = "192.168.1.129";
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private EditText idBat ;
    private EditText dest ;
    private EditText idSoc ;
    private EditText idCont ;
    private EditText poids ;
    private EditText cap ;
    private TextView etat;
    public SocketHandler sHandler;
    private Button blabla;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button clickButton = (Button) view.findViewById(R.id.boatarriv);
        clickButton.setOnClickListener(this);
        idBat =(EditText)view.findViewById(R.id.idBateau);;
        dest=(EditText)view.findViewById(R.id.dest); ;
        idSoc =(EditText)view.findViewById(R.id.idSoc);;
        idCont =(EditText)view.findViewById(R.id.idCont);;
        poids =(EditText)view.findViewById(R.id.poids);;
        cap =(EditText)view.findViewById(R.id.capa);;
        etat = (TextView)view.findViewById(R.id.etat);


        ois=sHandler.ois;
        oos=sHandler.oos;
        socket= SocketHandler.getSocket();
        return view;
    }

    @Override
    public void onClick(View view) {
        new Thread(new home_fragment.ClientThread()).start();
    }

    class ClientThread implements Runnable {


        @Override
        public void run() {

            //RECUPERATION DES TEXTES;

            String idBatStr = idBat.getText().toString();
            String destStr = dest.getText().toString();
            String idSocStr= idSoc.getText().toString() ;
            String idContStr = idCont.getText().toString();
            String poidsStr = poids.getText().toString();
            String capStr = cap.getText().toString();

            completeLog=idBatStr+":"+idSocStr+":"+capStr+":"+poidsStr+":"+idContStr+":"+destStr;
            RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.BOAT_ARRIVED, completeLog);
            try {

                oos.writeObject(req);
                oos.flush();
            }
            catch (IOException e) {
                Log.i("BOAT_ARRIVED erreur" ,e.getMessage());
            }
            ReponseIOBREP rep = null;
            try {

                rep = (ReponseIOBREP)ois.readObject();
                if(rep.getCode() == ReponseIOBREP.BOAT_ARRIVED)
                {

                    Log.i("BOAT_ARRIVED", rep.getChargeUtile());
                    //ACCES UI POUR UPDATE
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                               etat.setText("Le bateau a ete enregistre");
                               etat.setTextColor(Color.GREEN);
                                (new Handler()).postDelayed(this::updateEtat, 3000);
                            }

                            private void updateEtat() {
                                etat.setText("");
                            }
                        });
                    }

                }
                else
                {
                    Log.i("BOAT_ARRIVED",  rep.getChargeUtile());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                etat.setText("Le bateau n'a pas ete enregistre");
                                etat.setTextColor(Color.RED);
                            }
                        });
                    }
                }
            }
            catch (ClassNotFoundException e) {
                Log.i("BOAT_ARRIVED"," *** erreur classe");
            }
            catch (IOException e) {
                Log.i("BOAT_ARRIVED"," *** erreur reseau 0");
            }
        }
    }

}
