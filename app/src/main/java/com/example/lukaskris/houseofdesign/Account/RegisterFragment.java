package com.example.lukaskris.houseofdesign.Account;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lukaskris.houseofdesign.Model.Customer;
import com.example.lukaskris.houseofdesign.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;

public class RegisterFragment extends Fragment {

    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;


    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_register, container, false);

        getActivity().setTitle("Register");

        getActivity().setTitle("Register");

        mAuth = FirebaseAuth.getInstance();


        mProgress = new ProgressDialog(getActivity());

        mName = (EditText) view.findViewById(R.id.register_name);
        mEmail = (EditText) view.findViewById(R.id.register_email);
        mPassword = (EditText) view.findViewById(R.id.register_password);
        TextView mLogin = (TextView) view.findViewById(R.id.register_login_btn);
        Button mSubmit = (Button) view.findViewById(R.id.register_submit);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                fragmentTransaction.replace(R.id.login_fragment_layout,new LoginFragment());
                fragmentTransaction.commit();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mName.getText().toString().isEmpty()){
                    mName.setError(getString(R.string.error_field_required));
                    mName.requestFocus();
                }else if(mPassword.getText().toString().isEmpty()){
                    mPassword.setError(getString(R.string.error_field_required));
                    mPassword.requestFocus();
                }else if(mEmail.getText().toString().isEmpty()){
                    mEmail.setError(getString(R.string.error_field_required));
                    mEmail.requestFocus();
                }else{
                    if(getView() != null) {
                        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    }
                    mProgress.setMessage("Signin up...");
                    mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    if(isEmailValid(mEmail.getText().toString()) && mPassword.length()>=8) {
                        mProgress.show();
                        final String defaultPicture = "https://firebasestorage.googleapis.com/v0/b/onlineshop-cee9c.appspot.com/o/user_picture%2Fuserdefault.png?alt=media&token=d5116128-8e81-4809-b12e-ec3492f68257";
                        Customer newCustomer = new Customer(mName.getText().toString(),mEmail.getText().toString(),"",mPassword.getText().toString(),defaultPicture);
                        service.createCustomer(newCustomer)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Customer>() {
                                    @Override
                                    public void accept(Customer customer) throws Exception {

                                        mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {

                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(mName.getText().toString())
                                                        .setPhotoUri(Uri.parse(defaultPicture))
                                                        .build();
                                                if(mAuth.getCurrentUser() != null) {
                                                    mAuth.getCurrentUser().updateProfile(profileUpdates);
                                                    mAuth.getCurrentUser().sendEmailVerification();
                                                    mAuth.signOut();
                                                }
                                                Toast.makeText(getContext(), "Registration success. Please verify your account.", Toast.LENGTH_LONG).show();
                                                mProgress.dismiss();
                                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                ft.replace(R.id.login_fragment_layout, new LoginFragment());
                                                ft.commit();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                if(getView() != null) {
                                                    Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                                    mProgress.dismiss();
                                                }
                                            }
                                        });


                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Log.d("Customer errorr", throwable.getLocalizedMessage());
                                        Snackbar.make(view,throwable.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
                                        mProgress.hide();
                                    }
                                });


                    }else{
                        if(!isEmailValid(mEmail.getText().toString())) {
                            mEmail.setError(getString(R.string.error_invalid_email));
                            mEmail.requestFocus();
                        }
                        else if(mPassword.length()<8) {
                            mPassword.setError(getString(R.string.error_invalid_password));
                            mPassword.requestFocus();
                        }
                    }
                }
            }
        });
        return view;
    }

    private boolean isHasBeenInitialized(){
        boolean hasBeenInitialized=false;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(getContext());
        for(FirebaseApp app : firebaseApps){
            if(app.getName().equals("Lukenza")){
                hasBeenInitialized=true;
            }
        }
        return hasBeenInitialized;
    }

    private boolean isEmailValid(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
