package com.heliohost.kakapo.datitalia;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

public class MapActivity extends AppCompatActivity implements ChildEventListener {

    private final String TAG = "MapActivity";

    private DatabaseReference mProvinceRef;
    private Map<String, Provincia> provinces;
    private List<String> comparti = new ArrayList<>();
    private String compartoFilter = null;
    private AdapterView.OnItemSelectedListener mSpinnerListener;
    private ArrayAdapter<String> mSpinnerAdapter;

    private Geocoder geocoder;
    private MapManager mapManager;
    private Map<Marker, String> markers;
    private Map<Marker, Provincia> provByMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DataUtility.getInstance().googleConnection(this, this);

        if (savedInstanceState != null)
            compartoFilter = savedInstanceState.getString("compartoFilter", null);

        setupMap();
        //setupText();
        setupSpinner();
        setupData();


    }

    void setupData() {
        if (mProvinceRef == null)
            mProvinceRef = DataUtility.getInstance().getProvinces();
        if (provinces == null)
            provinces = new HashMap<>();
    }

    /*
        void setupText() {
            if (compartoFilter != null) {
                findViewById(R.id.map_text).setVisibility(View.VISIBLE);
                findViewById(R.id.map_filtered).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.map_text)).setText(R.string.filtered_provinces);
                ((TextView) findViewById(R.id.map_filtered)).setText(compartoFilter);
            } else {
                findViewById(R.id.map_text).setVisibility(View.VISIBLE);
                findViewById(R.id.map_filtered).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.map_text)).setText(R.string.all_provinces);
            }
        }
    */
    void setupMap() {
        markers = new HashMap<>();
        provByMarker = new HashMap<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_container, mapFragment);
        fragmentTransaction.commit();
        mapManager = new MapManager(mapFragment, getApplicationContext()) {
            @Override
            public void onMapLoaded() {
                super.onMapLoaded();
                mProvinceRef.addChildEventListener(MapActivity.this);
            }

            @Override
            public void onMapReady(GoogleMap googleMap) {
                super.onMapReady(googleMap);
                googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
            }
        };
        // getSupportFragmentManager().beginTransaction().replace(R.id.map_fragment_container, mapFragment).commit();

        mapManager.setOnInfoWindowClickListener(new MapManager.MapListener() {
            @Override
            public void onInfoWindowClick(Marker marker, Object key) {
                if (key instanceof String)
                    goProvincia((String) key);
            }
        });
        mapManager.startLoadMap();
        geocoder = new Geocoder(this, Locale.ITALY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupData();
        setupSpinner();
        //Sincronizzazione con firebase va fatta dopo che la mappa sia pronta se si usa un singolo thread.
        //mProvinceRef.addChildEventListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mProvinceRef.removeEventListener(this);    //detach listener
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState == null)
            outState = new Bundle();
        outState.putString("compartoFilter", compartoFilter);
        super.onSaveInstanceState(outState);
    }

    /* Method which take the user to provincia details when marker in map is clicked */
    public void goProvincia(String name) {
        Log.d(TAG, "goProvincia: Changing fragment");
        if (name == null)
            return;
        Bundle provinciaBundle = new Bundle();
        provinciaBundle.putString("provincia", name);
        provinciaBundle.putSerializable("objprov", provinces.get(name));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ProvinceDetails provinceDetails = new ProvinceDetails();
        provinceDetails.setArguments(provinciaBundle);
        fragmentTransaction.replace(R.id.map_fragment_container, provinceDetails, "Visualizza Dati provincia").addToBackStack("visualizza_dati").commit();

    }

    @Override
    public void onDestroy() {
        markers.clear();
        markers = null;
        if (mapManager != null) {
            mapManager.onDestroy();
            mapManager = null;
        }
        geocoder = null;
        provinces.clear();
        provinces = null;
        destroySpinner();
        super.onDestroy();
    }

    void showProvincia(final Provincia provincia) {

        if (compartoFilter == null || provincia.getEntrate().containsKey(compartoFilter) || provincia.getUscite().containsKey(compartoFilter)) {
            Log.d(TAG, provincia.getNomeProvincia() + " si trova in (" + provincia.getLatitude() + "," + provincia.getLongitude() + ")");
            String snippet = "";
            if (compartoFilter != null) {
                long entrata = 0;
                if (provincia.getEntrate().containsKey(compartoFilter)) {
                    entrata = provincia.getEntrate().get(compartoFilter);
                }
                long uscita = 0;
                if (provincia.getUscite().containsKey(compartoFilter)) {
                    uscita = provincia.getUscite().get(compartoFilter);
                }
                snippet = getString(R.string.map_snippet, ((double)entrata / 100), ((double)uscita / 100));
                Log.d(TAG, "showProvincia: SNIPPET :" + snippet);
            }
            mapManager.insertMarker(
                    DataUtility.getInstance().getProvinciaId(provincia), // internal id, use only alphanumeric characters
                    provincia.getLatitude(), provincia.getLongitude(), // latitude and longitude
                    provincia.getNomeProvincia() + "(" + provincia.getRegione() + ")", // displayed name
                    snippet
            );
        }
    }

    void addProvincia(Provincia provincia) {
        provinces.put(DataUtility.getInstance().getProvinciaId(provincia), provincia);
        TreeSet<String> compartiSet = new TreeSet<>(comparti); // TreeSet is sorted
        compartiSet.addAll(provincia.getEntrate().keySet());
        comparti.clear();
        comparti.addAll(compartiSet);
        mSpinnerAdapter.notifyDataSetChanged();
        showProvincia(provincia);
    }

    void removeProvincia(Provincia provincia) {
        provinces.remove(DataUtility.getInstance().getProvinciaId(provincia));
        mapManager.removeMarker(DataUtility.getInstance().getProvinciaId(provincia));
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "A new province has come!");
        if (!dataSnapshot.hasChild("longitude") || !dataSnapshot.hasChild("latitude")) {
            Log.d(TAG, "onChildAdded: WOW STA PROVINCIA NON HA LONGITUDINE O LATITUDINE");
            List<Address> addresses;
            try {

                Provincia provincia = dataSnapshot.getValue(Provincia.class);
                Log.d(TAG, "onChildAdded: Cercando coordinate per :" + provincia.getNomeProvincia());
                addresses = geocoder.getFromLocationName(provincia.getNomeProvincia() + ", " + provincia.getRegione() + ", Italia", 1);
                provincia.setLongitude(addresses.get(0).getLongitude());
                provincia.setLatitude(addresses.get(0).getLatitude());
                dataSnapshot.getRef().setValue(provincia);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Ricevuta una provincia che non è stato possibile aggiungere alla mappa perché non si trovano le coordinate.");
            }
        }

        try {
            Provincia provincia = dataSnapshot.getValue(Provincia.class);
            Log.d(TAG, "Its name is " + provincia.getNomeProvincia() + "!" + provincia.getLatitude() + "," + provincia.getLongitude());
            addProvincia(provincia);
        } catch (Exception e) {
            Log.d(TAG, "Ricevuta una provincia che non è stato possibile elaborare per aggiungerla alla mappa.");
            Log.d(TAG, dataSnapshot.toString());
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "A province has been removed!");
        try {
            Provincia provincia = dataSnapshot.getValue(Provincia.class);
            Log.d(TAG, "Its name is " + provincia.getNomeProvincia() + "!");
            removeProvincia(provincia);
        } catch (Exception e) {
            Log.d(TAG, "Ricevuta una provincia che non è stato possibile rimuovere dalla mappa.");
            Log.d(TAG, dataSnapshot.toString());
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        onChildRemoved(dataSnapshot);
        onChildAdded(dataSnapshot, s);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "Database error listening for provinces");
        Log.d(TAG, databaseError.toString());
    }

    void refreshProvinces() {
        mapManager.clearMarkers();
        for (Provincia provincia : provinces.values()) {
            showProvincia(provincia);
        }
    }

    void setupSpinner() {
        if (mSpinnerListener == null)
            mSpinnerListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "Selected " + position + "° item from dropdown");
                    compartoFilter = comparti.get(position);
                    if (compartoFilter != null)
                        Log.d(TAG, "that seems to correspond to " + compartoFilter);
                    else
                        Log.d(TAG, "that seems to correspond to nothing");
                    //setupText();
                    refreshProvinces();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    compartoFilter = null;
                    //setupText();
                    refreshProvinces();
                }
            };
        Spinner spinner = findViewById(R.id.comparti_spinner);
        spinner.setOnItemSelectedListener(null);
        spinner.setOnItemSelectedListener(mSpinnerListener);
        if (mSpinnerAdapter == null) {
            mSpinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, comparti);
        }
        spinner.setAdapter(null);
        spinner.setAdapter(mSpinnerAdapter);
        if (compartoFilter != null)
            spinner.setSelection(comparti.indexOf(compartoFilter));
        spinner.setPrompt(getString(R.string.select_comparto));
    }

    void destroySpinner() {
        Spinner spinner = findViewById(R.id.comparti_spinner);
        spinner.setOnItemSelectedListener(null);
        spinner.setAdapter(null);
        mSpinnerListener = null;
        mSpinnerAdapter = null;
        comparti.clear();
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
        if (id == R.id.impostazioni) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_provincia, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            final String[] content = marker.getSnippet().split("\n");

            TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.nome_prov));
            tvTitle.setText(marker.getTitle());
            TextView tvEntrata = ((TextView) myContentsView.findViewById(R.id.entrata_prov));
            tvEntrata.setText(content[0]);
            TextView tvUscita = ((TextView) myContentsView.findViewById(R.id.uscita_prov));
            tvUscita.setText(content[1]);
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }
}