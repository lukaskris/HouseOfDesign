package com.leaksoft.app.houseofdesign.orders;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.leaksoft.app.houseofdesign.model.Cart;
import com.leaksoft.app.houseofdesign.model.Customer;
import com.leaksoft.app.houseofdesign.model.Items;
import com.leaksoft.app.houseofdesign.model.Orders;
import com.leaksoft.app.houseofdesign.model.OrdersBukti;
import com.leaksoft.app.houseofdesign.model.OrdersDetail;
import com.leaksoft.app.houseofdesign.model.OrdersInfo;
import com.leaksoft.app.houseofdesign.model.ShippingAddress;
import com.leaksoft.app.houseofdesign.R;
import com.leaksoft.app.houseofdesign.services.FirebaseStorageService;
import com.leaksoft.app.houseofdesign.util.CurrencyUtil;
import com.leaksoft.app.houseofdesign.util.NetworkUtil;
import com.leaksoft.app.houseofdesign.util.PreferencesUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

import static com.leaksoft.app.houseofdesign.services.ServiceFactory.service;

public class DetailTagihanActivity extends AppCompatActivity {
    private final static int ALL_PERMISSIONS_RESULT = 107;
    static int INTENT_IMAGE = 200;

    TextView mInvoice;
    TextView mStatus;
    TextView mAlamat;
    TextView mJumlah;
    RecyclerView mRecycler;
    ProgressBar mLoading;
    ScrollView mLayout;
    Orders orders;
    LinearLayout mNoInternet;

    ProgressDialog mDialog;

    ItemAdapter adapter;

    Uri picUri;
    Button mUpload;

    List<Cart> mList;
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tagihan);

        setTitle("Detail Tagihan");

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDialog = new ProgressDialog(this);
        mList = new ArrayList<>();
        mInvoice = (TextView) findViewById(R.id.detail_tagihan_invoice);
        mStatus = (TextView) findViewById(R.id.detail_tagihan_status);
        mAlamat = (TextView) findViewById(R.id.detail_tagihan_alamat);
        mJumlah = (TextView) findViewById(R.id.detail_tagihan_total);
        mRecycler = (RecyclerView) findViewById(R.id.detail_tagihan_item);
        mLayout = (ScrollView) findViewById(R.id.detail_tagihan_scrollbar);
        mLoading = (ProgressBar) findViewById(R.id.detail_tagihan_loading);
        mNoInternet = (LinearLayout) findViewById(R.id.detail_tagihan_no_internet);
        mUpload = (Button) findViewById(R.id.detail_tagihan_upload);

        if(getIntent().hasExtra("items")) {
            mList = (List<Cart>) getIntent().getSerializableExtra("items");
        }

        adapter = new ItemAdapter(this,mList);

        if(getIntent().hasExtra("invoice")){
            orders = new Orders();
            Log.d("INVOICE", getIntent().getStringExtra("invoice"));
            loadOrders(getIntent().getStringExtra("invoice"));
        }
        else if(getIntent().hasExtra("orders")){
            orders = (Orders) getIntent().getSerializableExtra("orders");
            mInvoice.setText(orders.getInvoice());
            mStatus.setText(orders.getStatus());
            mJumlah.setText(CurrencyUtil.rupiah(new BigDecimal(orders.getTotal())));

            getInfo();
        }

        mDialog.setTitle("Uploading");

        mNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                if(!hasPermissions(DetailTagihanActivity.this,permissions)){
                    ActivityCompat.requestPermissions(DetailTagihanActivity.this,permissions, ALL_PERMISSIONS_RESULT);
                }else {
                    startActivityForResult(getPickImageChooserIntent(), 200);
                }

            }
        });

        mRecycler.setHasFixedSize(true);
        mRecycler.setNestedScrollingEnabled(false);
        mRecycler.setFocusable(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(adapter);


    }

    private void loadOrders(final String invoice) {
        String email = "";
        Customer user = PreferencesUtil.getUser(DetailTagihanActivity.this);
        if(user!=null && user.getEmail() != null){
            email = user.getEmail();
        }
        if(!email.equals("") && invoice != null) {
            service.getOrders(email, invoice, "*", 0, 10)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new DefaultObserver<List<Orders>>() {
                @Override
                public void onNext(List<Orders> orderses) {
                    for (final Orders order : orderses) {
                        Log.d("Debug", order.getInvoice() + " | " + order.getStatus() +  " "+ order.getTotal());
                        if (order.getInvoice().equals(invoice)) {

                            mInvoice.setText(order.getInvoice());
                            mStatus.setText(order.getStatus());
                            mJumlah.setText(CurrencyUtil.rupiah(new BigDecimal(order.getTotal())));

                            String[] name = order.getName().split(", ");
                            String[] thumbnail = order.getThumbnail().split(", ");
                            String[] price = order.getPrice().split(", ");
                            String[] quantity = order.getQuantity().split(", ");
                            List<OrdersDetail> temp = new ArrayList<>();
                            for (int i = 0; i < name.length; i++) {
                                OrdersDetail d = new OrdersDetail(order.getInvoice(), name[i], thumbnail[i], Integer.parseInt(price[i]),Integer.parseInt(quantity[i]));
                                temp.add(d);
                            }
                            order.setDetail(temp);

                            for(OrdersDetail d: temp){
                                Items item = new Items(d.getName(),d.getPrice(),d.getThumbnail());
                                Cart c = new Cart(item,d.getQuantity());
                                mList.add(c);
                            }

                            adapter.notifyDataSetChanged();
                            orders = order;
                            getInfo();
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.d("Debug", e.getLocalizedMessage());
                }

                @Override
                public void onComplete() {

                }
            });
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        if (requestCode == INTENT_IMAGE && resultCode == RESULT_OK) {
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);
                mDialog.setMessage("Menyiapkan file untuk diupload...");
                mDialog.show();

                Glide.with(DetailTagihanActivity.this).load(picUri).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        setUpload(resource);
                    }
                });


            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                mDialog.setMessage("Menyiapkan file untuk diupload...");
                mDialog.show();

                Glide.with(DetailTagihanActivity.this).load(bitmap).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        setUpload(resource);
                    }
                });

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case ALL_PERMISSIONS_RESULT:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startActivityForResult(getPickImageChooserIntent(), 200);
                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }

    private void setUpload(Bitmap bmp){
        String name = orders.getInvoice();

        mDialog.setMessage("Upload is 0% done");
        if(!mDialog.isShowing())
            mDialog.show();
        mDialog.setCancelable(false);
        FirebaseStorageService.getInstance(DetailTagihanActivity.this)
                .uploadPicture(bmp,name)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mLayout,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests")
                        int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        mDialog.setMessage("Upload is " + progress + "% done");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        OrdersBukti info = new OrdersBukti(orders.getInvoice(), "invoice/"+orders.getInvoice()+".jpg");
                        service.setOrdersBukti(info)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<OrdersBukti>() {
                                    @Override
                                    public void accept(OrdersBukti ordersBukti) throws Exception {
                                        mDialog.dismiss();
                                        Snackbar.make(mLayout, "Bukti transfer telah diterima, harap menunggu proses verifikasi.", Snackbar.LENGTH_SHORT).show();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        mDialog.dismiss();
                                        Snackbar.make(mLayout,throwable.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
    }

    private Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 2);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
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
