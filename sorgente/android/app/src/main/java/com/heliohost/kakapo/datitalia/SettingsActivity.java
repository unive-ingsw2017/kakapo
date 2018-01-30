package com.heliohost.kakapo.datitalia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView changeUsername;
    private TextView changeProvincia;
    private TextView infoSviluppatori;
    private TextView segnalazioni;
    private TextView infoApplicazione;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private static void contactHelpAndSupport(Context context, String[] to, String subject) {
        String body = "";
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "Come possiamo aiutarti?\n\n\n\n\n\n\nPer favore, non cancellare il contenuto sottostante\nDevice OS: Android(" +
                    Build.VERSION.RELEASE + ")\n App v" + body + "\n Device: " + Build.BRAND +
                    ", " + Build.MODEL;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, "Send email:"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DataUtility.getInstance().googleConnection(this, this);
        changeUsername = findViewById(R.id.change_username);
        changeProvincia = findViewById(R.id.change_provincia);
        infoSviluppatori = findViewById(R.id.info_sviluppatori);
        segnalazioni = findViewById(R.id.segnalazioni);
        infoApplicazione = findViewById(R.id.info_applicazione);

        changeProvincia.setOnClickListener(this);
        changeUsername.setOnClickListener(this);
        infoSviluppatori.setOnClickListener(this);
        segnalazioni.setOnClickListener(this);
        infoApplicazione.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_provincia:
                changeProvinciaDialog();
                break;
            case R.id.change_username:
                changeUsernameDialog();
                break;
            case R.id.info_sviluppatori:
                infoSviluppatoriDialog();
                break;
            case R.id.info_applicazione:
                infoApplicazioneDialog();
                break;

            case R.id.segnalazioni:
                contactHelpAndSupport(this, new String[]{"kakapo.software.engineering@gmail.com"}, "Segnalazione e/o consiglio");
                break;
        }
    }

    private void changeProvinciaDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View inflator_provincia = inflater.inflate(R.layout.scelta_provincia, null);
        AlertDialog alert;
        builder.setView(inflator_provincia);
        final Spinner spinner = inflator_provincia.findViewById(R.id.spinner);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String provincia = spinner.getSelectedItem().toString();
                Log.i("PROVINCIA", provincia);
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(firebaseAuth.getUid()).child("provincia").setValue(provincia);
                showResponde();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert = builder.create();
        alert.show();
    }

    private void changeUsernameDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View inflator_username = inflater.inflate(R.layout.scelta_username, null);
        AlertDialog alert;
        builder.setView(inflator_username);
        final TextInputLayout editText = inflator_username.findViewById(R.id.text_input_layout);
        //editText.setError(null);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String username = editText.getEditText().getText().toString().trim();
                if (username.isEmpty()) {
                    editText.setError("Devi inserire un username");
                } else {
                    Log.i("NOME", username);
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(firebaseAuth.getUid()).child("username").setValue(username);
                    showResponde();
                }

            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();

    }

    private void infoSviluppatoriDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View inflator_sviluppatori = inflater.inflate(R.layout.info_sviluppatori, null);
        AlertDialog alert;
        builder.setView(inflator_sviluppatori);
        builder.setPositiveButton("Indietro", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert = builder.create();
        alert.show();
    }

    private void infoApplicazioneDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View inflator_applicazione = inflater.inflate(R.layout.info_applicazione, null);
        AlertDialog alert;
        builder.setView(inflator_applicazione);
        builder.setPositiveButton("Indietro", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert = builder.create();
        alert.show();
    }


    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            DataUtility.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showResponde() {
        Snackbar.make(findViewById(R.id.settings_layout), R.string.settings_response, Snackbar.LENGTH_SHORT).show();
    }
}
