package com.example.application_bateau.Utils;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ProtocoleIOBREP.ReponseIOBREP;
import ProtocoleIOBREP.RequeteIOBREP;

public class RepAndReq {
    public void RequestIOBREP(int typee, String charge, ObjectOutputStream oos)
    {
        RequeteIOBREP req = new RequeteIOBREP(typee, charge);
        try {
            oos.writeObject(req);
            oos.flush();
        }
        catch (IOException e) {
            Log.i("Erreur réseau" ,"Erreur sur le protocole n° " + typee +" : " + e.getMessage());
        }
    }
    public ReponseIOBREP ReponseIOBREP(int type, ObjectInputStream ois )
    {

        try {
            ReponseIOBREP rep = (ReponseIOBREP)ois.readObject();


            return rep;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


}
