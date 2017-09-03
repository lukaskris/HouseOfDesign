package com.example.lukaskris.houseofdesign.Account;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lukaskris.houseofdesign.Model.City;
import com.example.lukaskris.houseofdesign.Model.Customer;
import com.example.lukaskris.houseofdesign.Model.Provience;
import com.example.lukaskris.houseofdesign.Model.ShippingAddress;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Services.ServiceFactory;
import com.example.lukaskris.houseofdesign.Util.PreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;

public class AddAddressActivity extends AppCompatActivity {
    Spinner mProvince;
    Spinner mCity;
    LinearLayout mLayoutCity;
    RequestQueue queue;
    List<Provience> proviences;
    List<City> cities;
    ProgressBar dialog;
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
        getSupportActionBar().setTitle("Add Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        proviences = new ArrayList<>();
        cities = new ArrayList<>();
        mLayoutCity = (LinearLayout) findViewById(R.id.add_address_kabupaten_layout);
        mProvince = (Spinner) findViewById(R.id.add_address_provinsi);
        mCity = (Spinner) findViewById(R.id.add_address_kabupaten);
        dialog = (ProgressBar) findViewById(R.id.add_address_progress);
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
                    if(FirebaseAuth.getInstance().getCurrentUser()!= null) {

                        String name = mName.getText().toString();
                        String address = mAlamat.getText().toString();
                        String province = mProvince.getSelectedItem().toString();
                        String province_id = proviences.get(mProvince.getSelectedItemPosition()).getId();
                        String city = mCity.getSelectedItem().toString();
                        String city_id = cities.get(mCity.getSelectedItemPosition()).getId();
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        String postal = mKodePos.getText().toString();
                        String phone = mNoTelp.getText().toString();
                        ShippingAddress shippingAddress = new ShippingAddress(name,address,province,province_id,city,city_id,postal,phone,"0",email);
                        service.createAddress(shippingAddress)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<ShippingAddress>() {
                                    @Override
                                    public void accept(ShippingAddress shippingAddress) throws Exception {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddAddressActivity.this,"AddressSaved",Toast.LENGTH_SHORT).show();
                                        finish();
                                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        progressDialog.dismiss();
                                        Log.d("Error di throwable",throwable.getLocalizedMessage());
                                        Snackbar.make(mName,throwable.getLocalizedMessage(),Snackbar.LENGTH_LONG).show();
                                    }
                                });

                    }
                }
            }
        });
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
        String url = "http://api.rajaongkir.com/starter/province";
        dialog.setVisibility(View.VISIBLE);
        JSONObject object = new JSONObject();
        JsonObjectRequest getRequest;
        try {
            getRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray result = response.getJSONObject("rajaongkir").getJSONArray("results");
                        List<String> data = new ArrayList<>();
                        for(int i=0;i<result.length();i++){
                            JSONObject obj = result.getJSONObject(i);
                            Provience provience = new Provience(obj.getString("province_id"),obj.getString("province"));
                            proviences.add(provience);
                            data.add(obj.getString("province"));
                        }
                        ArrayAdapter adapter = new ArrayAdapter(AddAddressActivity.this, android.R.layout.simple_spinner_dropdown_item, data);
                        mProvince.setAdapter(adapter);
                        mProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String idProvience = proviences.get(position).getId();
                                getCity(idProvience);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.setVisibility(View.GONE);
                    Snackbar.make(mName,error.getLocalizedMessage(),Snackbar.LENGTH_SHORT);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("key", "e1b42829f5d45bf380bf7f22aa57cb06");

                    return params;

                }
            };

            queue.add(getRequest);
        }catch (Exception e){
            dialog.setVisibility(View.GONE);
            Snackbar.make(mName,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT);
        }

    }

    private void getCity(String province){
        String url = "http://api.rajaongkir.com/starter/city?province="+province;

        JSONObject object = new JSONObject();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                JSONArray result = null;
                try {
                    result = response.getJSONObject("rajaongkir").getJSONArray("results");
                    List<String> data = new ArrayList<>();
                    for(int i=0;i<result.length();i++){
                        JSONObject obj = result.getJSONObject(i);
                        City city = new City(obj.getString("city_id"),obj.getString("city_name"));
                        cities.add(city);
                        data.add(obj.getString("city_name"));
                    }
                    mLayoutCity.setVisibility(View.VISIBLE);
                    dialog.setVisibility(View.GONE);
                    ArrayAdapter adapter = new ArrayAdapter(AddAddressActivity.this, android.R.layout.simple_spinner_dropdown_item, data);
                    mCity.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("key", "e1b42829f5d45bf380bf7f22aa57cb06");

                return params;

            }
        };
        queue.add(getRequest);
    }


}
