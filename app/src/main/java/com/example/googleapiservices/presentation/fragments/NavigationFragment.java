package com.example.googleapiservices.presentation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.googleapiservices.R;
import com.example.googleapiservices.presentation.activities.auth.AuthContract;


public class NavigationFragment extends Fragment {

    private AuthContract.AuthListener mListener;

    private View.OnClickListener onGoogleAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.openScreen(AuthContract.AuthFlow.GOOGLE);
        }
    };

    private View.OnClickListener onFacebookAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.openScreen(AuthContract.AuthFlow.FACEBOOK);
        }
    };

    public NavigationFragment() {
        // Required empty public constructor
    }
    public static NavigationFragment newInstance() {
        return new NavigationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navigation, container, false);
        Button googleScreen = v.findViewById(R.id.btn_nav_auth_google);
        googleScreen.setOnClickListener(onGoogleAction);
        Button facebookScreen = v.findViewById(R.id.btn_nav_auth_facebook);
        facebookScreen.setOnClickListener(onFacebookAction);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthContract.AuthListener) {
            mListener = ( AuthContract.AuthListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
