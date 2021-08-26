package com.example.application_bateau;

import android.graphics.Color;
import android.os.Bundle;
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

import com.example.application_bateau.Utils.UpdateEtat;
import com.example.application_bateau.socketHanlder.SocketHandler;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ProtocoleIOBREP.ReponseIOBREP;
import ProtocoleIOBREP.RequeteIOBREP;

public class bateau_vers_parc_fragment extends Fragment implements View.OnClickListener {
    private Socket socket;
    public SocketHandler sHandler;
    private String completeLog = null;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private EditText idContaineurLoad;
    private EditText destContLoad;
    private TextView update;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bateau_vers_parc, container, false);
        idContaineurLoad = view.findViewById(R.id.idContaineurLoad);
        Button clickButton = (Button) view.findViewById(R.id.handlein);
        clickButton.setOnClickListener(this);
        update = (TextView) view.findViewById(R.id.statusBatToParc);
        Button endhandlein = (Button) view.findViewById(R.id.endHandleIn);
        endhandlein.setOnClickListener(this);
        destContLoad =(EditText) view.findViewById(R.id.destBatToParc) ;
        ois=sHandler.ois;
        oos=sHandler.oos;
        socket= SocketHandler.getSocket();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {

            case R.id.handlein:
                new Thread(new bateau_vers_parc_fragment.ClientThreadHandleIn()).start();
                break;
            case R.id.endHandleIn:
                new Thread(new bateau_vers_parc_fragment.ClientThreadEndHandleIn()).start();
                break;

        }


    }
    class ClientThreadHandleIn implements Runnable {
        @Override
        public void run() {
            completeLog=idContaineurLoad.getText().toString() +":"+destContLoad.getText().toString() ;
            RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.HANDLE_CONTAINER_IN, completeLog);
            try {

                oos.writeObject(req);
                oos.flush();
            }
            catch (IOException e) {
                Log.i("HANDLE_CONT_INerreur" ,e.getMessage());
            }
            ReponseIOBREP rep = null;
            try {
                rep = (ReponseIOBREP)ois.readObject();
                if(rep.getCode() == ReponseIOBREP.HANDLE_CONTAINER_IN)
                {

                    Log.i("HANDLE_CONTAINER_IN", rep.getChargeUtile());

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(()
                                -> UpdateEtat.UpdateEtat(Color.RED,update,10000, " Le containeur : "+idContaineurLoad.getText().toString() +" n'est pas traite"));
                    }
                }
                else
                    Log.i("HANDLE_CONTAINER_IN",  rep.getChargeUtile());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(()
                            -> UpdateEtat.UpdateEtat(Color.GREEN,update,10000, " Le containeur : "+idContaineurLoad.getText().toString() +" est en attente envoie au bateau"));
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

    class ClientThreadEndHandleIn implements Runnable {
        @Override
        public void run() {
            completeLog="noneed";
            RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.END_CONTAINER_IN, completeLog);
            try {
                oos.writeObject(req);
                oos.flush();
            }
            catch (IOException e) {
                Log.i("END_CONTAINER_IN erreur" ,e.getMessage());
            }
            ReponseIOBREP rep = null;
            try {
                rep = (ReponseIOBREP)ois.readObject();
                if(rep.getCode() == ReponseIOBREP.END_CONTAINER_IN)
                {
                    Log.i("END_CONTAINER_IN", rep.getChargeUtile());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(()
                                -> UpdateEtat.UpdateEtat(Color.RED,update,3000,"Probleme avec l'envoie du containeur au bateau"));
                    }
                }
                else
                    Log.i("END_CONTAINER_IN",  rep.getChargeUtile());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(()
                            -> UpdateEtat.UpdateEtat(Color.GREEN,update,3000, "Le containeur : "+idContaineurLoad.getText().toString() + " a  ete envoie au bateau"));
                }
            }
            catch (ClassNotFoundException e) {
                Log.i("END_CONTAINER_IN"," *** erreur classe");
            }
            catch (IOException e) {
                Log.i("END_CONTAINER_IN"," *** erreur reseau 0");
            }
        }
    }
}
