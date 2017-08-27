package com.example.lukaskris.houseofdesign.Account;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lukaskris.houseofdesign.Model.City;
import com.example.lukaskris.houseofdesign.Model.Provience;
import com.example.lukaskris.houseofdesign.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {
    Spinner mProvince;
    Spinner mCity;
    LinearLayout mLayoutCity;
    RequestQueue queue;
    List<Provience> proviences;
    List<City> cities;
    ProgressBar dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        getSupportActionBar().setTitle("Add Address");
        proviences = new ArrayList<>();
        cities = new ArrayList<>();
        mLayoutCity = (LinearLayout) findViewById(R.id.add_address_kabupaten_layout);
        mProvince = (Spinner) findViewById(R.id.add_address_provinsi);
        mCity = (Spinner) findViewById(R.id.add_address_kabupaten);
        dialog = (ProgressBar) findViewById(R.id.add_address_progress);
        queue = Volley.newRequestQueue(this);
        getProvince();
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
