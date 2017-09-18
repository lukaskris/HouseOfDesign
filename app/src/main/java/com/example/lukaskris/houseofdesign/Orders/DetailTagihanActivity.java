package com.example.lukaskris.houseofdesign.Orders;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.Model.Cart;
import com.example.lukaskris.houseofdesign.Model.Orders;
import com.example.lukaskris.houseofdesign.Model.OrdersInfo;
import com.example.lukaskris.houseofdesign.Model.ShippingAddress;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;
import com.example.lukaskris.houseofdesign.Util.NetworkUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;

public class DetailTagihanActivity extends AppCompatActivity {
    TextView mInvoice;
    TextView mStatus;
    TextView mAlamat;
    TextView mJumlah;
    RecyclerView mRecycler;
    ProgressBar mLoading;
    ScrollView mLayout;
    Orders orders;
    LinearLayout mNoInternet;
    Button mUpload;

    List<Cart> mList;
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tagihan);

        setTitle("Detail Tagihan");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orders = (Orders) getIntent().getSerializableExtra("orders");

        mInvoice = (TextView) findViewById(R.id.detail_tagihan_invoice);
        mStatus = (TextView) findViewById(R.id.detail_tagihan_status);
        mAlamat = (TextView) findViewById(R.id.detail_tagihan_alamat);
        mJumlah = (TextView) findViewById(R.id.detail_tagihan_total);
        mRecycler = (RecyclerView) findViewById(R.id.detail_tagihan_item);
        mLayout = (ScrollView) findViewById(R.id.detail_tagihan_scrollbar);
        mLoading = (ProgressBar) findViewById(R.id.detail_tagihan_loading);
        mNoInternet = (LinearLayout) findViewById(R.id.detail_tagihan_no_internet);
        mUpload = (Button) findViewById(R.id.detail_tagihan_upload);
        mNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mList = new ArrayList<>();
        mList = (List<Cart>) getIntent().getSerializableExtra("items");

        ItemAdapter adapter = new ItemAdapter(this,mList);
        mRecycler.setHasFixedSize(true);
        mRecycler.setNestedScrollingEnabled(false);
        mRecycler.setFocusable(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(adapter);
        mInvoice.setText(orders.getInvoice());
        mStatus.setText(orders.getStatus());
        mJumlah.setText(CurrencyUtil.rupiah(new BigDecimal(orders.getTotal())));
        getInfo();
    }

    private void getInfo(){
        if(!NetworkUtil.isOnline(this)){
            Snackbar.make(mInvoice,"Tidak ada koneksi internet",Snackbar.LENGTH_LONG).show();
            mLoading.setVisibility(View.GONE);
            mLayout.setVisibility(View.GONE);
            mNoInternet.setVisibility(View.VISIBLE);
            mUpload.setVisibility(View.GONE);
            return;
        }

        mLoading.setVisibility(View.VISIBLE);
        mLayout.setVisibility(View.GONE);
        mNoInternet.setVisibility(View.GONE);
        service.getOrdersInfo(orders.getInvoice())
                .flatMap(new Function<List<OrdersInfo>, ObservableSource<OrdersInfo>>() {
                    @Override
                    public ObservableSource<OrdersInfo> apply(List<OrdersInfo> ordersInfos) throws Exception {
                        return Observable.fromIterable(ordersInfos);
                    }
                })
                .flatMap(new Function<OrdersInfo, ObservableSource<ShippingAddress>>() {
                    @Override
                    public ObservableSource<ShippingAddress> apply(OrdersInfo ordersInfo) throws Exception {
                        return service.getAddress(orders.getEmail(), String.valueOf(ordersInfo.getShipping_id()));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ShippingAddress>() {
                    @Override
                    public void accept(ShippingAddress shippingAddress) throws Exception {
                        mAlamat.setText(shippingAddress.toString());
                        mLoading.setVisibility(View.GONE);
                        mLayout.setVisibility(View.VISIBLE);
                        mNoInternet.setVisibility(View.GONE);
                        if(orders.getStatusCode() != 5)
                            mUpload.setVisibility(View.VISIBLE);
                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }

    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
        List<Cart> itemsList;
        Context mContext;

        ItemAdapter(Context mContext, List<Cart> itemsList) {
            this.itemsList = itemsList;
            this.mContext = mContext;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(
                    R.layout.payment_row_item, parent, false);
            return new ItemViewHolder(v);
        }

        @Override
        public int getItemCount() {
            return itemsList.size();
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            Cart item = itemsList.get(position);
            holder.mName.setText(item.getItem().getName());
            String price = CurrencyUtil.rupiah(new BigDecimal(item.getItem().getPrice()));
            String qty = String.valueOf(item.getQuantity());
            holder.mQty.setText(qty + " x " + price);
            Glide.with(mContext)
                    .load("https://storage.googleapis.com/houseofdesign/"+item.getItem().getThumbnail()).override(100,100)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.mImage);
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView mName;
            TextView mQty;
            ImageView mImage;
            ItemViewHolder(View itemView) {
                super(itemView);
                mName = (TextView) itemView.findViewById(R.id.payment_row_name);
                mImage = (ImageView) itemView.findViewById(R.id.payment_row_image);
                mQty = (TextView) itemView.findViewById(R.id.payment_row_qty_price);
            }
        }
    }
}
