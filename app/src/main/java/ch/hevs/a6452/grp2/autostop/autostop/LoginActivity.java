package ch.hevs.a6452.grp2.autostop.autostop;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.PersonEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.FirebaseCallBack;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.FirebaseManager;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.PotostopSession;

public class LoginActivity extends AppCompatActivity {
    //Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    //Users created and tested
    private PersonEntity userEntity;
    private PersonEntity checkUser = null;
    //Layout elements
    @BindView(R.id.email_input)
    EditText emailInput;
    @BindView(R.id.password_input)
    EditText passwordInput;
    @BindView(R.id.login_button)
    Button login;
    @BindView(R.id.create_account_button)
    Button createAccount;
    Toast loginFailed;
    Toast registerFailed;
    Toast passwordInvalid;
    Toast emailInvalid;

    //GOOGLE LOGIN REFERENCES
    private static final int RC_SIGN_IN = 1212;
    @BindView(R.id.loginText)
    protected TextView lblLogin;

    @BindView(R.id.sign_in_button)
    protected SignInButton signInButton;

    protected GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        //Setup Toasts
        loginFailed = Toast.makeText(this, R.string.toast_could_not_log_in, Toast.LENGTH_LONG);
        loginFailed.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);

        loginFailed = Toast.makeText(this, R.string.toast_could_not_sign_in, Toast.LENGTH_LONG);
        loginFailed.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);

        passwordInvalid = Toast.makeText(this, R.string.toast_invalid_password, Toast.LENGTH_LONG);
        passwordInvalid.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);

        emailInvalid = Toast.makeText(this, R.string.toast_invalid_email, Toast.LENGTH_LONG);
        emailInvalid.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptFirebaseLogin();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerOnFirebase();
            }
        });

        //GOOGLE LOGIN REFERENCES
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Create client based on what is defined in gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Sign in button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check for existing sign in account, if a client is already logged it will not be null
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
            updateUI(null);
        else
            getUserAndChangeUI(currentUser.getUid());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                loginFailed.show();
            }
        }
    }

    private boolean fetchMailAndPassword(String email, String password){
        //Check password validity
        if(!TextUtils.isEmpty(password) && !isPasswordValid(password)){
            passwordInvalid.show();
            return false;
        }
        //Check email validity
        else if (!isEmailValid(email)){
            emailInvalid.show();
            return false;
        }

        return true;
    }

    private void attemptFirebaseLogin() {
        //Fetch inputs
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        //If something went wrong, focus the mismatching field
        if(!fetchMailAndPassword(email, password)){

        }
        //If email and password are OK, attempt to log in
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Login succeed : start main activity
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                FirebaseManager.getUser(user.getUid(), new FirebaseCallBack() {
                                    @Override
                                    public void onCallBack(Object o) {
                                        userEntity = (PersonEntity) o;
                                        updateUI(userEntity);
                                    }
                                });
                            }
                            //Login failed : show errors
                            else {
                                loginFailed.show();
                                passwordInput.requestFocus();
                            }
                        }
                    });
        }
    }

    private void registerOnFirebase() {
        //Fetch inputs
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        //If something went wrong, focus the mismatching field
        if(!fetchMailAndPassword(email, password)){
            loginFailed.show();
        }
        //Email and password OK : attempt to register new user
        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //If the account is created : go to the profile form
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                //Insert the user in the DB
                                insertUserInDb(user);
                                getUserAndChangeUI(user.getUid());
                            }
                            //Email was incorrect : do nothing
                            else {
                                registerFailed.show();
                                emailInput.requestFocus();
                            }
                        }
                    });
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Insert the user in the DB
                            //If user exists, it won't overwrite the existing values
                            if(!userExists())
                                insertUserInDb(user);
                            // Sign in success, update UI with the signed-in user's information
                            getUserAndChangeUI(mAuth.getCurrentUser().getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            loginFailed.show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void insertUserInDb(FirebaseUser user) {
        PersonEntity emptyUser = new PersonEntity();
        //Mail is automatically set from the FirebaseUser
        emptyUser.setEmail(mAuth.getCurrentUser().getEmail());
        //Insert the user in the DB, with the same UID as the one in the FirebaseAuth
        mDatabase.getReference(PotostopSession.NODE_PERSON).child(user.getUid()).setValue(emptyUser, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    System.out.println("USER SUCCESSFULLY ADDED");
                }
            }
        });
    }


    private boolean userExists() {
        //Try to get user in the db
        FirebaseManager.getUser(mAuth.getUid(), new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                checkUser = (PersonEntity) o;
            }
        });
        //If still null : false
        if(checkUser == null)
            return false;

        return true;
    }

    private void getUserAndChangeUI(String uId){
        //Used to load the PersonEntity because of the Async behavior of Firebase
        FirebaseManager.getUser(uId, new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                userEntity = (PersonEntity) o;
                //Call updateUI only after user has been fetched
                updateUI(userEntity);
            }
        });
    }

    private void updateUI(PersonEntity account) {
        //Check if account is null
        if(account == null){
            //If null : stay on the LoginActivity
            return;
        }
        else {
            //Check if profile has been setup
            if(!checkProfile(account)){
                //If no : redirect to "Profile" activity
                redirectToProfile();
            }
            else {
                //If yes : redirect to "Start trip" activity
                startMainActivity();
            }
        }
    }

    private boolean isPasswordValid(String password) {
        //Password must be at least 5 chars
        return password.length() > 5;
    }

    private boolean isEmailValid(String email) {
        //Check the pattern of the email input
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean checkProfile(PersonEntity user) {
        //Check all user's fields : if one is empty return false
        if(user.getFullname().equals(""))
            return false;
        if(user.getSex() == 999)
            return false;
        if(user.getBirthDate() == 0L)
            return false;
        if(user.getEmergencyEmail().equals(""))
            return false;
        if(user.getEmergencyPhone().equals(""))
            return false;

        return true;
    }

    private void startMainActivity(){
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        finish();
    }

    private void redirectToProfile(){
        Intent profile = new Intent(this, MainActivity.class);
        profile.putExtra("profileSet", false);
        startActivity(profile);
        finish();
    }
}