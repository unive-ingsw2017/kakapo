package com.heliohost.kakapo.datitalia;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Simone on 30/12/2017.
 */

public class GameDatiAdapter extends RecyclerView.Adapter<GameDatiAdapter.MyViewHolder> {

    private List<GameDatiPrepartita> datiList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView entrataProvincia, uscitaProvincia, titolo;

        public MyViewHolder(View view) {
            super(view);
            entrataProvincia = view.findViewById(R.id.entrata_prov);
            uscitaProvincia = view.findViewById(R.id.uscita_prov);
            titolo = view.findViewById(R.id.titolo);
        }
    }
    public GameDatiAdapter(List<GameDatiPrepartita> datiList){
        this.datiList=datiList;
    }
    @Override
    public GameDatiAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_game_dati, parent, false);

        return new GameDatiAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GameDatiAdapter.MyViewHolder holder, int position) {
        GameDatiPrepartita gameDati = datiList.get(position);
        holder.entrataProvincia.setText("" + (gameDati.getEntrateProvincia()/100));
        holder.uscitaProvincia.setText("" + (gameDati.getUsciteProvincia()/100));
        holder.titolo.setText(gameDati.getTitolo());
    }

    @Override
    public int getItemCount() {
        return datiList.size();
    }
}