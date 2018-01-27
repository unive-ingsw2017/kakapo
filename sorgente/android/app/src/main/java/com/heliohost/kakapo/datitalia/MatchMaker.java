package com.heliohost.kakapo.datitalia;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by Gregory on 1/15/2018.
 */


public class MatchMaker {
    public static final String NONE = "none";
    public final static String NATIONAL = "National";
    public final static String REGIONAL = "Regional";
    public final static String RANDOM = "Random";
    private static final String TAG = "matchmaker";
    private final FirebaseDatabase database;
    private final String matchMode;

    private final DatabaseReference matchmaker;
    private final DatabaseReference games;

    private final Set<MatchInteraction> interactions;
    private final Set<MatchMakerInteraction> matchMakerInteractions;

    private DBMatch dbMatch = null;
    private boolean secondFound = false;
    private DatabaseReference createdGameReference;
    private User player;
    private FirstPlayerLogic firstPlayerLogic;
    private SecondPlayerLogic secondPlayerLogic;

    private boolean waiting = false;
    private ValueEventListener waitingEvent = null;

    public MatchMaker(FirebaseDatabase database, String matchMode, User player) {
        this.database = database;
        this.matchMode = matchMode;
        this.player = player;

        matchmaker = database.getReference("matchmaker" + matchMode);
        games = database.getReference("games");

        this.interactions = new HashSet<>();
        this.matchMakerInteractions = new HashSet<>();

        firstPlayerLogic = new FirstPlayerLogic();
        secondPlayerLogic = new SecondPlayerLogic();

    }

    public void addMatchInteraction(MatchInteraction matchInteraction) {
        interactions.add(matchInteraction);
    }

    public void removeMatchInteraction(MatchInteraction matchInteraction) {
        interactions.remove(matchInteraction);
    }

    public void addMatchMakerInteraction(MatchMakerInteraction matchMakerInteraction) {
        matchMakerInteractions.add(matchMakerInteraction);
    }

    public void removeMatchMakerInteraction(MatchMakerInteraction matchMakerInteraction) {
        matchMakerInteractions.remove(matchMakerInteraction);
    }

