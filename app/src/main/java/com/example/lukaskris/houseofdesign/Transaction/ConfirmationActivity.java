package com.example.lukaskris.houseofdesign.Transaction;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.lukaskris.houseofdesign.Model.Courier;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConfirmationActivity extends AppCompatActivity {

    Spinner mCourier;
    Spinner mService;
    TextView mPrice;
    List<Courier> mPackage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        String destination = "444";
        String weight = "1000";
        getSupportActionBar().setTitle("Confirmation");
        mCourier = (Spinner) findViewById(R.id.confirmation_courier_name);
        mService = (Spinner) findViewById(R.id.confirmation_courier_package);
        mPrice = (TextView) findViewById(R.id.confirmation_courier_price);
        mPackage = new ArrayList<>();
        getService();
        mService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Courier courier = mPackage.get(position);
                mPrice.setText(CurrencyUtil.rupiah(new BigDecimal(courier.getmCost())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void getService(){
        String url = "http://api.rajaongkir.com/starter/cost";
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("origin", "444");
            object.put("destination", "444");
            object.put("weight","1000");
            object.put("courier","jne");
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    System.out.println(response.toString());
                    try{
                        List<String> data = new ArrayList<>();
                        JSONArray result = response.getJSONObject("rajaongkir").getJSONArray("results");
                        Log.d("result 1", result.getString(0));
                        Log.d("result 2", result.getJSONObject(0).getString("costs"));
                        JSONArray obj1 = result.getJSONObject(0).getJSONArray("costs");
                        JSONArray service = result.getJSONObject(0).getJSONArray("costs");

                        mPackage.clear();
                        for(int i=0;i < service.length(); i++){
                            JSONObject obj = service.getJSONObject(i);
                            String code = obj.getString("service");
                            String description = obj.getString("description");
                            String price = obj.getJSONArray("cost").getJSONObject(0).getString("value");
                            String estimation = obj.getJSONArray("cost").getJSONObject(0).getString("etd");
                            Courier courier = new Courier(code,description,price,estimation);
                            mPackage.add(courier);
                            data.add(description + "("+code+")");
                        }
                        ArrayAdapter adapter = new ArrayAdapter(ConfirmationActivity.this, android.R.layout.simple_spinner_dropdown_item, data);
                        mService.setAdapter(adapter);
                        progressDialog.dismiss();

                    }catch (JSONException e){
                        progressDialog.dismiss();
                        Log.d("errorjson", e.toString());
                        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.confirmation);
                        Snackbar.make(relativeLayout,e.toString(),Snackbar.LENGTH_SHORT).show();
                    }

                }
            },new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError e) {
                    // do something...
                    Log.d("ErrorResponse", e.toString());
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.confirmation);
                    Snackbar.make(relativeLayout,e.toString(),Snackbar.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
//                    params.put("content-type", "application/x-www-form-urlencoded");
                    params.put("key", "e1b42829f5d45bf380bf7f22aa57cb06");

                    return params;

                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(getRequest);
        }catch (Exception e){
            Log.d("error", e.toString());

            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.confirmation);
            Snackbar.make(relativeLayout,e.toString(),Snackbar.LENGTH_SHORT).show();
        }
    }

}
