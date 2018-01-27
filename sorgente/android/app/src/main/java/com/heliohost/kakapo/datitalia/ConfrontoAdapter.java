package com.heliohost.kakapo.datitalia;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Simone on 29/12/2017.
 */

public class ConfrontoAdapter extends RecyclerView.Adapter<ConfrontoAdapter.MyViewHolder> {

    private List<ConfrontoDati> datiList;

    public ConfrontoAdapter(List<ConfrontoDati> datiList) {
        this.datiList = datiList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_confronto, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ConfrontoDati confrontoDati = datiList.get(position);
        holder.entrataProvincia1.setText("" + (confrontoDati.getEntrataProvincia1() / 100));
        holder.entrataProvincia2.setText("" + (confrontoDati.getEntrataProvincia2() / 100));
        holder.uscitaProvincia1.setText("" + (confrontoDati.getUscitaProvincia1() / 100));
        holder.uscitaProvincia2.setText("" + (confrontoDati.getUscitaProvincia2() / 100));
        holder.titolo.setText(confrontoDati.getTitolo());
    }

    @Override
    public int getItemCount() {
        return datiList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView entrataProvincia1, entrataProvincia2, uscitaProvincia1, uscitaProvincia2, titolo;

        public MyViewHolder(View view) {
            super(view);
            entrataProvincia1 = view.findViewById(R.id.entrata_prov1);
            entrataProvincia2 = view.findViewById(R.id.entrata_prov2);
            uscitaProvincia1 = view.findViewById(R.id.uscita_prov1);
            uscitaProvincia2 = view.findViewById(R.id.uscita_prov2);
            titolo = view.findViewById(R.id.titolo);
        }
    }
}