    public void findMatch() {
        Log.d(TAG, "findMatch: looking matchmaker value!");
        matchmaker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String refvalue = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: refValue :" + refvalue);
                if (refvalue.equals(NONE)) {
                    firstArriver();
                } else {
                    if (!matchMode.equals(MatchMaker.RANDOM)) {
                        DataUtility.getInstance().getGames().child(refvalue).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DBMatch dbMatch = dataSnapshot.getValue(DBMatch.class);
                                if (dbMatch.getPlayer1Province().equals(player.getProvincia())) {
                                    Log.d(TAG, "onDataChange: Stessa provincia :/");
                                    Handler h = new Handler();
                                    h.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            findMatch();
                                        }
                                    }, 2000);
                                } else {
                                    secondArriver(refvalue);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        secondArriver(refvalue);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void firstArriver() {

        final String gameRef;
        //Creo una partita temporaneamente
        createdGameReference = games.push();
        gameRef = createdGameReference.getKey();
        Log.d(TAG, "firstArriver: " + gameRef);

        if (!matchMode.equals(MatchMaker.RANDOM)) {
            dbMatch = new DBMatch(player.getID(), player.getProvincia());
            createdGameReference.setValue(dbMatch);

            final String newGameRef = gameRef;

            matchmaker.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    if (mutableData.getValue(String.class).equals(NONE)) {
                        mutableData.setValue(newGameRef);
                        //Game ID aggiunto nel documento matchmaker del db
                        return Transaction.success(mutableData);
                    }

                    return Transaction.abort();
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    if (b) {
                        // Il gioco è stato aggiunto correttamente creato, ora aspettiamo il secondo
                        // giocatore.
                        waiting = true;
                        Log.d(TAG, "onComplete: Waiting for player 2");
                        waitingEvent = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                dbMatch = dataSnapshot.getValue(DBMatch.class);
                                for (MatchInteraction mi : interactions) {
                                    mi.onMatchUpdate(dbMatch);
                                }

                                if (!dbMatch.getPlayer2ID().equals(NONE) && !secondFound) {
                                    secondFound = true;
                                    createdGameReference.removeEventListener(this);

                                    for (MatchInteraction mi : interactions) {
                                        mi.onSecondPlayerFound(dbMatch);
                                    }
                                    createdGameReference.addValueEventListener(firstPlayerLogic);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                temporaryFailure();
                            }
                        };
                        createdGameReference.addValueEventListener(waitingEvent);
                        for (MatchMakerInteraction mmi : matchMakerInteractions) {
                            mmi.onBeignFirst(dbMatch);
                        }
                    } else {
                        createdGameReference.removeValue();
                        temporaryFailure();
                    }
                }
            });
        } else {
            DataUtility.getInstance().getProvinces().addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> province = new ArrayList<>();
                            for (DataSnapshot ds : dataSnapshot.getChildren())
                                province.add(ds.getKey());
                            Random r = new Random();
                            dbMatch = new DBMatch(player.getID(), NONE);
                            dbMatch.setPlayer1Province(province.remove(r.nextInt(province.size())));
                            dbMatch.setPlayer2Province(province.remove(r.nextInt(province.size())));
                            createdGameReference.setValue(dbMatch);
                            final String newGameRef = gameRef;

                            matchmaker.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    if (mutableData.getValue(String.class).equals(NONE)) {
                                        mutableData.setValue(newGameRef);
                                        //Game ID aggiunto nel documento matchmaker del db
                                        return Transaction.success(mutableData);
                                    }

                                    return Transaction.abort();
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b,
                                                       DataSnapshot dataSnapshot) {
                                    if (b) {
                                        // Il gioco è stato aggiunto correttamente creato, ora aspettiamo il secondo
                                        // giocatore.
                                        waiting = true;
                                        waitingEvent = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                dbMatch = dataSnapshot.getValue(DBMatch.class);
                                                for (MatchInteraction mi : interactions) {
                                                    mi.onMatchUpdate(dbMatch);
                                                }

                                                if (!dbMatch.getPlayer2ID().equals(NONE) && !secondFound) {
                                                    secondFound = true;
                                                    createdGameReference.removeEventListener(this);

                                                    for (MatchInteraction mi : interactions) {
                                                        mi.onSecondPlayerFound(dbMatch);
                                                    }
                                                    createdGameReference.addValueEventListener(firstPlayerLogic);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                temporaryFailure();
                                            }
                                        };
                                        waiting = true;
                                        createdGameReference.addValueEventListener(waitingEvent);
                                        for (MatchMakerInteraction mmi : matchMakerInteractions) {
                                            mmi.onBeignFirst(dbMatch);
                                        }
                                    } else {
                                        createdGameReference.removeValue();
                                        temporaryFailure();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );

        }
    }

    private void temporaryFailure() {
        for (MatchMakerInteraction mmi : matchMakerInteractions) {
            mmi.onTemporaryFailure();
            findMatch();
        }
    }

    private void secondArriver(final String gameRef) {
        matchmaker.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue(String.class).equals(gameRef)) {
                    mutableData.setValue(NONE);
                    return Transaction.success(mutableData);
                }
                return Transaction.abort();
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                if (b) {
                    final DatabaseReference gameReference = games.child(gameRef);
                    gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot1) {
                            dbMatch = dataSnapshot1.getValue(DBMatch.class);
                            dbMatch.setPlayer2ID(player.getID());
                            dbMatch.setGameRef(gameRef);
                            if (!matchMode.equals(RANDOM))
                                dbMatch.setPlayer2Province(player.getProvincia());
                            if (dbMatch == null)
                                Log.d(TAG, "onDataChange: dbmatch is null!");
                            Log.d(TAG, "onDataChange: DBMATCH" + dbMatch);
                            final DatabaseReference db = FirebaseDatabase.getInstance().getReference("provinces");

                            db.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int i = 0;
                                    if (dataSnapshot.hasChild(dbMatch.getPlayer1Province()) && dataSnapshot.hasChild(dbMatch.getPlayer2Province())) {
                                        Provincia provincia1 = dataSnapshot.child(dbMatch.getPlayer1Province()).getValue(Provincia.class);
                                        Provincia provincia2 = dataSnapshot.child(dbMatch.getPlayer2Province()).getValue(Provincia.class);
                                        dbMatch.setQuestions(new Match2(provincia1, provincia2).getQuestions());
                                        dbMatch.setCorrettePlayer1(0);
                                        dbMatch.setCorrettePlayer2(0);
                                        dbMatch.setSbagliatePlayer1(0);
                                        dbMatch.setSbagliatePlayer2(0);
                                        dbMatch.setPlayer1Status("online");
                                        dbMatch.setPlayer2Status("online");
                                        gameReference.runTransaction(new Transaction.Handler() {
                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                mutableData.setValue(dbMatch);
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                                if (b) {
                                                    gameReference.addListenerForSingleValueEvent(secondPlayerLogic);

                                                    for (MatchInteraction mi : interactions) {
                                                        mi.onSecondPlayerFound(dbMatch);
                                                    }

                                                }
                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "onDataChange: NO PROVINCES WITH THAT NAMEs");
                                        if (dataSnapshot.hasChild(dbMatch.getPlayer1Province()))
                                            Log.d(TAG, "onDataChange: Player one province (" + dbMatch.getPlayer1Province() + ") is recognized");
                                        if (dataSnapshot.hasChild(dbMatch.getPlayer2Province()))
                                            Log.d(TAG, "onDataChange: Player two province (" + dbMatch.getPlayer2Province() + ") is recognized");
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            Log.d(TAG, "onDataChange: provincia:" + ds.getKey());
                                            if (ds.getKey().equals(dbMatch.getPlayer1Province()))
                                                Log.d(TAG, "onDataChange: Player one province found!");
                                            if (ds.getKey().equals(dbMatch.getPlayer2Province()))
                                                Log.d(TAG, "onDataChange: Player two province found!");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    temporaryFailure();
                }
            }
        });

    }

    public void cancelSearch() {
        if (waiting && waitingEvent != null) {
            createdGameReference.removeEventListener(waitingEvent);
            waitingEvent = null;
            waiting = false;
            matchmaker.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    if (!mutableData.getValue(String.class).equals(NONE)) {
                        mutableData.setValue(NONE);
                        return Transaction.success(mutableData);
                    }

                    return Transaction.abort();
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (!b)
                        Log.d(TAG, "onComplete: CANNOT STOP SEARCHING, GET HELP!!!!");
                    else {
                        for (MatchMakerInteraction mmi : matchMakerInteractions) {
                            mmi.onStopSearching();
                        }
                    }
                }
            });
        }

    }


    public interface MatchMakerInteraction {
        void onTemporaryFailure();

        void onBeignFirst(DBMatch dbMatch);

        void onBeignSecond(DBMatch dbMatch);

        void onStopSearching();
    }

    public interface MatchInteraction {
        void onMatchUpdate(DBMatch DBMatch);

        void onSecondPlayerFound(DBMatch dbMatch);
    }


    private class FirstPlayerLogic implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onDataChange: LOGICA 1");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class SecondPlayerLogic implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onDataChange: LOGICA 2");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

}
