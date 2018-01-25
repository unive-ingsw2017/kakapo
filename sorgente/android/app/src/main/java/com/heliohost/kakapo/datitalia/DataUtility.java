package com.heliohost.kakapo.datitalia;

import android.content.Context;
import android.location.Address;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gregory on 1/18/2018.
 */

public class DataUtility {

    private static DataUtility instance = null;
    private FirebaseDatabase db;
    private DatabaseReference users;
    private DatabaseReference provinces;
    private DatabaseReference games;
    private FirebaseUser firebaseUser;
    private GoogleApiClient mGoogleApiClient;

    private Map<Provincia, Address> provinciaAddressMap;

    private DataUtility() {
        db = FirebaseDatabase.getInstance();
        users = db.getReference("users");
        provinces = db.getReference("provinces");
        games = db.getReference("games");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        provinciaAddressMap = new HashMap<>();
    }

    public static DataUtility getInstance() {
        if (instance == null) {
            instance = new DataUtility();
        }
        return instance;
    }

    public void googleConnection(Context context, FragmentActivity fragmentActivity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(fragmentActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    public DatabaseReference getUsers() {
        return users;
    }

    public DatabaseReference getProvinces() {
        return provinces;
    }

    public DatabaseReference getGames() {
        return games;
    }

    String getProvinciaId(Provincia provincia) {
        return provincia.getNomeProvincia().replaceAll("[^a-zA-Z0-9]+", "");
    }

    public Map<Provincia, Address> getProvinciaAddressMap() {
        return provinciaAddressMap;
    }
}
