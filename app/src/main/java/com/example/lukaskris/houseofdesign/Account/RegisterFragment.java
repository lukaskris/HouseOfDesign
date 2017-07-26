package com.example.lukaskris.houseofdesign.Account;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lukaskris.houseofdesign.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;
    private TextView mLogin;
    private Button mSubmit;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("user");

        mProgress = new ProgressDialog(view.getContext());

        mName = (EditText) view.findViewById(R.id.register_name);
        mEmail = (EditText) view.findViewById(R.id.register_email);
        mPassword = (EditText) view.findViewById(R.id.register_password);
        mLogin = (TextView) view.findViewById(R.id.register_login_btn);
        mSubmit = (Button) view.findViewById(R.id.register_submit);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.login_fragment_layout,new LoginFragment());
                fragmentTransaction.commit();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mName.getText().toString().isEmpty()){
                    mName.setError(getString(R.string.error_field_required));
                    mEmail.requestFocus();
                }else if(mPassword.getText().toString().isEmpty()){
                    mPassword.setError(getString(R.string.error_field_required));
                    mEmail.requestFocus();
                }else if(mEmail.getText().toString().isEmpty()){
                    mEmail.setError(getString(R.string.error_field_required));
                    mEmail.requestFocus();
                }else{
                    mProgress.setMessage("Signin up...");
                    if(isEmailValid(mEmail.getText().toString()) && mPassword.length()>=8) {
                        mProgress.show();
                        mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String user_id = authResult.getUser().getUid();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(mName.getText().toString())
                                        .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/onlineshop-cee9c.appspot.com/o/user_picture%2Fuserdefault.png?alt=media&token=d5116128-8e81-4809-b12e-ec3492f68257"))
                                        .build();
                                mAuth.getCurrentUser().updateProfile(profileUpdates);
                                DatabaseReference mChild = mDatabase.child(user_id);
                                mChild.child("name").setValue(mName.getText().toString());
                                mChild.child("image").setValue("default");
                                mChild.child("email").setValue(mEmail.getText().toString());
                                mProgress.hide();
//                                Snackbar.make(getView(), "Registration success.", Snackbar.LENGTH_SHORT).show();
                                InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                im.hideSoftInputFromWindow(getView().getWindowToken(),0);
                                Toast.makeText(getContext(),"Registration success. Please verify your account.",Toast.LENGTH_LONG).show();
                                mAuth.getCurrentUser().sendEmailVerification();
                                mAuth.signOut();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.login_fragment_layout, new LoginFragment());
                                ft.commit();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                Log.d("ONFAILURE", e.getMessage());
                                mProgress.dismiss();
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

    public boolean isEmailValid(String email) {
        if (email == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
