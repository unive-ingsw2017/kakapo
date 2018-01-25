package com.heliohost.kakapo.datitalia;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by simonescaboro on 16/01/18.
 */

class ClassificaAdapter extends RecyclerView.Adapter<ClassificaAdapter.MyViewHolder> {

    private List<User> userList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nomePlayer, punteggio;

        public MyViewHolder(View view) {
            super(view);
            punteggio = view.findViewById(R.id.punteggio);
            nomePlayer = view.findViewById(R.id.nome_player);
        }
    }
    public ClassificaAdapter(List<User> userList){
        this.userList=userList;
    }

    @Override
    public ClassificaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_classifica, parent, false);

        return new ClassificaAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ClassificaAdapter.MyViewHolder holder, int position) {
        User userDati = userList.get(position);
        holder.nomePlayer.setText(userDati.getUsername() + " ("+userDati.getProvincia()+")");
        holder.punteggio.setText("" + userDati.getPoints() + " pt");
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
