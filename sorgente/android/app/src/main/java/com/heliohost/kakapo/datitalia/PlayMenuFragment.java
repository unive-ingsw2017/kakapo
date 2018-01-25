package com.heliohost.kakapo.datitalia;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayMenuFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "PlayMenuFragment";



    public interface MenuListener{
        void onQuickGamePressed();
        void onInvitePlayerPressed();
        void onShowInvitesPressed();
    }

    private MenuListener mMenuListener = null;

    @Override
    public void onClick(View v) {
        if(mMenuListener != null) {
            switch (v.getId()) {
                case R.id.btn_quick_game:
                    mMenuListener.onQuickGamePressed();
                    break;
                case R.id.btn_invite_player:
                    mMenuListener.onInvitePlayerPressed();
                    break;
                case R.id.btn_show_invitations:
                    mMenuListener.onShowInvitesPressed();
                    break;
                default:
                    Log.d(TAG, "Default case reached");
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof  MenuListener){
            mMenuListener = (MenuListener)context;
        } else {
            Log.d(TAG, "onAttach: Fragment attached to activity not implementing MenuListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMenuListener = null;
    }

    public PlayMenuFragment() {
        // Required empty public constructor
        super();
    }

    public PlayMenuFragment createInstance(){
        PlayMenuFragment f = new PlayMenuFragment();

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_play_menu, container, false);

        v.findViewById(R.id.btn_quick_game).setOnClickListener(this);

        return v;
    }

    public void setMenuListener(MenuListener ml){
        this.mMenuListener = ml;
    }

}
