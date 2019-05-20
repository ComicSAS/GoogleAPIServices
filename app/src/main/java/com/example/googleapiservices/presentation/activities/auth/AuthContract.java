package com.example.googleapiservices.presentation.activities.auth;

import com.example.googleapiservices.model.User;

public interface AuthContract {

    // todo rename to AuthType after create ViewPager
    enum AuthFlow {
        GOOGLE, FACEBOOK, DEFAULT
    }

    interface AuthListener {
        void socialAuth(AuthContract.AuthFlow type, AuthContract.AuthCallback callback);
        void openScreen(AuthContract.AuthFlow type);
    }

    interface AuthCallback {
        void showData(User user);
    }

}
