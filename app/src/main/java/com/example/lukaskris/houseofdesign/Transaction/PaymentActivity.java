package com.example.lukaskris.houseofdesign.Transaction;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.HomeActivity;
import com.example.lukaskris.houseofdesign.Model.Cart;
import com.example.lukaskris.houseofdesign.Model.Items;
import com.example.lukaskris.houseofdesign.Model.Orders;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    ImageView mLogoBank;
    TextView mInvoice;
    TextView mBatas;
    TextView mTotal;
    RecyclerView mRecycler;

    @SuppressLint("SimpleDateFormat")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setTitle("Detail Pembayaran");
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Orders orders = (Orders) getIntent().getSerializableExtra("orders");

        mLogoBank = (ImageView) findViewById(R.id.payment_logo_bank);
        mInvoice = (TextView) findViewById(R.id.payment_invoice);
        mBatas = (TextView) findViewById(R.id.payment_batas_pembayaran);
        mTotal = (TextView) findViewById(R.id.payment_total);
        mRecycler = (RecyclerView) findViewById(R.id.payment_list_item);



        Glide.with(this).load(R.drawable.bank_mandiri).override(500,200).into(mLogoBank);
        mInvoice.setText(orders.getInvoice());
        mTotal.setText(CurrencyUtil.rupiah(new BigDecimal(orders.getTotal())));

        mBatas.setText(new SimpleDateFormat("dd MMM yyyy HH:mm").format(orders.getExpired_at()));
        List<Cart> list = (List<Cart>) getIntent().getSerializableExtra("items");
        ItemAdapter adapter = new ItemAdapter(PaymentActivity.this,list);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d("Debug", "finish dari payment");
                setResult(RESULT_OK,null);
                finish();
                break;
        }
        return true;
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
