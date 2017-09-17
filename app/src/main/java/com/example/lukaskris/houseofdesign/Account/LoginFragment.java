package com.example.lukaskris.houseofdesign.Account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lukaskris.houseofdesign.HomeActivity;
import com.example.lukaskris.houseofdesign.Model.Customer;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Util.NetworkUtil;
import com.example.lukaskris.houseofdesign.Util.PreferencesUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;
import static com.example.lukaskris.houseofdesign.Shop.HomeFragment.calledActivity;

public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmail;
    private EditText mPassword;

    private ProgressDialog mProgress;

    public LoginFragment() {}


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

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Login");
        Button googleSignin = (Button) view.findViewById(R.id.sign_in_button);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null)
            mAuth.signOut();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {}
        };

        configureSignin();

        googleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkUtil.isOnline(getContext())){
                    Snackbar.make(v,"Tidak ada koneksi internet",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                signIn();
            }
        });


        mEmail = (EditText) view.findViewById(R.id.login_email);
        mPassword = (EditText) view.findViewById(R.id.login_password);

        mProgress = new ProgressDialog(view.getContext());
        mProgress.setMessage("Please wait...");

        Button mSubmit = (Button) view.findViewById(R.id.email_sign_in_button);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if(!NetworkUtil.isOnline(getContext())) {
                    Snackbar.make(v, "Tidak ada koneksi internet",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(isEmailValid(email) && password.length()>=8) {
                    mProgress.show();

                    if (!email.isEmpty() && !password.isEmpty()) {
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @SuppressWarnings("ConstantConditions")
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    mProgress.dismiss();
                                    InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    im.hideSoftInputFromWindow(getView().getWindowToken(),0);
                                    Snackbar.make(getView(), getString(R.string.error_invalid_auth), Snackbar.LENGTH_SHORT).show();
                                } else {

                                    service.getCustomer(email)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<Customer>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onNext(Customer customers) {
                                                    PreferencesUtil.saveUser(getContext(),customers);
                                                }

                                                @Override
                                                public void onError(Throwable e) {

                                                }

                                                @Override
                                                public void onComplete() {
                                                    if (mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().isEmailVerified() ) {
                                                        Toast.makeText(getContext(),getString(R.string.error_invalid_verification), Toast.LENGTH_LONG).show();
                                                    }
                                                    mProgress.dismiss();
                                                    getActivity().finish();
                                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                                }
                                            });
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

        TextView mRegister = (TextView) view.findViewById(R.id.login_register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
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
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void configureSignin(){
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }
//
//    private void sendEmailVerification() {
//
//        // Send verification email
//        // [START send_email_verification]
//        final FirebaseUser user = mAuth.getCurrentUser();
//        user.sendEmailVerification()
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // [START_EXCLUDE]
//                        // Re-enable button
//
//                        if (task.isSuccessful()) {
//                            Toast.makeText(getContext(),
//                                    "Please Verification your account, verification email sent to " + user.getEmail(),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.e("Debug", "sendEmailVerification", task.getException());
//                            Toast.makeText(getContext(),
//                                    "Failed to send verification email.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        // [END_EXCLUDE]
//                    }
//                });
//        // [END send_email_verification]
//    }

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
                if(getView() != null)
                    Snackbar.make(getView(),"Login Gagal",Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        if(getView() != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

            mProgress.show();
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Snackbar.make(getView(), "Autentikasi Gagal.", Snackbar.LENGTH_SHORT).show();
                            } else {
                                createUserInDatabase(acct);
                            }
                        }
                    });
        }
    }

    private void createUserInDatabase(GoogleSignInAccount acct){
        String defaultPicture = "https://firebasestorage.googleapis.com/v0/b/onlineshop-cee9c.appspot.com/o/user_picture%2Fuserdefault.png?alt=media&token=d5116128-8e81-4809-b12e-ec3492f68257";
        Customer newCustomer = new Customer(acct.getDisplayName(), acct.getEmail(), "", "", defaultPicture);

        service.createCustomer(newCustomer)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {
                    @Override
                    public void accept(Customer customer) throws Exception {
                        PreferencesUtil.saveUser(getContext(), customer);
                        mProgress.dismiss();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), HomeActivity.class));

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(getView()!=null)
                            Snackbar.make(getView(),throwable.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

//    private boolean isHasBeenInitialized(){
//        boolean hasBeenInitialized=false;
//        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(getContext());
//        for(FirebaseApp app : firebaseApps){
//            if(app.getName().equals("House of design")){
//                hasBeenInitialized=true;
//            }
//        }
//        return hasBeenInitialized;
//    }

}
