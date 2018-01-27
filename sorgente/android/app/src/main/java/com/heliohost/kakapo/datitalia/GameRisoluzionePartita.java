package com.heliohost.kakapo.datitalia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GameRisoluzionePartita extends AppCompatActivity {

    private static final String VINCITORE = "Il vincitore è ";
    DBMatch dbMatch;
    private List<RisoluzionePartita> datiList = new ArrayList<>();
    private RisoluzionePartitaAdapter mAdapter;
    private RecyclerView rv;
    private DBMatch dbMatchActual;
    private String playerID;
    private int corrette = 0;
    private int sbagliate = 0;
    private String uid;
    private TextView textViewVincitore;
    private String player1Name;
    private String player2Name;
    private Long pointsPlayer1;
    private Long pointsPlayer2;
    private TextView nome1;
    private TextView nome2;
    private TextView corretta;
    private Button esci;

    private void getPlayer2Name() {
        if (dbMatch.getPlayer2ID().equals("botID")) {
            player2Name = "Computer";
            createRecycleView();
        } else {
            FirebaseDatabase.getInstance().getReference().child("users").child(dbMatch.getPlayer2ID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    player2Name = dataSnapshot.child("username").getValue(String.class);
                    pointsPlayer2 = dataSnapshot.child("points").getValue(Long.class);
                    createRecycleView();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private void getPlayer1Name() {
        FirebaseDatabase.getInstance().getReference().child("users").child(dbMatch.getPlayer1ID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                player1Name = dataSnapshot.child("username").getValue(String.class);
                pointsPlayer1 = dataSnapshot.child("points").getValue(Long.class);
                getPlayer2Name();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void createRecycleView() {
        nome1.setText(player1Name + " (" + dbMatch.getPlayer1Province() + ")");
        nome2.setText(player2Name + " (" + dbMatch.getPlayer2Province() + ")");
        corretta.setText("Corretta");
        FirebaseDatabase.getInstance().getReference().child("games").child(dbMatch.getGameRef()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dbMatchActual = dataSnapshot.getValue(DBMatch.class);
                Log.d("BOOOOOO", "onDataChange: " + dbMatchActual.toString());
                if (dbMatchActual.getCorrettePlayer1() > dbMatchActual.getCorrettePlayer2()) {
                    textViewVincitore.setText(VINCITORE + player1Name);
                    FirebaseDatabase.getInstance().getReference().child("users").child(dbMatch.getPlayer1ID()).child("points").setValue(pointsPlayer1 - 10);

                } else {
                    if (dbMatchActual.getCorrettePlayer1() == dbMatchActual.getCorrettePlayer2()) {
                        textViewVincitore.setText("Pareggio");
                        FirebaseDatabase.getInstance().getReference().child("users").child(dbMatch.getPlayer1ID()).child("points").setValue(pointsPlayer1 - 5);
                        if (!dbMatch.getPlayer2ID().equals("botID"))
                            FirebaseDatabase.getInstance().getReference().child("users").child(dbMatch.getPlayer2ID()).child("points").setValue(pointsPlayer2 - 5);
                    } else {
                        if (!dbMatch.getPlayer2ID().equals("botID"))
                            FirebaseDatabase.getInstance().getReference().child("users").child(dbMatch.getPlayer2ID()).child("points").setValue(pointsPlayer2 - 10);
                        textViewVincitore.setText(VINCITORE + player2Name);
                    }
                }
                mAdapter = new RisoluzionePartitaAdapter(datiList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                rv = findViewById(R.id.recycle_view);
                rv.setLayoutManager(mLayoutManager);
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(mAdapter);
                for (MatchQuestion matchQuestion : dbMatchActual.getQuestions()) {
                    String domanda = "Chi " + matchQuestion.getQuestionType() + " riguardo a " + matchQuestion.getComparto() + "?";
                    String prov1 = dbMatchActual.getPlayer1Province();
                    String prov2 = dbMatchActual.getPlayer2Province();
                    String correct = (matchQuestion.getCorrectAnswer() == 1) ? prov1 : prov2;
                    String risp1 = (matchQuestion.getPlayer1response() == 1) ? prov1 : (matchQuestion.getPlayer1response() == 2) ? prov2 : "None";
                    String risp2 = (matchQuestion.getPlayer2response() == 1) ? prov1 : (matchQuestion.getPlayer2response() == 2) ? prov2 : "None";
                    RisoluzionePartita risoluzionePartita = new RisoluzionePartita(risp1, risp2, correct, domanda);
                    datiList.add(risoluzionePartita);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_risoluzione_partita);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        dbMatch = (DBMatch) getIntent().getSerializableExtra("dbMatch");
        esci = findViewById(R.id.esci);

        esci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                finish();
            }
        });

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        playerID = (uid.equals(dbMatch.getPlayer1ID())) ? dbMatch.getPlayer1ID() : dbMatch.getPlayer2ID();
        textViewVincitore = findViewById(R.id.vincitore);
        nome1 = findViewById(R.id.nome1);
        nome2 = findViewById(R.id.nome2);
        corretta = findViewById(R.id.corretta);
        getPlayer1Name();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, StartActivity.class));
        finish();
    }
}
