package com.example.googleapiservices.presentation.activities.auth;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.googleapiservices.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class AuthActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "AuthActivity";

    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";

    private LoginButton authFacebookLoginButton;

    private SignInButton authSignInButton;

    private Button authSignOutButton;

    private CircleImageView authCircleImageVIew;

    private TextView authTxtName, authTxtEmail;

    private CallbackManager authCallBackManager;

    private View.OnClickListener googleSignInAction, googleSignOutAction;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        checkLoginStatus();
        checkSingInStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        authFacebookTokenTracker.stopTracking();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        initViews();
        initListeners();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        bindListeners();

        authCallBackManager = CallbackManager.Factory.create();
        authFacebookLoginButton.setReadPermissions(Arrays.asList(EMAIL, PUBLIC_PROFILE));

        authFacebookLoginButton.registerCallback(authCallBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        checkLoginStatus();
//        checkSingInStatus();
    }

    private void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            loadUserProfile(AccessToken.getCurrentAccessToken());
            authSignInButton.setVisibility(View.INVISIBLE);
        }
    }

    private void checkSingInStatus() {
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            updateUI(GoogleSignIn.getLastSignedInAccount(this));
            authSignInButton.setVisibility(View.INVISIBLE);
            authSignOutButton.setVisibility(View.VISIBLE);
        }

    }

    private void initViews() {
        authFacebookLoginButton = findViewById(R.id.loginBtnAuthFacebook);
        authCircleImageVIew = findViewById(R.id.profileAuthPic);
        authTxtName = findViewById(R.id.profileAuthName);
        authTxtEmail = findViewById(R.id.profileAuthEmail);
        authSignInButton = findViewById(R.id.google_sign_in_button);
        authSignInButton.setSize(SignInButton.SIZE_WIDE);
        authSignInButton.setColorScheme(SignInButton.COLOR_DARK);
        authSignOutButton = findViewById(R.id.google_sign_out_button);
    }

    private void initListeners() {
        googleSignInAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        };
        googleSignOutAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        };
    }

    private void bindListeners() {
        authSignInButton.setOnClickListener(googleSignInAction);
        authSignOutButton.setOnClickListener(googleSignOutAction);
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            authTxtName.setText("");
            authTxtEmail.setText("");
            authCircleImageVIew.setImageResource(0);
            authSignOutButton.setVisibility(View.INVISIBLE);
        } else {
            authFacebookLoginButton.setVisibility(View.INVISIBLE);
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            Uri personPhoto = account.getPhotoUrl();
            authTxtName.setText(personName);
            authTxtEmail.setText(personEmail);
            Glide.with(AuthActivity.this).load(personPhoto).into(authCircleImageVIew);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        authTxtName.setText("");
        authTxtEmail.setText("");
        authCircleImageVIew.setImageResource(0);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
        authFacebookLoginButton.setVisibility(View.VISIBLE);
        authSignOutButton.setVisibility(View.INVISIBLE);
        authSignInButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        authCallBackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
            authSignOutButton.setVisibility(View.VISIBLE);
            authSignInButton.setVisibility(View.INVISIBLE);
            authFacebookLoginButton.setVisibility(View.INVISIBLE);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    AccessTokenTracker authFacebookTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                authTxtName.setText("");
                authTxtEmail.setText("");
                authCircleImageVIew.setImageResource(0);
                Toast.makeText(AuthActivity.this, "Successful log out", Toast.LENGTH_LONG).show();
                authSignInButton.setVisibility(View.VISIBLE);
            } else {
                loadUserProfile(currentAccessToken);
                authSignInButton.setVisibility(View.INVISIBLE);
            }

        }
    };

    private void loadUserProfile(AccessToken newAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    authTxtEmail.setText(email);
                    authTxtName.setText(first_name + " " + last_name);

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();

                    Glide.with(AuthActivity.this).load(image_url).into(authCircleImageVIew);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        Bundle parametrs = new Bundle();
        parametrs.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parametrs);
        request.executeAsync();
    }
}

