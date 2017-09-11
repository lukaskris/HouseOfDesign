package com.example.lukaskris.houseofdesign.Transaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lukaskris.houseofdesign.Model.Orders;
import com.example.lukaskris.houseofdesign.R;

public class PaymentActivity extends AppCompatActivity {
    ImageView mLogoBank;
    TextView mInvoice;
    TextView mBatas;
    TextView mTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setTitle("Detail Pembayran");
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Orders orders = (Orders) getIntent().getSerializableExtra("orders");

        mLogoBank = (ImageView) findViewById(R.id.payment_logo_bank);
        mInvoice = (TextView) findViewById(R.id.payment_invoice);
        mBatas = (TextView) findViewById(R.id.payment_batas_pembayaran);
        mTotal = (TextView) findViewById(R.id.payment_total);



        Glide.with(this).load(R.drawable.bank_mandiri).override(200,50).into(mLogoBank);
        mInvoice.setText(orders.getInvoice());
        mTotal.setText(String.valueOf(orders.getTotal()));
        mBatas.setText(orders.getExpired_at().toString());
    }
}
