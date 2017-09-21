package com.leaksoft.app.houseofdesign.account;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.leaksoft.app.houseofdesign.model.City;
import com.leaksoft.app.houseofdesign.model.Province;
import com.leaksoft.app.houseofdesign.model.ShippingAddress;
import com.leaksoft.app.houseofdesign.model.Subdistrict;
import com.leaksoft.app.houseofdesign.R;
import com.leaksoft.app.houseofdesign.services.RajaOngkir;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.leaksoft.app.houseofdesign.services.ServiceFactory.service;

public class AddAddressActivity extends AppCompatActivity {
    Spinner mProvince;
    Spinner mCity;
    Spinner mSubdistrict;
    LinearLayout mLayoutProvince;
    LinearLayout mLayoutCity;
    LinearLayout mLayoutSubdistrict;
    RequestQueue queue;
    List<Province> proviences;
    List<City> cities;
    List<Subdistrict> subdistricts;
    ProgressBar mProgressProvince;
    ProgressBar mProgressCity;
    ProgressBar mProgressSubdistrict;
    Button mSave;
    EditText mName;
    EditText mAlamat;
    EditText mKodePos;
    EditText mNoTelp;
    TextInputLayout mLayoutName;
    TextInputLayout mLayoutAlamat;
    TextInputLayout mLayoutKodePos;
    TextInputLayout mLayoutNoTelp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Add Address");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        proviences = new ArrayList<>();
        cities = new ArrayList<>();
        subdistricts = new ArrayList<>();
        mLayoutProvince = (LinearLayout) findViewById(R.id.add_address_province_layout);
        mLayoutCity = (LinearLayout) findViewById(R.id.add_address_kabupaten_layout);
        mLayoutSubdistrict = (LinearLayout) findViewById(R.id.add_address_subdistrict_layout);
        mProvince = (Spinner) findViewById(R.id.add_address_provinsi);
        mCity = (Spinner) findViewById(R.id.add_address_kabupaten);
        mSubdistrict = (Spinner) findViewById(R.id.add_address_subdistrict);
        mProgressProvince = (ProgressBar) findViewById(R.id.add_address_province_progress);
        mProgressCity = (ProgressBar) findViewById(R.id.add_address_city_progress);
        mProgressSubdistrict = (ProgressBar) findViewById(R.id.add_address_subdistrict_progress);
        mSave = (Button) findViewById(R.id.add_address_save);
        mName = (EditText) findViewById(R.id.add_address_name);
        mAlamat = (EditText) findViewById(R.id.add_address_alamat);
        mKodePos = (EditText) findViewById(R.id.add_address_kodepos);
        mNoTelp = (EditText) findViewById(R.id.add_address_no_telp);
        mLayoutName = (TextInputLayout) findViewById(R.id.add_address_name_layout);
        mLayoutAlamat = (TextInputLayout) findViewById(R.id.add_address_alamat_layout);
        mLayoutKodePos = (TextInputLayout) findViewById(R.id.add_address_kodepos_layout);
        mLayoutNoTelp = (TextInputLayout) findViewById(R.id.add_address_notelp_layout);

