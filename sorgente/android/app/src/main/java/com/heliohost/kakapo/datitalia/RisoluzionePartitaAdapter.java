package com.heliohost.kakapo.datitalia;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by simonescaboro on 22/01/18.
 */

public class RisoluzionePartitaAdapter   extends RecyclerView.Adapter<RisoluzionePartitaAdapter.MyViewHolder> {

    private List<RisoluzionePartita> datiList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView domanda, risposta1, risposta2, rispostaGiusta;

        public MyViewHolder(View view) {
            super(view);
            risposta1 = view.findViewById(R.id.risposta_1);
            risposta2 = view.findViewById(R.id.risposta_2);
            rispostaGiusta = view.findViewById(R.id.risposta_giusta);
            domanda = view.findViewById(R.id.domanda);
        }
    }
    public RisoluzionePartitaAdapter(List<RisoluzionePartita> datiList){
        this.datiList=datiList;
    }
    @Override
    public RisoluzionePartitaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_risoluzione_partita, parent, false);

        return new RisoluzionePartitaAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RisoluzionePartitaAdapter.MyViewHolder holder, int position) {
        RisoluzionePartita gameDati = datiList.get(position);
        holder.rispostaGiusta.setText("" + gameDati.getRispostaEsatta());
        holder.risposta1.setText("" + gameDati.getRispostaPlayer1());
        holder.risposta2.setText("" + gameDati.getRispostaPlayer2());
        holder.domanda.setText(gameDati.getDomanda());
    }

    @Override
    public int getItemCount() {
        return datiList.size();
    }
}