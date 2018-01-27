package com.heliohost.kakapo.datitalia;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = AppCompatActivity.class.getSimpleName();

    // elementi della pagina
    private TextView mTimer;
    private ProgressBar mProgressBar;
    private Button abbandonaButton;
    private Button risposta1;
    private Button risposta2;
    private TextView textViewDomanda;

    // contatori
    private int corrette = 0;
    private int sbagliate = 0;
    private int corretteAv = 0;
    private int sbagliateAv = 0;
    private int actualQuestion = 0;
    private int totalQuestions;
    private int time = 100;

    // utilities
    private String domanda;
    private String playerStatus;
    private String adversaeyStatus;
    private String playerID;
    private String uid;
    private DBMatch dbRef;
    private DBMatch dbMatchActual;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private MyCountDownTimer getCountDownTimer_singol = new MyCountDownTimer(1500, 1000);
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        dbRef = (DBMatch) getIntent().getSerializableExtra("dbMatch");
        totalQuestions = dbRef.getQuestions().size();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        playerStatus = (uid.equals(dbRef.getPlayer1ID())) ? "player1Status" : "player2Status";
        adversaeyStatus = (uid.equals(dbRef.getPlayer1ID())) ? "player2Status" : "player1Status";
        playerID = (uid.equals(dbRef.getPlayer1ID())) ? dbRef.getPlayer1ID() : dbRef.getPlayer2ID();
        databaseReference.child("games").child(dbRef.getGameRef()).child(playerStatus).setValue("onGame");

        mTimer = findViewById(R.id.textView3);
        mProgressBar = findViewById(R.id.progress_bar);
        abbandonaButton = findViewById(R.id.abbandona);
        risposta1 = findViewById(R.id.risposta_1);
        risposta2 = findViewById(R.id.risposta_2);
        textViewDomanda = findViewById(R.id.domanda);

        //risposta1.setBackgroundColor(getResources().getColor(R.color.pla_button_1));
        //risposta2.setBackgroundColor(getResources().getColor(R.color.pla_button_2));
        risposta1.setBackground(getDrawable(R.drawable.game_button));
        risposta2.setBackground(getDrawable(R.drawable.game_button));
        abbandonaButton.setOnClickListener(this);
        risposta1.setOnClickListener(this);
        risposta2.setOnClickListener(this);

        risposta1.setText(dbRef.getPlayer1Province());
        risposta2.setText(dbRef.getPlayer2Province());

        domanda = dbRef.getQuestions().get(actualQuestion).getQuestionType() + " " + dbRef.getPlayer1Province() + " o " + dbRef.getPlayer2Province() + " riguardo a " + dbRef.getQuestions().get(actualQuestion).getComparto() + "?";
        textViewDomanda.setText(domanda);
        int seconds = dbRef.getQuestions().size() * 7000;
        mTimer.setText("" + (seconds / 1000));
        // tempo totale

        countDownTimer = new CountDownTimer(seconds, 1000) {
            @Override
            public void onTick(long l) {
                mProgressBar.setProgress(time);
                time = time - 5;
            }

            @Override
            public void onFinish() {
                textViewDomanda.setText("TEMPO SCADUTO");
                risoluzione_partita();
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.abbandona:
                abbandona_dialog();
                break;
            case R.id.risposta_1:
                if (actualQuestion < totalQuestions)
                    nextQuestion(1);

                break;
            case R.id.risposta_2:
                if (actualQuestion < totalQuestions)
                    nextQuestion(2);
                break;
        }
    }

    private void abbandona_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sicuro di voler abbandonare la partita?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        countDownTimer.cancel();
                        changeStatusOnExit();
                        GameActivity.this.finish();
                        startActivity(new Intent(getApplicationContext(), StartActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    private void risoluzione_partita() {
        databaseReference.child("games").child(dbRef.getGameRef()).child(playerStatus).setValue("onWaiting");
        goToResolution();
    }

    private void goToResolution() {
        databaseReference.child("games").child(dbRef.getGameRef()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                countDownTimer.cancel();
                Log.d(TAG, "onDataChange: THREAD ABORTITO");
                String status = dataSnapshot.child(adversaeyStatus).getValue(String.class);
                if (status.equals("onWaiting") || status.equals("offline") || status.equals("online")) {
                    dbMatchActual = dataSnapshot.getValue(DBMatch.class);
                    for (MatchQuestion matchQuestion : dbMatchActual.getQuestions()) {
                        Integer rispostaData = (uid.equals(dbRef.getPlayer1ID())) ? matchQuestion.getPlayer1response() : matchQuestion.getPlayer2response();
                        Integer rispostaCorretta = matchQuestion.getCorrectAnswer();
                        if (rispostaCorretta == rispostaData)
                            corrette++;
                        else
                            sbagliate++;
                    }
                    if(dbRef.getPlayer2ID().equals("botID")){
                        for(MatchQuestion matchQuestion : dbMatchActual.getQuestions()){
                            Integer rispostaCorrettaAv = matchQuestion.getCorrectAnswer();
                            Integer rispostaDataAv = matchQuestion.getPlayer2response();
                            if(rispostaCorrettaAv == rispostaDataAv)
                                corretteAv++;
                            else
                                sbagliateAv++;
                        }
                        FirebaseDatabase.getInstance().getReference().child("games").child(dbRef.getGameRef()).child("correttePlayer2").setValue(corretteAv);
                        FirebaseDatabase.getInstance().getReference().child("games").child(dbRef.getGameRef()).child("sbagliatePlayer2").setValue(sbagliateAv);
                    }
                    String playerCorrette = (uid.equals(dbRef.getPlayer1ID())) ? "correttePlayer1" : "correttePlayer2";
                    String playerSbagliate = (uid.equals(dbRef.getPlayer1ID())) ? "sbagliatePlayer1" : "sbagliatePlayer2";
                    FirebaseDatabase.getInstance().getReference().child("games").child(dbRef.getGameRef()).child(playerCorrette).setValue(corrette);
                    FirebaseDatabase.getInstance().getReference().child("games").child(dbRef.getGameRef()).child(playerSbagliate).setValue(sbagliate);
                    Log.d("RISP", "onDataChange: risposte 1 " + corrette);
                    Log.d("RISP", "onDataChange: risposte 1 " + corrette );
                    corrette = 0;
                    sbagliate = 0;
                    corretteAv = 0;
                    sbagliateAv = 0;
                    Intent intent1 = new Intent(getApplicationContext(), GameRisoluzionePartita.class);
                    intent1.putExtra("dbMatch", dbMatchActual);
                    startActivity(intent1);
                } else {
                    Toast.makeText(getApplicationContext(), "Sto aspettando il tuo avversario", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setColours(int answer) {
        if (actualQuestion == totalQuestions) {
            Log.d("VAAAA", "setColours: " + actualQuestion);
        } else {
            Integer actualAnswer = 0;
            boolean exception = false;
            try {
                actualAnswer = dbRef.getQuestions().get(actualQuestion).getCorrectAnswer();
            } catch (Exception e) {
                exception = true;
                risposta1.setClickable(false);
                risposta2.setClickable(false);
                risoluzione_partita();
            }
            if (!exception) {
                risposta1.setClickable(false);
                risposta2.setClickable(false);

                if (actualAnswer == answer) {
                    if (actualAnswer == 1)
                        risposta1.setBackground(getDrawable(R.drawable.button_correct));
                    else
                        risposta2.setBackground(getDrawable(R.drawable.button_correct));
                } else {
                    if (actualAnswer == 1) {
                        risposta1.setBackground(getDrawable(R.drawable.button_correct));
                        risposta2.setBackground(getDrawable(R.drawable.button_wrong));

                    } else {
                        risposta1.setBackground(getDrawable(R.drawable.button_wrong));
                        risposta2.setBackground(getDrawable(R.drawable.button_correct));
                    }
                }
                getCountDownTimer_singol.start();
            }
        }
    }

    private void nextQuestion(int answer) {
        if (dbRef.getPlayer1ID().equals(uid))
            databaseReference.child("games").child(dbRef.getGameRef()).child("questions").child("" + actualQuestion).child("player1response").setValue(answer);
        else
            databaseReference.child("games").child(dbRef.getGameRef()).child("questions").child("" + actualQuestion).child("player2response").setValue(answer);


        setColours(answer);
        actualQuestion++;
        if (actualQuestion == totalQuestions) {
            risposta1.setClickable(false);
            risposta2.setClickable(false);
            risoluzione_partita();
        } else {
            domanda = dbRef.getQuestions().get(actualQuestion).getQuestionType() + " " + dbRef.getPlayer1Province() + " o " + dbRef.getPlayer2Province() + " riguardo a " + dbRef.getQuestions().get(actualQuestion).getComparto() + "?";
            textViewDomanda.setText(domanda);
        }
    }

    private void changeStatusOnExit() {
        databaseReference.child("games").child(dbRef.getGameRef()).child(playerStatus).setValue("offline");
        databaseReference.child("users").child(playerID).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long points = dataSnapshot.getValue(Long.class);
                points = points + 10;
                databaseReference.child("users").child(playerID).child("points").setValue(points);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.abbandona_gioco))
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        countDownTimer.cancel();
                        GameActivity.this.finish();
                        databaseReference.child("games").child(dbRef.getGameRef()).child(playerStatus).setValue("offline");
                        risoluzione_partita();
                        startActivity(new Intent(getApplicationContext(), StartActivity.class));
                        GameActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        changeStatusOnExit();
        super.onDestroy();

    }

    public class MyCountDownTimer extends CountDownTimer {


        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            //risposta1.setBackgroundColor(getResources().getColor(R.color.pla_button_1));
            //risposta2.setBackgroundColor(getResources().getColor(R.color.pla_button_2));
            risposta1.setBackground(getDrawable(R.drawable.game_button));
            risposta2.setBackground(getDrawable(R.drawable.game_button));
            risposta1.setClickable(true);
            risposta2.setClickable(true);
        }
    }
}
