package com.heliohost.kakapo.datitalia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GameDatiPartita extends Fragment {

    private TextView nome;
    private RecyclerView rv;
    private List<GameDatiPrepartita> datiList = new ArrayList<>();
    private GameDatiAdapter mAdapter;
    private TextView nomeProv;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;
    private String player;
    private DBMatch dbMatch;

    public GameDatiPartita() {
        // Required empty public constructor
    }

    private void addElements() {
        mDatabase.child(player).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Prepartita", "onDataChange: ottenuti dati provincia :" + dataSnapshot.getKey());
                Provincia p = dataSnapshot.getValue(Provincia.class);
                Map<String, Long> mapUscite = p.getUscite();
                Map<String, Long> mapEntrate = p.getEntrate();
                for (Map.Entry<String, Long> mapElem : mapEntrate.entrySet()) {
                    if (mapUscite.containsKey(mapElem.getKey())) {
                        GameDatiPrepartita gameDatiPrepartita = new GameDatiPrepartita(mapElem.getKey(), mapElem.getValue(), mapUscite.get(mapElem.getKey()));
                        datiList.add(gameDatiPrepartita);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_dati_partita, container, false);

        progressBar = v.findViewById(R.id.progress_bar);
        nomeProv = v.findViewById(R.id.nome_prov);
        progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        Bundle bundle = getArguments();
        dbMatch = (DBMatch) bundle.getSerializable("dbMatch");
        player = (FirebaseAuth.getInstance().getUid().equals(dbMatch.getPlayer1ID())) ? dbMatch.getPlayer2Province() : dbMatch.getPlayer1Province();
        nomeProv.setText("Stai giocando contro: " + player);
        new CountDownTimer(10000, 50) {
            int i = 90;

            @Override
            public void onTick(long l) {
                progressBar.setProgress((int) l/100);
                i = i - 5;
            }

            @Override
            public void onFinish() {
                try {
                    Intent intent = new Intent(getContext().getApplicationContext(), GameActivity.class);
                    intent.putExtra("dbMatch", dbMatch);
                    startActivity(intent);
                }catch (Exception e){
                    Log.d("ERRORE", "onFinish: avvio bislacco");
                }

            }
        }.start();

        //Bundle bundle = this.getArguments();
        //nome = v.findViewById(R.id.nome_prov);
        //nome.setText(bundle.getString("pro"));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("provinces");
        mAdapter = new GameDatiAdapter(datiList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv = v.findViewById(R.id.recycle_view);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mAdapter);
        addElements();
        return v;
    }
}
