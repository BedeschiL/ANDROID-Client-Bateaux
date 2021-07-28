package com.example.application_bateau;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class ChoixActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivy_choix);
        BottomNavigationView bottomnav = findViewById(R.id.bottom_nav);
        bottomnav.setOnNavigationItemReselectedListener(navLister);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameout,new home_fragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemReselectedListener navLister = new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull @NotNull MenuItem item) {
            Fragment selectFrag = null;
            switch(item.getItemId())
            {
                case R.id.item1:
                    Toast.makeText(ChoixActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    selectFrag = new home_fragment();
                    break;
                case R.id.item2:
                    Toast.makeText(ChoixActivity.this, "load", Toast.LENGTH_SHORT).show();
                    selectFrag = new loadfragment();
                    break;
                case R.id.item3:
                    Toast.makeText(ChoixActivity.this, "unlaod", Toast.LENGTH_SHORT).show();
                    selectFrag = new unloadfragment();
                    break;


        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frameout,selectFrag).commit();
    };



    };
}