package com.heliohost.kakapo.datitalia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConfrontoActivity extends AppCompatActivity {

    String TAG = "DBP";
    private Spinner spinner_provincia1;
    private Spinner spinner_provincia2;
    private Button btnConfronta;
    private String primaProvincia;
    private String secondaProvincia;
    private ConfrontoSceltaFragment mConfrontoSceltaFragment;
    private ConfrontoVisualizzaFragment mConfrontoVisualizzaFragment;
    private ArrayList<Provincia> province;
    private ValueEventListener spinnerUpdater;
    private DatabaseReference provinces;

    private void getSpinnerItem(View view) {
        spinner_provincia1 = view.findViewById(R.id.spinner_provincia1);
        spinner_provincia2 = view.findViewById(R.id.spinner_provincia2);
        primaProvincia = spinner_provincia1.getSelectedItem().toString();
        secondaProvincia = spinner_provincia2.getSelectedItem().toString();
    }

    private void setDati() {
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confronto);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DataUtility.getInstance().googleConnection(this, this);

        this.provinces = DataUtility.getInstance().getProvinces();
        if (savedInstanceState == null) {
            mConfrontoSceltaFragment = new ConfrontoSceltaFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, mConfrontoSceltaFragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //provinces.addValueEventListener(spinnerUpdater);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //provinces.removeEventListener(spinnerUpdater);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            DataUtility.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return true;
        }
        if (id == R.id.impostazioni) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
