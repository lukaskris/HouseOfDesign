package com.example.lukaskris.houseofdesign.Shop;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.R;

import java.util.ArrayList;
import java.util.List;

import endpoint.backend.itemApi.model.Type;
import me.himanshusoni.quantityview.QuantityView;

public class TypeActivity extends AppCompatActivity {
    private TextView mName;
    private TextView mPrice;
    private ImageView mImage;
    private TextView mColor;
    private RecyclerView mRecyclerColor;
    private RecyclerView mRecyclerSize;
    private Button mAddToCart;
    private Button mBuyNow;
    private TextView mQtyAvailable;
    private QuantityView mQty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mName = (TextView) findViewById(R.id.type_name);
        mPrice = (TextView) findViewById(R.id.type_price);
        mImage = (ImageView) findViewById(R.id.type_picture);
        mColor = (TextView) findViewById(R.id.type_color);
        mRecyclerColor = (RecyclerView) findViewById(R.id.type_recycler_color);
        mRecyclerSize = (RecyclerView) findViewById(R.id.type_recycler_size);
        mAddToCart = (Button) findViewById(R.id.type_add_to_cart);
        mBuyNow = (Button) findViewById(R.id.type_buy_now);
        mQtyAvailable = (TextView) findViewById(R.id.type_qty_available);
        mQty = (QuantityView) findViewById(R.id.type_qty);

        String name = getIntent().getStringExtra("nama");
        String price = getIntent().getStringExtra("harga");
        String foto = getIntent().getStringExtra("foto");
        List<Type> type = (List<Type>) getIntent().getSerializableExtra("type");

        mName.setText(name);
        mPrice.setText(price);
        Glide.with(this).load(foto).override(120,120).diskCacheStrategy(DiskCacheStrategy.RESULT).into(mImage);

        mQtyAvailable.setVisibility(View.GONE);



        List<Type> mList = new ArrayList<>();
//        Type type = new Type();
//        type.setSize("S");

        Type type2 = new Type();
        type2.setSize("M");
        type2.setColor("Merah");
        Type type3 = new Type();
        type3.setSize("L");
        type3.setColor("Biru");
        Type type4 = new Type();
        type4.setSize("XL");
        type4.setColor("Merah");
        Type type5 = new Type();
        type5.setSize("XXL");
        type5.setColor("Putih");
        mList.addAll(type);
//        mList.add(type2);
//        mList.add(type3);
//        mList.add(type4);
//        mList.add(type5);

        sizeAdapter adapter = new sizeAdapter(this, mList);
        mRecyclerSize.setLayoutManager(new GridLayoutManager(this,4));
        mRecyclerSize.setHasFixedSize(true);
        mRecyclerSize.setAdapter(adapter);

        ColorAdapter colorAdapter = new ColorAdapter(this,mList);
        mRecyclerColor.setLayoutManager(new GridLayoutManager(this,4));
        mRecyclerColor.setHasFixedSize(true);
        mRecyclerColor.setAdapter(colorAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public class sizeAdapter extends RecyclerView.Adapter<sizeAdapter.sizeViewHolder>{
        List<Type> mList;
        Context context;
        boolean selected;
        sizeViewHolder prevHolder;

        public sizeAdapter(Context context, List<Type> mList){
            this.context = context;
            this.mList = mList;
        }

        @Override
        public sizeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.button_layout,parent,false);
            return new sizeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final sizeViewHolder holder, int position) {
            Type type = mList.get(position);
            holder.size.setText(type.getSize());
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContextCompat.getDrawable(v.getContext(), R.drawable.button_pressed);

                    if(holder.clicked)
                        holder.disableState();
                    else {
                        holder.enableState();
                        if(prevHolder != null && !prevHolder.equals(holder)){
                            prevHolder.disableState();
                        }
                    }
                    prevHolder = holder;
                }
            });
        }

        @Override
        public void onBindViewHolder(sizeViewHolder holder, int position, List<Object> payloads) {
            if(payloads.isEmpty()){
                selected=false;
                onBindViewHolder(holder,position);
            }else {
                for(Object payload: payloads){
                    if(payload instanceof Type){
                        holder.disableState();
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() : 0);
        }

        class sizeViewHolder extends RecyclerView.ViewHolder {
            TextView size;
            View mView;
            ViewGroup button;
            Boolean clicked=false;

            public sizeViewHolder (View itemView) {
                super(itemView);
                mView = itemView;
                button = (ViewGroup) itemView.findViewById(R.id.button);
                size= (TextView) itemView.findViewById(R.id.size);
            }

            public void disableState(){
                clicked=false;
                button.setSelected(false);
            }
            public void enableState(){
                clicked=true;
                button.setSelected(true);
            }
        }
    }



    public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder>{
        List<Type> mList;
        Context context;
        boolean selected;
        ColorViewHolder prevHolder;

        public ColorAdapter(Context context, List<Type> mList){
            this.context = context;
            this.mList = mList;
        }

        @Override
        public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.button_layout,parent,false);
            return new ColorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ColorViewHolder holder, int position) {
            final Type type = mList.get(position);
            holder.color.setText(type.getColor());
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContextCompat.getDrawable(v.getContext(), R.drawable.button_pressed);

                    if(holder.clicked)
                        holder.disableState();
                    else {
                        holder.enableState();
                        mColor.setText("Color: "+ type.getColor());
                        if(prevHolder != null && !prevHolder.equals(holder)){
                            prevHolder.disableState();
                        }
                    }
                    prevHolder = holder;
                }
            });
        }

        @Override
        public void onBindViewHolder(ColorViewHolder holder, int position, List<Object> payloads) {
            if(payloads.isEmpty()){
                selected=false;
                onBindViewHolder(holder,position);
            }else {
                for(Object payload: payloads){
                    if(payload instanceof Type){
                        holder.disableState();
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() : 0);
        }

        class ColorViewHolder extends RecyclerView.ViewHolder {
            TextView color;
            View mView;
            ViewGroup button;
            Boolean clicked=false;

            public ColorViewHolder (View itemView) {
                super(itemView);
                mView = itemView;
                button = (ViewGroup) itemView.findViewById(R.id.button);
                color = (TextView) itemView.findViewById(R.id.size);
            }

            public void disableState(){
                clicked=false;
                button.setSelected(false);
            }
            public void enableState(){
                clicked=true;
                button.setSelected(true);
            }
        }
    }

}
