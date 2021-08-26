package com.example.application_bateau;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.application_bateau.socketHanlder.SocketHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ChoixActivity extends AppCompatActivity {
    private static String langage=null;
    public SocketHandler sHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivy_choix);
        Bundle b = getIntent().getExtras();
        if(b != null)
        {
            langage = b.getString("lg");
            if(langage==null)
            {
                Log.i("value","NULLL");
            }
            else
            {
                Log.i("value",langage);
                Locale myLocale = new Locale(langage);
                Resources res = super.getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
                Intent refresh = new Intent(this, ChoixActivity.class);
                finish();
                startActivity(refresh);
            }
        }



        BottomNavigationView bottomnav = findViewById(R.id.bottom_nav);
        bottomnav.setOnNavigationItemReselectedListener(navLister);



    }

    private BottomNavigationView.OnNavigationItemReselectedListener navLister = new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull @NotNull MenuItem item) {
            Fragment selectFrag = null;
            switch(item.getItemId())
            {

                case R.id.item2:
                    Toast.makeText(ChoixActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    selectFrag = new home_fragment();
                    break;
                case R.id.item3:
                    Toast.makeText(ChoixActivity.this, "load", Toast.LENGTH_SHORT).show();
                    selectFrag = new parc_vers_bateau_fragment();
                    break;
                case R.id.item4:
                    Toast.makeText(ChoixActivity.this, "unlaod", Toast.LENGTH_SHORT).show();
                    selectFrag = new bateau_vers_parc_fragment();
                    break;


        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frameout,selectFrag).commit();
    };



    };
}