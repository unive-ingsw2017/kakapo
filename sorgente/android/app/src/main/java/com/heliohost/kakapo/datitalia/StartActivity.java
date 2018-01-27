package com.heliohost.kakapo.datitalia;


import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.Serializable;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "StartActivity";

    private User utente;
    private DatabaseReference userReference;
    private ValueEventListener userLoaded;
    private FirebaseAuth firebaseAuth;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    dialog_choose_provincia();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        DataUtility.getInstance().googleConnection(this,this);
    }

    private void dialog_choose_provincia() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View inflator_provincia = inflater.inflate(R.layout.scelta_provincia_primo_accesso, null);
        AlertDialog alert;
        builder.setView(inflator_provincia);
        final Spinner spinner = inflator_provincia.findViewById(R.id.spinner);
        builder.setCancelable(false);
        builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String provincia = spinner.getSelectedItem().toString();
                Log.i("PROVINCIA", provincia);
                User user1 = new User(uid, firebaseAuth.getCurrentUser().getEmail(),
                        firebaseAuth.getCurrentUser().getDisplayName(),
                        provincia,
                        false,
                        0);
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(user1);
            }
        });
        alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
            Adding listeners for Buttons
        */
        findViewById(R.id.btn_game).setOnClickListener(StartActivity.this);
        findViewById(R.id.btn_view_data).setOnClickListener(this);
        findViewById(R.id.btn_confronto).setOnClickListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        //userReference.removeEventListener(userLoaded);
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
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.btn_game:
                Log.i(TAG, "Game button pressed!");
                i = new Intent(getApplicationContext(), GameMenuActivity.class);
                startActivity(i);
                break;
            case R.id.btn_view_data:
                Log.i(TAG, "View Data button pressed!");
                i = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(i);
                break;
            case R.id.btn_confronto:
                Log.i(TAG, "Confronto button pressed!");
                i = new Intent(getApplicationContext(), ConfrontoActivity.class);
                startActivity(i);
            default:
                Log.w(TAG, "OnClickListener switch default case reached, unhandled callback for component pressed!");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,StartActivity.class));
        finish();
    }
}