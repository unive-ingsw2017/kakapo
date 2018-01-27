package com.heliohost.kakapo.datitalia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


public class GameCaricamentoPROVVISORIO extends Fragment {

    public static final String TAG = AppCompatActivity.class.getSimpleName();
    boolean flag = false;
    private ProgressBar progressBar;
    private GameCaricamentoPROVVISORIO context = this;

    public GameCaricamentoPROVVISORIO() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_game_caricamento_provvisorio, container, false);
    }

    private void changeFragment() {
        Log.d(TAG, "Avvio fragment");
        Bundle bundle = new Bundle();
        DBMatch dbMatch = (DBMatch) bundle.getSerializable("dbMatch");
        Intent intent = new Intent(this.getContext(), GameActivity.class);
        intent.putExtra("dbRef", dbMatch);
        startActivity(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Thread abortito");
    }

}
