package com.leaksoft.app.houseofdesign.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.leaksoft.app.houseofdesign.model.Customer;
import com.leaksoft.app.houseofdesign.util.PreferencesUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

import static com.leaksoft.app.houseofdesign.services.ServiceFactory.service;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInsService";
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token){
        Customer cus = PreferencesUtil.getUser(getApplicationContext());
        PreferencesUtil.saveToken(getApplicationContext(),token);
        if(cus!=null) {
            cus.setFirebasetoken(token);
            PreferencesUtil.saveUser(getApplicationContext(),cus);
            service.updateProfile(cus).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DefaultObserver<Customer>() {
                        @Override
                        public void onNext(Customer customer) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }
}
