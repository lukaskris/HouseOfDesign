package com.example.lukaskris.houseofdesign.Transaction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lukaskris.houseofdesign.Account.AddAddressActivity;
import com.example.lukaskris.houseofdesign.Account.AddressActivity;
import com.example.lukaskris.houseofdesign.Model.Courier;
import com.example.lukaskris.houseofdesign.Model.ShippingAddress;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Services.RajaOngkir;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonSyntaxException;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;

public class ConfirmationActivity extends AppCompatActivity {

    Spinner mCourier;
    Spinner mService;
    TextView mPrice;
    List<Courier> mPackage;
    TextView mChange;
    TextView mTotal;

    TextView mName;
    TextView mAddress;
    TextView mProv;
    TextView mPhone;
    LinearLayout mLayoutAddress;
    AVLoadingIndicatorView mLoading;
    LinearLayout mNoAddress;
    Button mAddAddress;
    Button mConfirm;

    ShippingAddress shippingAddress;

    int total;
    int weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Confirmation");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mCourier = (Spinner) findViewById(R.id.confirmation_courier_name);
        mService = (Spinner) findViewById(R.id.confirmation_courier_package);
        mPrice = (TextView) findViewById(R.id.confirmation_courier_price);
        mChange = (TextView) findViewById(R.id.confirmation_change);
        mTotal = (TextView) findViewById(R.id.confirmation_total);
        mName = (TextView) findViewById(R.id.confirmation_name);
        mAddress = (TextView) findViewById(R.id.confirmation_address);
        mProv = (TextView) findViewById(R.id.confirmation_prov_city_post);
        mPhone = (TextView) findViewById(R.id.confirmation_phone);
        mLoading = (AVLoadingIndicatorView) findViewById(R.id.confirmation_loading);
        mLayoutAddress = (LinearLayout) findViewById(R.id.confirmation_address_layout);
        mNoAddress = (LinearLayout) findViewById(R.id.confirmation_no_address);
        mAddAddress = (Button) findViewById(R.id.confirmation_add_address);
        mConfirm = (Button) findViewById(R.id.confirmation_confirm);

        mAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ConfirmationActivity.this, AddAddressActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        total = getIntent().getIntExtra("total",0);
        weight = getIntent().getIntExtra("weight",0);
        mTotal.setText(CurrencyUtil.rupiah(new BigDecimal(total)));

        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmationActivity.this, AddressActivity.class);
                intent.putExtra("select","select");
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        mCourier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(shippingAddress != null)
                    getService();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mPackage = new ArrayList<>();
        getDefaultAddress();
        mService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Courier courier = mPackage.get(position);
                mPrice.setText(CurrencyUtil.rupiah(new BigDecimal(courier.getmCost())));
                total = total + Integer.parseInt(courier.getmCost());
                mTotal.setText(CurrencyUtil.rupiah(new BigDecimal(total)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shippingAddress!=null){

                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDefaultAddress();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            ShippingAddress address = (ShippingAddress) data.getSerializableExtra("shipping");
            mName.setText(address.getName());
            mAddress.setText(address.getAddress());
            mProv.setText(address.getCity()+", " +address.getProvince() + " "+address.getPostal_code());
            mPhone.setText(address.getPhone());
        }
    }

    private void getDefaultAddress(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            mLoading.setVisibility(View.VISIBLE);
            mLayoutAddress.setVisibility(View.GONE);
            service.getDefaultAddress(user.getEmail())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<ShippingAddress>>() {
                        @Override
                        public void accept(List<ShippingAddress> shippingAddresses) throws Exception {
                            if(shippingAddresses.size() > 0) {
                                ShippingAddress address = shippingAddresses.get(0);
                                shippingAddress = address;
                                mName.setText(address.getName());
                                mAddress.setText(address.getAddress());
                                mProv.setText(address.getCity() + ", " + address.getProvince() + " " + address.getPostal_code());
                                mPhone.setText(address.getPhone());
                                getService();
                                mLoading.setVisibility(View.GONE);
                                mLayoutAddress.setVisibility(View.VISIBLE);
                            }else{
                                mLoading.setVisibility(View.GONE);
                                mNoAddress.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(throwable instanceof JsonSyntaxException){
                                mLoading.setVisibility(View.GONE);
                                mNoAddress.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }

    private void getService(){
        String origin = "444";
        String destination = shippingAddress.getSubdistrict_id();
        mService.setVisibility(View.GONE);
        (findViewById(R.id.confirmation_loading_courier)).setVisibility(View.VISIBLE);
        String courier = "";
        if(mCourier.getSelectedItem().toString().equalsIgnoreCase("jne"))
            courier="jne";
        else if(mCourier.getSelectedItem().toString().equalsIgnoreCase("tiki"))
            courier="tiki";
        else if(mCourier.getSelectedItem().toString().equalsIgnoreCase("Pos Indonesia"))
            courier="pos";

        RajaOngkir.getCost(origin,destination,String.valueOf(weight),courier)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        List<String> data = new ArrayList<>();
                        JSONArray result = response.getJSONObject("rajaongkir").getJSONArray("results");

                        JSONArray service = result.getJSONObject(0).getJSONArray("costs");

                        mPackage.clear();
                        for (int i = 0; i < service.length(); i++) {
                            JSONObject obj = service.getJSONObject(i);
                            String code = obj.getString("service");
                            String description = obj.getString("description");
                            String price = obj.getJSONArray("cost").getJSONObject(0).getString("value");
                            String estimation = obj.getJSONArray("cost").getJSONObject(0).getString("etd");
                            Courier courier = new Courier(code, description, price, estimation);
                            mPackage.add(courier);
                            data.add(description + "(" + code + ")");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ConfirmationActivity.this, android.R.layout.simple_spinner_dropdown_item, data);
                        mService.setAdapter(adapter);
                        mService.setVisibility(View.VISIBLE);
                        (findViewById(R.id.confirmation_loading_courier)).setVisibility(View.GONE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        (findViewById(R.id.confirmation_loading_courier)).setVisibility(View.GONE);
                        Toast.makeText(ConfirmationActivity.this, throwable.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
//        try {
//
//            object.put("origin", "444");
//            object.put("destination", shippingAddress.getCity_id());
//            object.put("destinationType","city");
//            object.put("weight",weight);
//            object.put("courier",courier);
//            Log.d("DEBUG",courier + " weight " + weight);
//            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
////                    System.out.println(response.toString());
//                    try{
//
//
//                    }catch (JSONException e){
//                        progressDialog.dismiss();
//                        Log.d("errorjson", e.toString());
//                        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.confirmation);
//                        Snackbar.make(relativeLayout,e.toString(),Snackbar.LENGTH_SHORT).show();
//                    }
//
//                }
//            },new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError e) {
//                    // do something...
//                    progressDialog.dismiss();
//                    Log.d("ErrorResponse", e.toString());
//                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.confirmation);
//                    Snackbar.make(relativeLayout,e.toString(),Snackbar.LENGTH_SHORT).show();
//                }
//            }){
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String>  params = new HashMap<String, String>();
////                    params.put("content-type", "application/x-www-form-urlencoded");
//                    params.put("key", "e1b42829f5d45bf380bf7f22aa57cb06");
//
//                    return params;
//
//                }
//            };
//
//            RequestQueue queue = Volley.newRequestQueue(this);
//            queue.add(getRequest);
//        }catch (Exception e){
//            Log.d("error", e.toString());
//            progressDialog.dismiss();
//            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.confirmation);
//            Snackbar.make(relativeLayout,e.toString(),Snackbar.LENGTH_SHORT).show();
//        }
    }

}