        queue = Volley.newRequestQueue(this);


        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(AddAddressActivity.this);
                progressDialog.setMessage("Saving...");
                progressDialog.setCancelable(true);
                if(validasiInput()) {
                    progressDialog.show();
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null) {

                        service.getAddress(user.getEmail())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<List<ShippingAddress>>() {
                                    @Override
                                    public void accept(List<ShippingAddress> shippingAddresses) throws Exception {
                                        String name = mName.getText().toString();
                                        String address = mAlamat.getText().toString();
                                        String province = mProvince.getSelectedItem().toString();
                                        String province_id = proviences.get(mProvince.getSelectedItemPosition()).getProvince_id();
                                        String city = mCity.getSelectedItem().toString();
                                        String city_id = cities.get(mCity.getSelectedItemPosition()).getId();
                                        String subdistrict = mSubdistrict.getSelectedItem().toString();
                                        String subdistrict_id = subdistricts.get(mSubdistrict.getSelectedItemPosition()).getId();
                                        String email = user.getEmail();
                                        String postal = mKodePos.getText().toString();
                                        String phone = mNoTelp.getText().toString();
                                        String status = "0";
                                        if(shippingAddresses.size()<=0){
                                            status="1";
                                        }
                                        ShippingAddress shippingAddress = new ShippingAddress(name, address, province, province_id, city, city_id,
                                                                                subdistrict, subdistrict_id, postal, phone, status, email);
                                        service.createAddress(shippingAddress)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<ShippingAddress>() {
                                                    @Override
                                                    public void accept(ShippingAddress shippingAddress) throws Exception {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(AddAddressActivity.this, "AddressSaved", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                                    }
                                                }, new Consumer<Throwable>() {
                                                    @Override
                                                    public void accept(Throwable throwable) throws Exception {
                                                        progressDialog.dismiss();
                                                        Log.d("Error di throwable", throwable.getLocalizedMessage());
                                                        Snackbar.make(mName, throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                });




                    }
                }
            }
        });
        getProvince();
        getProvince();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
        }
        return true;
    }

    private boolean validasiInput(){
        boolean valid = true;
        if(mName.getText().toString().isEmpty()){
            mLayoutName.setError(getString(R.string.error_field_required));
            valid=false;
        }
        if(mAlamat.getText().toString().isEmpty()){
            mLayoutAlamat.setError(getString(R.string.error_field_required));
            valid=false;
        }else if(mAlamat.getText().toString().length()<8){
            mLayoutAlamat.setError(getString(R.string.error_minimum_required_address));
            valid=false;
        }
        if(mKodePos.getText().toString().isEmpty()){
            mLayoutKodePos.setError(getString(R.string.error_field_required));
            valid=false;
        }
        if (mNoTelp.getText().toString().isEmpty()) {
            mLayoutNoTelp.setError(getString(R.string.error_field_required));
            valid=false;
        }
        return valid;
    }

    private void getProvince(){
        mLayoutProvince.setVisibility(View.GONE);
        mLayoutCity.setVisibility(View.GONE);
        RajaOngkir.getProvince()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONObject("rajaongkir").getJSONArray("results");
                            List<String> data = new ArrayList<>();
                            for(int i=0;i<result.length();i++){
                                JSONObject obj = result.getJSONObject(i);
                                Province province = new Province(obj.getString("province_id"),obj.getString("province"));
                                proviences.add(province);
                                data.add(obj.getString("province"));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddAddressActivity.this, android.R.layout.simple_spinner_dropdown_item, data);

                            mProvince.setAdapter(adapter);
                            mProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if(proviences.size()>0) {
                                        String idProvince = proviences.get(position).getProvince_id();
                                        getCity(idProvince);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });


                        }catch (Exception e){
                            mProgressProvince.setVisibility(View.GONE);
                            Toast.makeText(AddAddressActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressProvince.setVisibility(View.GONE);
                        Toast.makeText(AddAddressActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        mProgressProvince.setVisibility(View.GONE);
                        mLayoutProvince.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void getCity(String province){
        mLayoutCity.setVisibility(View.GONE);
        mLayoutSubdistrict.setVisibility(View.GONE);
        mProgressCity.setVisibility(View.VISIBLE);
        cities.clear();
        RajaOngkir.getCity(province)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONObject("rajaongkir").getJSONArray("results");
                            List<String> data = new ArrayList<>();
                            for(int i=0;i<result.length();i++){
                                JSONObject obj = result.getJSONObject(i);
                                City city = new City(obj.getString("city_id"),obj.getString("city_name"));
                                cities.add(city);
                                data.add(obj.getString("city_name"));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddAddressActivity.this, android.R.layout.simple_spinner_dropdown_item, data);
                            mCity.setAdapter(adapter);
                            mCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if(cities.size()>0) {
                                        String idCity = cities.get(position).getId();
                                        getSubdistrict(idCity);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } catch (JSONException e) {
                            mProgressCity.setVisibility(View.GONE);
                            Toast.makeText(AddAddressActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressCity.setVisibility(View.GONE);
                        Toast.makeText(AddAddressActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        mProgressCity.setVisibility(View.GONE);
                        mLayoutCity.setVisibility(View.VISIBLE);
                    }
                });

    }

    private void getSubdistrict(String city){

        mProgressSubdistrict.setVisibility(View.VISIBLE);
        mLayoutSubdistrict.setVisibility(View.GONE);
        subdistricts.clear();
        Log.d("DEBUGID",city);
        RajaOngkir.getSubdistrict(city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONObject("rajaongkir").getJSONArray("results");
                            List<String> data = new ArrayList<>();
                            for(int i=0;i<result.length();i++){
                                JSONObject obj = result.getJSONObject(i);
                                Subdistrict subdistrict = new Subdistrict(obj.getString("subdistrict_id"),obj.getString("subdistrict_name"));
                                subdistricts.add(subdistrict);
                                data.add(obj.getString("subdistrict_name"));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddAddressActivity.this, android.R.layout.simple_spinner_dropdown_item, data);
                            mSubdistrict.setAdapter(adapter);
                        } catch (JSONException e) {
                            mProgressSubdistrict.setVisibility(View.GONE);
                            Toast.makeText(AddAddressActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressSubdistrict.setVisibility(View.GONE);
                        Toast.makeText(AddAddressActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        mProgressSubdistrict.setVisibility(View.GONE);
                        mLayoutSubdistrict.setVisibility(View.VISIBLE);
                    }
                });
    }

}
