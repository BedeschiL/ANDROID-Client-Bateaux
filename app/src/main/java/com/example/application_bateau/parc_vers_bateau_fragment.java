package com.example.application_bateau;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application_bateau.Utils.RepAndReq;
import com.example.application_bateau.Utils.UpdateEtat;
import com.example.application_bateau.socketHanlder.SocketHandler;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ProtocoleIOBREP.ReponseIOBREP;
import ProtocoleIOBREP.RequeteIOBREP;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class parc_vers_bateau_fragment extends Fragment implements View.OnClickListener {
    //region comm
    private Socket socket;
    private String completeLog = null;
    private static final int SERVERPORT = 6666;
    private static final String SERVER_IP = "192.168.1.129";
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    public SocketHandler sHandler;
    //endregion
    //region container
    private EditText dest;
    private CheckBox ordre;
    //endregion
    //region table
    public static String[] spaceHeader = {"Emplacement", "Id cont", "Destination"};
    public TableView<String[]> tableView;
    //endregion
    //region handleout
    private EditText idContHandle;
    private CheckBox ordreHandle;
    //endregion
    //region messageupdate
    private TextView updateUnload;
    //endregion
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //region View

        View view = inflater.inflate(R.layout.parc_vers_bateau, container, false);

        //endregion
        //region Buttons
        //DEST POUR GET CONTAINER
        Button clickButton = view.findViewById(R.id.cont);
        clickButton.setOnClickListener(this);
        dest = view.findViewById(R.id.destBat);
        ordre = view.findViewById(R.id.trie);
        //CONTAINEUR HANDLE
        Button clickButtonHangdle = view.findViewById(R.id.contoff);
        clickButtonHangdle.setOnClickListener(this);
        idContHandle = view.findViewById(R.id.idContUnload);
        ordreHandle = view.findViewById(R.id.trieHandle);
        //CONTAINEUR END HANDLE
        Button clickButtonHandout = view.findViewById(R.id.endout);
        clickButtonHandout.setOnClickListener(this);
        //endregion
        //region Update Message et tableau
        //TABLEAU
        tableView = view.findViewById(R.id.tableview);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getActivity(), spaceHeader));
        tableView.setColumnCount(3);
        //UPDATE MEESAGE
        updateUnload = view.findViewById(R.id.handleUpdate);

        //endregion
        //region socket
        ois = sHandler.ois;
        oos = sHandler.oos;
        socket = SocketHandler.getSocket();
        //endregion
        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cont:
                new Thread(new parc_vers_bateau_fragment.ThreadGetContainer()).start();
                break;
            case R.id.contoff:
                new Thread(new parc_vers_bateau_fragment.ThreadHANDLE_CONTAINER_OUT()).start();
                break;
            case R.id.endout:
                new Thread(new parc_vers_bateau_fragment.ThreadEND_CONTAINER_OUT()).start();
                break;
        }
    }
    class ThreadGetContainer implements Runnable {
        String[] parse = null;
        @Override
        public void run() {
            //region trie
            String trie;
            boolean checked = ordre.isChecked();
            if (checked) {
                trie = "First";
            } else {
                trie = "Random";
            }
            //endregion
            //region Requete
            RepAndReq repAndReq = new RepAndReq();
            completeLog = dest.getText().toString() + ":" + trie;
            repAndReq.RequestIOBREP(RequeteIOBREP.GET_CONTAINERS, completeLog, oos);
            //endregion
            //region reponse
            ReponseIOBREP rep = repAndReq.ReponseIOBREP(RequeteIOBREP.GET_CONTAINERS, ois);
            if (rep.getCode() == ReponseIOBREP.GET_CONTAINER) {
                if (rep.getChargeUtile() != null) {
                    String repToParse = rep.getChargeUtile();
                    parse = repToParse.split(":");
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateEtat();
                            }

                            private void updateEtat() {
                                String[][] parse2 = new String[parse.length][0];
                                int j = 0;
                                for (int i = 1; i < parse.length; i++) {
                                    parse2[j] = parse[i].split("@");
                                    j++;
                                }
                                tableView.setDataAdapter(new SimpleTableDataAdapter(getActivity(), parse2));
                            }
                        });
                    }
                } else
                    Log.i("GET_CONTAINER", "Pas de reponse serveur");
            }
            else
                Log.i("GET_CONTAINERfail", rep.getChargeUtile());
            //endregion
        }
    }
    class ThreadHANDLE_CONTAINER_OUT implements Runnable {
        @Override
        public void run() {
            completeLog = null;
            //region trie
            String trie;
            boolean checked = ordreHandle.isChecked();
            if (checked) {
                trie = "First";
            } else {
                trie = "Random";
            }
            //endregion
            //region Requete
            completeLog = idContHandle.getText().toString() + ":" + trie;
            RepAndReq repAndReq = new RepAndReq();
            repAndReq.RequestIOBREP(RequeteIOBREP.HANDLE_CONTAINER_OUT, completeLog, oos);
            //endregion
            //region reponse
            ReponseIOBREP rep = repAndReq.ReponseIOBREP(RequeteIOBREP.HANDLE_CONTAINER_OUT, ois);
            if (rep.getCode() == ReponseIOBREP.HANDLE_CONTAINER_OUT) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(()
                        -> UpdateEtat.UpdateEtat(Color.GREEN,updateUnload,3000,"Le containeur a ete enregistre, en attente de validation"));
                }
            } else if (getActivity() != null) {
                getActivity().runOnUiThread(()
                        -> UpdateEtat.UpdateEtat(Color.RED,updateUnload,3000,"Le containeur n'a pas ete trouve, ou n'est pas le plus ancien"));
            }
            //endregion
        }
    }
    class ThreadEND_CONTAINER_OUT implements Runnable {
        @Override
        public void run() {
            //region Requete
            completeLog = "noneed";
            RepAndReq repAndReq = new RepAndReq();
            repAndReq.RequestIOBREP(RequeteIOBREP.END_CONTAINER_OUT, completeLog, oos);
            //endregion
            //region reponse
            ReponseIOBREP rep = repAndReq.ReponseIOBREP(RequeteIOBREP.END_CONTAINER_OUT, ois);
                if (rep.getCode() == ReponseIOBREP.END_CONTAINER_OUT) {
                    Log.i("END_CONTAINER_OUT", rep.getChargeUtile());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            UpdateEtat.UpdateEtat(Color.GREEN,updateUnload,3000,"Le deplacement est confirme !");
                        });
                    }
                } else
                    Log.i("END_CONTAINER_OUT", rep.getChargeUtile());
                //endregion
        }
    }
}
