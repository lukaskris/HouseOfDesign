package com.example.lukaskris.houseofdesign.Account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lukaskris.houseofdesign.HomeActivity;
import com.example.lukaskris.houseofdesign.Model.User;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Static.Static;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
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

import static android.content.Context.MODE_PRIVATE;
import static com.example.lukaskris.houseofdesign.Shop.HomeFragment.calledActivity;

public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 1;
    private TextView mRegister;
    private Button googleSignin;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mRef;

    private EditText mEmail;
    private EditText mPassword;
    private Button mSubmit;

    private ProgressDialog mProgress;

    private String TAG = "Debug";
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Setting Firebase */
        if(!calledActivity){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledActivity=true;
        }
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        googleSignin = (Button) view.findViewById(R.id.sign_in_button);

        mAuth = FirebaseAuth.getInstance();
                mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null && getActivity() != null){
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                    .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Snackbar.make(view,"You got an Error", Snackbar.LENGTH_SHORT).show();

                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                    .build();
        googleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mRef = FirebaseDatabase.getInstance().getReference().child("user");

        mEmail = (EditText) view.findViewById(R.id.login_email);
        mPassword = (EditText) view.findViewById(R.id.login_password);

        mProgress = new ProgressDialog(view.getContext());
        mProgress.setMessage("Please wait...");
        mSubmit = (Button) view.findViewById(R.id.email_sign_in_button);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if(isEmailValid(email) && password.length()>=8) {
                    mProgress.show();

                    if (!email.isEmpty() && !password.isEmpty()) {
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    mProgress.dismiss();
                                    InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    im.hideSoftInputFromWindow(getView().getWindowToken(),0);
                                    Snackbar.make(getView(), getString(R.string.error_invalid_auth), Snackbar.LENGTH_SHORT).show();
                                } else {
                                    if (!mAuth.getCurrentUser().isEmailVerified()) {
//                                        sendEmailVerification();
                                        Toast.makeText(getContext(),getString(R.string.error_invalid_verification), Toast.LENGTH_LONG).show();
                                    }
                                    mProgress.dismiss();
                                    getActivity().finish();
                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                }
                            }
                        });
                    }
                }else{
                    if(!isEmailValid(email)){
                        mEmail.setError(getString(R.string.error_invalid_email));
                        mEmail.requestFocus();
                    }else if(mPassword.length()<8){
                        mPassword.setError(getString(R.string.error_invalid_password));
                    }
                }
            }
        });

        mRegister = (TextView) view.findViewById(R.id.login_register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.login_fragment_layout,new RegisterFragment());
                ft.commit();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public boolean isEmailValid(String email) {
        if (email == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendEmailVerification() {

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button

                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Please Verification your account, verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Debug", "sendEmailVerification", task.getException());
                            Toast.makeText(getContext(),
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else{
                Snackbar.make(getView(),result.getStatus().toString(),Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        final View v = getView();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Snackbar.make(v,"Authentication failed.",Snackbar.LENGTH_SHORT).show();
//                            Toast.makeText(, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

}
