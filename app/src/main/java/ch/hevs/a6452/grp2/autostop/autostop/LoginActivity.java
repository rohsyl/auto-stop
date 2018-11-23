package ch.hevs.a6452.grp2.autostop.autostop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1212;

    @BindView(R.id.googleLogo)
    protected ImageView imgGoogle;

    @BindView(R.id.loginText)
    protected TextView lblLogin;

    @BindView(R.id.loggedout)
    protected TextView btnLogout;

    @BindView(R.id.sign_in_button)
    protected SignInButton signInButton;

    protected GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imgGoogle.setImageResource(R.drawable.logoGoogle);
        lblLogin.setText(R.string.label_google_login);
        btnLogout.setText(R.string.label_logout_button);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        //Configure the sign-in requirements : ID, email and profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        //Create client based on what is defined in gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result returned from launching the intent from getSignInIntent
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            //Sign in successfully
            updateUI(account);
        } catch (ApiException e) {
            Log.w("","SignIn failcode : " + e.getStatusCode());
            //Return failure details
            updateUI(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check for existing sign in account, if a client is already logged it will not be null
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }

    private void updateUI(GoogleSignInAccount account) {
        //Check if account is null
        if(account == null){
            //If null : display the sign in button
        }
        else {
            //Check if profile has been setup
            //If no : redirect to "Profile" activity
            //If yes : redirect to "Start trip" activity
        }
    }
}
