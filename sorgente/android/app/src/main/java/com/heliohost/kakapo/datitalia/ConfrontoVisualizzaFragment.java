package com.heliohost.kakapo.datitalia;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ConfrontoVisualizzaFragment extends Fragment {

    private TextView nome1;
    private TextView nome2;
    private RecyclerView rv;
    private List<ConfrontoDati> datiList = new ArrayList<>();
    private ConfrontoAdapter mAdapter;
    private String prov1, prov2;
    private Provincia provincia1, provincia2;
    private Set<String> entrate;
    private ProgressBar progressBar;

    public ConfrontoVisualizzaFragment() {
        super();
    }

    private void addElements() {

        for (String entrata : entrate) {
            ConfrontoDati dati = new ConfrontoDati(entrata,
                    provincia1.getEntrate().get(entrata), provincia2.getEntrate().get(entrata),
                    provincia1.getUscite().get(entrata), provincia2.getUscite().get(entrata));
            datiList.add(dati);
        }
        progressBar.setVisibility(View.INVISIBLE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prov1 = this.getArguments().getString("prov1");
        prov2 = this.getArguments().getString("prov2");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_confronto_visualizza, container, false);

        Bundle bundle = this.getArguments();
        nome1 = v.findViewById(R.id.nome_prov_1);
        nome2 = v.findViewById(R.id.nome_prov_2);
        progressBar = v.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        nome1.setText(bundle.getString("prov1"));
        nome2.setText(bundle.getString("prov2"));

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv = v.findViewById(R.id.recycle_view);

        DataUtility.getInstance().getProvinces().addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        provincia1 = dataSnapshot.child(prov1).getValue(Provincia.class);
                        provincia2 = dataSnapshot.child(prov2).getValue(Provincia.class);
                        entrate = new HashSet<>(provincia1.getEntrate().keySet());
                        entrate.retainAll(provincia2.getEntrate().keySet());
                        entrate.retainAll(provincia1.getUscite().keySet());
                        entrate.retainAll(provincia2.getUscite().keySet());

                        mAdapter = new ConfrontoAdapter(datiList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                        rv.setLayoutManager(mLayoutManager);
                        rv.setItemAnimator(new DefaultItemAnimator());
                        rv.setAdapter(mAdapter);

                        addElements();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        return v;
    }

}
