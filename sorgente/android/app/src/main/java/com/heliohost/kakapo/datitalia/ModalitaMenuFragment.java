package com.heliohost.kakapo.datitalia;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ModalitaMenuFragment extends Fragment implements View.OnClickListener {


    private MenuListener mMenuListener;

    public ModalitaMenuFragment() {
        super();
    }

    @Override
    public void onClick(View v) {
        if (mMenuListener != null) {
            switch (v.getId()) {
                case R.id.btn_nazionale:
                    mMenuListener.onNazionaleButtonPressed();
                    break;
                case R.id.btn_regionale:
                    mMenuListener.onRegionaleButtonPressed();
                    break;
                case R.id.btn_random:
                    mMenuListener.onRandomButtonPressed();
                    break;
                case R.id.btn_classifica:
                    mMenuListener.onClassificaButtonPressed();
                    break;
                case R.id.info_gioco:
                    mMenuListener.onInfoButtonPressed();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_modalita_menu, container, false);
        v.findViewById(R.id.btn_random).setOnClickListener(this);
        v.findViewById(R.id.btn_classifica).setOnClickListener(this);
        v.findViewById(R.id.btn_nazionale).setOnClickListener(this);
        v.findViewById(R.id.btn_regionale).setOnClickListener(this);
        v.findViewById(R.id.info_gioco).setOnClickListener(this);
        return v;
    }

    public void setMenuListener(MenuListener ml) {
        this.mMenuListener = ml;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MenuListener) {
            mMenuListener = (MenuListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMenuListener = null;
    }

    public interface MenuListener {
        void onNazionaleButtonPressed();

        void onRegionaleButtonPressed();

        void onRandomButtonPressed();

        void onClassificaButtonPressed();

        void onInfoButtonPressed();
    }
}
