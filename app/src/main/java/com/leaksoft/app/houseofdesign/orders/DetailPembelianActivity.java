package com.leaksoft.app.houseofdesign.orders;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.leaksoft.app.houseofdesign.R;
import com.leaksoft.app.houseofdesign.model.Cart;
import com.leaksoft.app.houseofdesign.model.Orders;
import com.leaksoft.app.houseofdesign.model.OrdersInfo;
import com.leaksoft.app.houseofdesign.model.ShippingAddress;
import com.leaksoft.app.houseofdesign.services.RajaOngkir;
import com.leaksoft.app.houseofdesign.util.CurrencyUtil;
import com.leaksoft.app.houseofdesign.util.DateUtil;
import com.leaksoft.app.houseofdesign.util.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.leaksoft.app.houseofdesign.services.ServiceFactory.service;

public class DetailPembelianActivity extends AppCompatActivity {
    TextView mInvoice;
    TextView mStatus;
    TextView mAlamat;
    TextView mJumlah;
    TextView mKurir;
    RecyclerView mRecycler;
    ProgressBar mLoading;
    ScrollView mLayout;
    Orders orders;
    LinearLayout mNoInternet;
    ExpandableTextView mTrackingResult;
    TextView mHistory;
    Button mConfirm;
    List<Cart> mList;
    List<Manifest> mTrackingList;
    RecyclerView mRecyclerView;
    TrackingAdapter trackingAdapter;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pembelian);

        setTitle("Detail Pembelian");

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orders = (Orders) getIntent().getSerializableExtra("orders");
        mKurir = (TextView) findViewById(R.id.detail_pembelian_kurir);
        mInvoice = (TextView) findViewById(R.id.detail_pembelian_invoice);
        mStatus = (TextView) findViewById(R.id.detail_pembelian_status);
        mAlamat = (TextView) findViewById(R.id.detail_pembelian_alamat);
        mJumlah = (TextView) findViewById(R.id.detail_pembelian_total);
        mRecycler = (RecyclerView) findViewById(R.id.detail_pembelian_item);
        mLayout = (ScrollView) findViewById(R.id.detail_pembelian_scrollbar);
        mLoading = (ProgressBar) findViewById(R.id.detail_pembelian_loading);
        mHistory = (TextView) findViewById(R.id.detail_pembelian_history);
        mNoInternet = (LinearLayout) findViewById(R.id.detail_pembelian_no_internet);
        mTrackingResult = (ExpandableTextView) findViewById(R.id.detail_pembelian_expandable);
        mRecyclerView = (RecyclerView) findViewById(R.id.detail_pembelian_recycler);
        mConfirm = (Button) findViewById(R.id.detail_pembelian_konfirmasi);

        mNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });

        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTrackingResult.isExpanded())
                    mTrackingResult.collapse();
                else
                    mTrackingResult.expand();

                if(trackingAdapter.maxShow > 2){
                    trackingAdapter.maxShow = 2;
                    trackingAdapter.notifyDataSetChanged();
                }else {
                    trackingAdapter.maxShow = mTrackingList.size();
                    trackingAdapter.notifyDataSetChanged();
                }
            }
        });

        if(orders.getStatusCode() == 3){
            mConfirm.setVisibility(View.VISIBLE);
            mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailPembelianActivity.this);
                    builder.setMessage("Apakah barang yang dibeli telah sampai?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    orders.setStatus(4);
                                    confirmBarang();
                                }
                            })
                            .setNegativeButton("Batal",null)
                            .show();

                }
            });
        }

        mList = new ArrayList<>();
        mList = (List<Cart>) getIntent().getSerializableExtra("items");

        mTrackingList = new ArrayList<>();

        trackingAdapter = new TrackingAdapter(this, mTrackingList);

        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(trackingAdapter);


        ItemAdapter adapter = new ItemAdapter(this,mList);
        mRecycler.setHasFixedSize(false);
        mRecycler.setNestedScrollingEnabled(false);
        mRecycler.setFocusable(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(adapter);

        mInvoice.setText(orders.getInvoice());
        mStatus.setText(orders.getStatus());
        mJumlah.setText(CurrencyUtil.rupiah(new BigDecimal(orders.getTotal())));
        getInfo();
    }

    private void getWaybill(final String resi, String courier){
        RajaOngkir.getWaybill(resi, courier)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JSONObject response) {
                        try{
                            String text="\n";
                            JSONArray result = response.getJSONObject("rajaongkir").getJSONObject("result").getJSONArray("manifest");
                            for(int i=0;i<result.length();i++){
                                JSONObject obj = result.getJSONObject(i);
                                Manifest manifest = new Manifest(obj.getString("manifest_code"),obj.getString("manifest_description"),obj.getString("manifest_date"),obj.getString("manifest_time"),obj.getString("city_name"));
                                text= text + "\n"+ manifest.getManifest_description() + " " + manifest.getManifest_date() + " " + manifest.getManifest_time() + " " +manifest.getCity_name();
                                mTrackingList.add(manifest);
                            }
                            Collections.reverse(mTrackingList);
                            trackingAdapter.notifyDataSetChanged();
                            mTrackingResult.setText(text);

                        }catch (Exception e){
                            Log.d("DetailPembelianActivity", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void confirmBarang(){
        service.updateOrder(orders)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Orders>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Orders o) {
                        mStatus.setText(orders.getStatus());
                        mConfirm.setVisibility(View.GONE);
                        Snackbar.make(mAlamat,"Barang telah dikonfirmasi", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Snackbar.make(mAlamat,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getInfo(){
        if(!NetworkUtil.isOnline(this)){
            Snackbar.make(mInvoice,"Tidak ada koneksi internet",Snackbar.LENGTH_LONG).show();
            mLoading.setVisibility(View.GONE);
            mLayout.setVisibility(View.GONE);
            mNoInternet.setVisibility(View.VISIBLE);
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
                        getWaybill(ordersInfo.getNo_resi(),ordersInfo.getKurir_id());
                        mKurir.setText(ordersInfo.getKurir() + " ("+ ordersInfo.getType() + ")");
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

    private class Manifest{
        String manifest_code;
        String manifest_description;
        String manifest_date;
        String manifest_time;
        String city_name;

        Manifest(String manifest_code, String manifest_description, String manifest_date, String manifest_time, String city_name) {
            this.manifest_code = manifest_code;
            this.manifest_description = manifest_description;
            this.manifest_date = manifest_date;
            this.manifest_time = manifest_time;
            this.city_name = city_name;
        }

        String getManifest_description() {
            return manifest_description;
        }

        String getManifest_date() {
            return manifest_date;
        }

        String getManifest_time() {
            return manifest_time;
        }

        String getCity_name() {
            return city_name;
        }
    }

    class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder>{
        Context mContext;
        List<Manifest> list;
        int maxShow = 2;
        TrackingAdapter(Context context, List<Manifest> trackings){
            this.mContext = context;
            this.list= trackings;
        }

        @Override
        public TrackingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(
                    R.layout.detail_pembelian_row, parent, false);
            return new TrackingViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TrackingViewHolder holder, int position) {
            Manifest item = list.get(position);
            holder.description.setText(item.getManifest_description());
            Date date = DateUtil.stringToDate(item.getManifest_date() +  " " +item.getManifest_time(), "yyyy-MM-dd HH:mm");
            String dateString = DateUtil.dateToString(date, "dd MMM yyyy");
            holder.time.setText(dateString);
            holder.city.setText(item.getCity_name());
            if(position>=maxShow){
                holder.itemView.setVisibility(View.GONE);
            }else {
                holder.itemView.setVisibility(View.VISIBLE);
            }
        }

        public void setMaxShow(int maxShow){
            this.maxShow = maxShow;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class TrackingViewHolder extends RecyclerView.ViewHolder{
            TextView description;
            TextView time;
            TextView city;
            TrackingViewHolder(View itemView) {
                super(itemView);
                description = (TextView) itemView.findViewById(R.id.detail);
                time = (TextView) itemView.findViewById(R.id.tanggal);
                city = (TextView) itemView.findViewById(R.id.lokasi);
            }
        }
    }

    private class Tracking{
        String description;
        String time;
        String city;

        public Tracking(String description, String time, String city) {
            this.description = description;
            this.time = time;
            this.city = city;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
        List<Cart> itemsList;
        Context mContext;

        ItemAdapter(Context context, List<Cart> itemsList) {
            this.itemsList = itemsList;
            this.mContext = context;
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
