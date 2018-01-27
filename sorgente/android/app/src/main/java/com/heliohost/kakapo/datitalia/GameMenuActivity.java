package com.heliohost.kakapo.datitalia;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ThreadLocalRandom;

public class GameMenuActivity extends AppCompatActivity
        implements ModalitaMenuFragment.MenuListener,
        MatchMaker.MatchInteraction,
        MatchMaker.MatchMakerInteraction {

    static final String TAG = "GameMenuActivity";


    private final static String NONE = "none";
    FirebaseDatabase database;
    DatabaseReference mMatchmaker;
    DatabaseReference mGamesReference;
    private Match2 match2;
    private MatchMaker randomMatchMaker = null;
    private MatchMaker nationalMatchMaker = null;
    private MatchMaker regionalMatchMaker = null;
    private User utente;
    private String modalita;
    private TextView gameTitle;
    private LinearLayout fragmentContainer;
    private ModalitaMenuFragment mModalitaMenuFragment;
    private GameDatiPartita mGameDatiPartita;
    private GameCaricamentoPROVVISORIO mGameCaricamento;
    private ValueEventListener userUpdated;
    private DatabaseReference userRef;
    private BotTimer botTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //DataUtility.getInstance().googleConnection(this,this);
        mModalitaMenuFragment = new ModalitaMenuFragment();
        mGameDatiPartita = new GameDatiPartita();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mModalitaMenuFragment)
                .commit();


        database = FirebaseDatabase.getInstance();
        mMatchmaker = database.getReference("matchmaker");
        mGamesReference = database.getReference("games");


        userRef = DataUtility.getInstance().getUsers().child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userUpdated = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                utente = dataSnapshot.getValue(User.class);
                randomMatchMaker = new MatchMaker(database, MatchMaker.RANDOM, utente);
                randomMatchMaker.addMatchInteraction(GameMenuActivity.this);
                randomMatchMaker.addMatchMakerInteraction(GameMenuActivity.this);
                nationalMatchMaker = new MatchMaker(database, MatchMaker.NATIONAL, utente);
                nationalMatchMaker.addMatchInteraction(GameMenuActivity.this);
                nationalMatchMaker.addMatchMakerInteraction(GameMenuActivity.this);

                DataUtility.getInstance().getProvinces().child(utente.getProvincia()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Provincia p = dataSnapshot.getValue(Provincia.class);
                        regionalMatchMaker = new MatchMaker(database, p.getRegione(), utente);
                        regionalMatchMaker.addMatchInteraction(GameMenuActivity.this);
                        regionalMatchMaker.addMatchMakerInteraction(GameMenuActivity.this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        userRef.addListenerForSingleValueEvent(userUpdated);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: adding interactions");
        if (randomMatchMaker != null) {
            randomMatchMaker.addMatchInteraction(this);
            randomMatchMaker.addMatchMakerInteraction(this);
        }

        if (nationalMatchMaker != null) {
            nationalMatchMaker.addMatchInteraction(this);
            nationalMatchMaker.addMatchMakerInteraction(this);
        }

        if (regionalMatchMaker != null) {
            regionalMatchMaker.addMatchInteraction(this);
            regionalMatchMaker.addMatchMakerInteraction(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: removing interactions");
        if (randomMatchMaker != null) {
            randomMatchMaker.cancelSearch();
            randomMatchMaker.addMatchInteraction(this);
            randomMatchMaker.addMatchMakerInteraction(this);
        }

        if (nationalMatchMaker != null) {
            Log.d(TAG, "onStop: cancelling search for nazionale");
            nationalMatchMaker.cancelSearch();
            nationalMatchMaker.addMatchInteraction(this);
            nationalMatchMaker.addMatchMakerInteraction(this);
        }

        if (regionalMatchMaker != null) {
            regionalMatchMaker.cancelSearch();
            regionalMatchMaker.addMatchInteraction(this);
            regionalMatchMaker.addMatchMakerInteraction(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNazionaleButtonPressed() {
        Log.d(TAG, "Ho premuto");
        botTimer = new BotTimer(10000, 1000);
        botTimer.start();
        modalita = MatchMaker.NATIONAL;
        Log.d(TAG, "onNazionaleButtonPressed: Ricerca Gioco Nazionale");
        FragmentManager fragmentManager = getSupportFragmentManager();
        GameCaricamentoPROVVISORIO gameCaricamentoPROVVISORIO = new GameCaricamentoPROVVISORIO();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, gameCaricamentoPROVVISORIO, "Avvio caricamento");
        fragmentTransaction.addToBackStack("b");
        fragmentTransaction.commit();
        if (nationalMatchMaker != null)
            nationalMatchMaker.findMatch();
    }

    @Override
    public void onRegionaleButtonPressed() {
        modalita = MatchMaker.REGIONAL;
        botTimer = new BotTimer(10000, 1000);
        botTimer.start();
        Log.d(TAG, "onRegionaleButtonPressed: Ricerca Gioco Regionale");
        FragmentManager fragmentManager = getSupportFragmentManager();
        GameCaricamentoPROVVISORIO gameCaricamentoPROVVISORIO = new GameCaricamentoPROVVISORIO();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, gameCaricamentoPROVVISORIO, "Avvio caricamento");
        fragmentTransaction.addToBackStack("b");
        fragmentTransaction.commit();
        if (regionalMatchMaker != null)
            regionalMatchMaker.findMatch();
    }


    @Override
    public void onClassificaButtonPressed() {
        Log.d(TAG, "onClassificaButtonPressed: Richiesta Classifica!");
        startActivity(new Intent(getApplicationContext(), Classifica.class));
    }

    @Override
    public void onRandomButtonPressed() {
        Log.d(TAG, "onRandomButtonPressed: Random GAME button pressed");
        botTimer = new BotTimer(10000, 1000);
        botTimer.start();
        FragmentManager fragmentManager = getSupportFragmentManager();
        GameCaricamentoPROVVISORIO gameCaricamentoPROVVISORIO = new GameCaricamentoPROVVISORIO();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, gameCaricamentoPROVVISORIO, "Avvio caricamento");
        fragmentTransaction.addToBackStack("avvio_caricamento");
        fragmentTransaction.commit();
        if (randomMatchMaker != null)
            randomMatchMaker.findMatch();
    }

    @Override
    public void onTemporaryFailure() {
        Log.d(TAG, "onTemporaryFailure: Matchmaker temporary failure");
    }

    @Override
    public void onMatchUpdate(DBMatch match) {
        Log.d(TAG, "onMatchUpdate: DBMatch Updated " + match.toString());
    }

    @Override
    public void onSecondPlayerFound(final DBMatch dbMatch) {
        botTimer.cancel();
        Bundle bundle = new Bundle();
        bundle.putSerializable("dbMatch", dbMatch);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GameDatiPartita gameDatiPartita = new GameDatiPartita();
        gameDatiPartita.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, gameDatiPartita, "Visualizza Dati provincia");
        fragmentTransaction.addToBackStack("visualizza_dati").commit();
    }


    @Override
    public void onBeignFirst(DBMatch dbMatch) {
        Log.d(TAG, "onBeignFirst: I am first!");
    }

    @Override
    public void onBeignSecond(DBMatch dbMatch) {
        Log.d(TAG, "onBeignSecond: I am second!" + dbMatch.toString());
    }

    @Override
    public void onStopSearching() {
        Log.d(TAG, "onStopSearching: STOPPED SEARCHING!");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (botTimer != null)
            botTimer.cancel();
        if (randomMatchMaker != null)
            randomMatchMaker.cancelSearch();
        if (nationalMatchMaker != null)
            nationalMatchMaker.cancelSearch();
        if (regionalMatchMaker != null)
            regionalMatchMaker.cancelSearch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void botRequestDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.avvio_bot_message))
                .setCancelable(false)
                .setPositiveButton("Gioca!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (randomMatchMaker != null)
                            randomMatchMaker.cancelSearch();
                        if (nationalMatchMaker != null)
                            nationalMatchMaker.cancelSearch();
                        if (regionalMatchMaker != null)
                            regionalMatchMaker.cancelSearch();
                        FirebaseDatabase.getInstance().getReference().child("modalita").setValue("none");
                        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String provincia = dataSnapshot.child("users").child(uid).child("provincia").getValue(String.class);
                                final DBMatch dbMatch = new DBMatch();
                                dbMatch.setPlayer1Status("online");
                                dbMatch.setPlayer1ID(uid);
                                dbMatch.setSbagliatePlayer1(0);
                                dbMatch.setCorrettePlayer1(0);
                                dbMatch.setPlayer1Province(provincia);
                                int j = 0;
                                int numProv = 0;
                                for (DataSnapshot data : dataSnapshot.child("provinces").getChildren()) {
                                    j++;
                                    if (provincia.equals(data.getKey())) {
                                        numProv = j;
                                    }
                                }
                                int randomNumber = ThreadLocalRandom.current().nextInt(1, 20);
                                int i = 0;
                                for (DataSnapshot data : dataSnapshot.child("provinces").getChildren()) {
                                    i++;
                                    if (i == randomNumber) {
                                        if (numProv == randomNumber) {
                                            randomNumber = (randomNumber + 1) % 20;
                                        }
                                        String provincia1 = data.getKey();
                                        dbMatch.setPlayer2Province(provincia1);
                                        dbMatch.setPlayer2Status("onWaiting");
                                        dbMatch.setPlayer2ID("botID");
                                        dbMatch.setSbagliatePlayer2(0);
                                        dbMatch.setCorrettePlayer2(0);
                                    }
                                }
                                Provincia provincia1 = dataSnapshot.child("provinces").child(dbMatch.getPlayer1Province()).getValue(Provincia.class);
                                Provincia provincia2 = dataSnapshot.child("provinces").child(dbMatch.getPlayer2Province()).getValue(Provincia.class);
                                Match2 match2 = new Match2(provincia1, provincia2);
                                dbMatch.setQuestions(match2.getQuestions());
                                for (MatchQuestion matchQuestion : dbMatch.getQuestions()){
                                    int randomNum1 = ThreadLocalRandom.current().nextInt(1, 3);
                                    matchQuestion.setPlayer2response(randomNum1);
                                    Log.d("BOT", "onDataChange: BOT" + randomNum1);
                                }
                                String db = FirebaseDatabase.getInstance().getReference().child("games").push().getKey();
                                dbMatch.setGameRef(db);
                                FirebaseDatabase.getInstance().getReference().child("games").child(db).setValue(dbMatch);

                                Bundle bundle = new Bundle();
                                bundle.putSerializable("dbMatch", dbMatch);
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                GameDatiPartita gameDatiPartita = new GameDatiPartita();
                                gameDatiPartita.setArguments(bundle);
                                fragmentTransaction.replace(R.id.fragment_container, gameDatiPartita, "Visualizza Dati provincia");
                                fragmentTransaction.addToBackStack("visualizza_dati").commit();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                })
                .setNegativeButton("Attendi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class BotTimer extends CountDownTimer {

        public BotTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            botRequestDialog();
        }
    }
}
