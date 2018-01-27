package com.heliohost.kakapo.datitalia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Classifica extends AppCompatActivity {
    public static final String TAG = AppCompatActivity.class.getSimpleName();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private TextView punteggioPlayer;
    private TextView posizionePlayer;
    private RecyclerView rv;
    private List<User> datiList = new ArrayList<>();
    private ClassificaAdapter mAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifica);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DataUtility.getInstance().googleConnection(this, this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        Query queryRef = databaseReference.child("users").orderByChild("points");
        mAdapter = new ClassificaAdapter(datiList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv = findViewById(R.id.recycle_view);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mAdapter);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int i = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                datiList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String username = data.child("username").getValue(String.class);
                    String provincia = data.child("provincia").getValue(String.class);
                    Long point = data.child("points").getValue(Long.class);
                    point *= -1;
                    String points = new String("" + point);
                    User user = new User(username, Integer.parseInt(points), provincia);
                    datiList.add(user);
                    i++;
                    if (data.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                        punteggioPlayer = findViewById(R.id.punteggio_player);
                        posizionePlayer = findViewById(R.id.posizione_player);
                        Log.d(TAG, "ENTRO");
                        posizionePlayer.setText(getString(R.string.posizione_player) + "  " + i);
                        punteggioPlayer.setText(getString(R.string.punteggio_player) + "  " + points + " pt");
                    }
                    mAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            DataUtility.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
