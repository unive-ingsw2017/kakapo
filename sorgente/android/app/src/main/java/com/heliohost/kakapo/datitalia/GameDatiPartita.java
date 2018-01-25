package com.heliohost.kakapo.datitalia;

import android.content.Intent;
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
    private String[] staticComparti = new String[] {"comparto1", "comparto2", "compart32","comparto4","comparto5","comparto6","comparto7","comparto8","comparto9","comparto10","comparto11"};
    private TextView timer;
    private TextView nomeProv;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;
    private String player;
    private DBMatch dbMatch;

    public GameDatiPartita() {
        // Required empty public constructor
    }

    private void addElements(){
        mDatabase.child(player).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Prepartita", "onDataChange: ottenuti dati provincia :"+dataSnapshot.getKey());
                Provincia p = dataSnapshot.getValue(Provincia.class);
                Map<String,Long> mapUscite = p.getUscite();
                Map<String,Long> mapEntrate = p.getEntrate();
                for (Map.Entry<String,Long> mapElem: mapEntrate.entrySet()) {
                    if(mapUscite.containsKey(mapElem.getKey())) {
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

        timer = v.findViewById(R.id.timer);
        progressBar = v.findViewById(R.id.progress_bar);
        nomeProv = v.findViewById(R.id.nome_prov);

        Bundle bundle = getArguments();
        dbMatch = (DBMatch) bundle.getSerializable("dbMatch");
        player = (FirebaseAuth.getInstance().getUid().equals(dbMatch.getPlayer1ID())) ? dbMatch.getPlayer2Province() : dbMatch.getPlayer1Province();
        nomeProv.setText("Stai giocando contro:" + player);
        new CountDownTimer(5000,1000){
            int i = 90;

            @Override
            public void onTick(long l) {
                timer.setText(""+l/1000);

                progressBar.setProgress(i);
                i = i - 5;
            }

            @Override
            public void onFinish() {
                timer.setText("finito");
                //Match match = new Match("Sassari","Treviso");
                Intent intent = new Intent(getContext(),GameActivity.class);
                intent.putExtra("dbMatch",dbMatch);
                startActivity(intent);
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
