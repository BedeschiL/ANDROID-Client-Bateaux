package com.example.application_bateau;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.application_bateau.langageHandler.langageHandler;

import java.util.Locale;

public class Settings extends AppCompatActivity implements View.OnClickListener{
    Button FR;
    Button DE;
    Button EN;
    public static String lg=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button FR = findViewById(R.id.buttonFR);
        FR.setOnClickListener(this);
        Button DE=findViewById(R.id.buttonDE);
        DE.setOnClickListener(this);
        Button EN=findViewById(R.id.buttonEN);
        EN.setOnClickListener(this);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
       return true;
    }

    public void setLocale(String lang) {
        lg = lang;
        Locale myLocale = new Locale(lang);
        Resources res = super.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, Settings.class);
        finish();
        startActivity(refresh);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        Bundle b = new Bundle();
        b.putString("lg", lg);
        Log.i("lgsett",lg);//Your id
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();

    }
    @Override
    public void onClick(View view) {
        langageHandler lh = new langageHandler();
        switch(view.getId())
        {

            case R.id.buttonFR:
               setLocale("fr");
                break;
            case R.id.buttonDE:
               setLocale("de");
                break;
            case R.id.buttonEN:
                setLocale("en");
                break;

        }

    }
}