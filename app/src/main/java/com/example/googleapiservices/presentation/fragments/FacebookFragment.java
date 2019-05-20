package com.example.googleapiservices.presentation.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.googleapiservices.R;
import com.example.googleapiservices.model.User;
import com.example.googleapiservices.presentation.activities.auth.AuthContract;

public class FacebookFragment extends Fragment implements AuthContract.AuthCallback {

    private AuthContract.AuthListener mListener;

    public FacebookFragment() {
        // Required empty public constructor
    }

    public static FacebookFragment newInstance() {
        return  new FacebookFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_facebook, container, false);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthContract.AuthListener) {
            mListener = (AuthContract.AuthListener) context;
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

    @Override
    public void showData(User user) {
        //todo show data about autharizated user
    }
}
