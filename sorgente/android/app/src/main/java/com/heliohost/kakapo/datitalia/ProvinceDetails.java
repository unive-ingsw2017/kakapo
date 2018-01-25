package com.heliohost.kakapo.datitalia;

import android.app.Fragment;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ProvinceDetails extends android.support.v4.app.Fragment {
    private TextView nome;
    private RecyclerView rv;
    private List<GameDatiPrepartita> datiList = new ArrayList<>();
    private GameDatiAdapter mAdapter;
    private DatabaseReference mDatabase;
    private String provincia;
    private TextView nomeProv;

    public ProvinceDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_province_details, container, false);
        Bundle bundle = getArguments();
        provincia = bundle.getString("provincia");
        Provincia p = (Provincia) bundle.getSerializable("objprov");
        nomeProv = v.findViewById(R.id.nome_prov);
        nomeProv.setText(provincia);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("provinces");
        mAdapter = new GameDatiAdapter(datiList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rv = v.findViewById(R.id.recycle_view);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mAdapter);
        Map<String,Long> mapUscite = p.getUscite();
        Map<String,Long> mapEntrate = p.getEntrate();
        for (Map.Entry<String,Long> mapElem: mapEntrate.entrySet()) {
            if(mapUscite.containsKey(mapElem.getKey())) {
                GameDatiPrepartita gameDatiPrepartita = new GameDatiPrepartita(mapElem.getKey(), mapElem.getValue(), mapUscite.get(mapElem.getKey()));
                datiList.add(gameDatiPrepartita);
            }
        }
        mAdapter.notifyDataSetChanged();
        return v;
    }
}
