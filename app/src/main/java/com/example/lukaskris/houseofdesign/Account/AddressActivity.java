package com.example.lukaskris.houseofdesign.Account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lukaskris.houseofdesign.Model.ShippingAddress;
import com.example.lukaskris.houseofdesign.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wang.avi.AVLoadingIndicatorView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;

public class AddressActivity extends AppCompatActivity {
    List<ShippingAddress> addresses;
    FloatingActionButton addAddress;
    AddressAdapter adapter;
    RecyclerView mRecycler;
    AVLoadingIndicatorView mLoading;
    LinearLayout mNoData;
    LinearLayout mError;
    int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        setTitle("Address");
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type=1;

        if(getIntent().hasExtra("select")){
            setTitle("Select Address");
            type=0;
        }
        addresses = new ArrayList<>();
        mRecycler = (RecyclerView) findViewById(R.id.address_recycler);
        addAddress = (FloatingActionButton) findViewById(R.id.address_fab);
        mLoading = (AVLoadingIndicatorView) findViewById(R.id.address_loading);
        mNoData = (LinearLayout) findViewById(R.id.address_no_data);
        mError = (LinearLayout) findViewById(R.id.address_error);

        refresh();

        mError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mError.setVisibility(View.GONE);

                refresh();
            }
        });

        adapter = new AddressAdapter(AddressActivity.this,addresses);

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(adapter);

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddressActivity.this,AddAddressActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
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

    private void refresh(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mLoading.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
            service.getAddress(user.getEmail())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ShippingAddress>>() {
                    @Override
                    public void accept(List<ShippingAddress> shippingAddresses) throws Exception {
                        if(shippingAddresses.size()>0) {
                            addresses.clear();
                            addresses.addAll(shippingAddresses);
                            adapter.notifyDataSetChanged();
                            mLoading.setVisibility(View.GONE);
                            mRecycler.setVisibility(View.VISIBLE);
                        }else{
                            mRecycler.setVisibility(View.VISIBLE);
                            mNoData.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("Error", throwable.getLocalizedMessage());
                        mError.setVisibility(View.VISIBLE);
                        mLoading.setVisibility(View.GONE);
                    }
                });
        }
    }

    class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder>{
        Context context;
        List<ShippingAddress> addresses;

        public AddressAdapter(Context context,List<ShippingAddress> list){
            this.context = context;
            this.addresses = list;
        }

        @Override
        public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v =
                    inflater.inflate(R.layout.address_recyclerview_row, parent, false);
            AddressViewHolder vh = new AddressViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(AddressViewHolder holder, int position) {
            final ShippingAddress address = addresses.get(position);
            holder.mName.setText(address.getName());
            holder.mNotelp.setText(address.getPhone());
            holder.mKota.setText(address.getCity());
            holder.mProvinsiKodepos.setText(address.getProvince() + " " + address.getPostal_code());
            holder.mAlamat.setText(address.getAddress());
            if(type==0){
                holder.mEdit.setVisibility(View.GONE);
                holder.mDelete.setVisibility(View.GONE);
                holder.mDefault.setText("Pilih");
                holder.mDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("shipping", (Serializable) address);
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return addresses.size();
        }

        class AddressViewHolder extends RecyclerView.ViewHolder{
            TextView mName;
            TextView mAlamat;
            TextView mProvinsiKodepos;
            TextView mKota;
            TextView mNotelp;
            TextView mDefault;
            TextView mEdit;
            TextView mDelete;
            LinearLayout mOptions;

            public AddressViewHolder(View itemView) {
                super(itemView);
                mName = (TextView) itemView.findViewById(R.id.address_row_name);
                mAlamat = (TextView) itemView.findViewById(R.id.address_row_alamat);
                mProvinsiKodepos = (TextView) itemView.findViewById(R.id.address_row_provinsi_kodepos);
                mKota = (TextView) itemView.findViewById(R.id.address_row_kota);
                mNotelp = (TextView) itemView.findViewById(R.id.address_row_notelp);
                mOptions = (LinearLayout) itemView.findViewById(R.id.address_options);
                mEdit = (TextView) itemView.findViewById(R.id.address_edit);
                mDefault = (TextView) itemView.findViewById(R.id.address_edit);
                mDelete = (TextView) itemView.findViewById(R.id.address_delete);
            }
        }
    }
}
