package com.heliohost.kakapo.datitalia;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConfrontoSceltaFragment extends Fragment {

    private Spinner spinnerProvincia1;
    private Spinner spinnerProvincia2;
    private Button confrontaButton;
    private ArrayList<Provincia> province;
    private int lastprov1 = 0;
    private int lastprov2 = 0;
    //private ArrayAdapter<Provincia> provAdapter;
    private ValueEventListener provincesUpdated;
    private DatabaseReference provinces;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_confronto_scelta, container, false);
        spinnerProvincia1 = v.findViewById(R.id.spinner_provincia2);
        spinnerProvincia2 = v.findViewById(R.id.spinner_provincia1);
        confrontaButton = v.findViewById(R.id.btn_confronta);

        confrontaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("prov1", spinnerProvincia1.getSelectedItem().toString());
                bundle.putString("prov2", spinnerProvincia2.getSelectedItem().toString());
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ConfrontoVisualizzaFragment confrontoVisualizzaFragment = new ConfrontoVisualizzaFragment();
                confrontoVisualizzaFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_container, confrontoVisualizzaFragment, "Visualizza Confronto");
                fragmentTransaction.addToBackStack("A");
                fragmentTransaction.commit();
            }
        });
        return v;
    }

    public List<Provincia> getProvinceScelte() {
        ArrayList<Provincia> scelte = new ArrayList<>();
        scelte.add(province.get(lastprov1));
        scelte.add(province.get(lastprov2));
        return scelte;
    }

}
