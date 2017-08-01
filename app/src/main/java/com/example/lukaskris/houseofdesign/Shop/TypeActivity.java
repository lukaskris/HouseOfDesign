package com.example.lukaskris.houseofdesign.Shop;

import android.content.Context;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.Model.Type;
import com.example.lukaskris.houseofdesign.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TypeActivity extends AppCompatActivity {
    private TextView mName;
    private TextView mPrice;
    private ImageView mImage;
    private TextView mColor;
    private RecyclerView mRecyclerColor;
    private RecyclerView mRecyclerSize;
    private Button mAddToCart;
    private Button mBuyNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        mName = (TextView) findViewById(R.id.type_name);
        mPrice = (TextView) findViewById(R.id.type_price);
        mImage = (ImageView) findViewById(R.id.type_picture);
        mColor = (TextView) findViewById(R.id.type_color);
        mRecyclerColor = (RecyclerView) findViewById(R.id.type_recycler_color);
        mRecyclerSize = (RecyclerView) findViewById(R.id.type_recycler_size);
        mAddToCart = (Button) findViewById(R.id.type_add_to_cart);
        mBuyNow = (Button) findViewById(R.id.type_buy_now);

        String name = getIntent().getStringExtra("nama");
        String price = getIntent().getStringExtra("harga");
        String foto = getIntent().getStringExtra("foto");
        mName.setText(name);
        mPrice.setText(price);
        Glide.with(this).load(foto).override(120,120).diskCacheStrategy(DiskCacheStrategy.RESULT).into(mImage);

        List<Type> mList = new ArrayList<>();
        Type type = new Type();
        type.setSize("S");

        Type type2 = new Type();
        type2.setSize("M");
        Type type3 = new Type();
        type3.setSize("L");

        mList.add(type);
        mList.add(type2);
        mList.add(type3);

        sizeAdapter adapter = new sizeAdapter(this, mList);
        mRecyclerSize.setLayoutManager(new GridLayoutManager(this,3));
        mRecyclerSize.setAdapter(adapter);

    }

    public class sizeAdapter extends RecyclerView.Adapter<sizeAdapter.sizeViewHolder>{
        List<Type> mList;
        Context context;

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
                    holder.button.setSelected(true);
                    Log.d("Click", (String) holder.size.getText());
                }
            });
        }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() : 0);
        }

        class sizeViewHolder extends RecyclerView.ViewHolder {
            TextView size;
            View mView;
            ViewGroup button;
            public sizeViewHolder (View itemView) {
                super(itemView);
                mView = itemView;
                button = (ViewGroup) itemView.findViewById(R.id.button);
                size= (TextView) itemView.findViewById(R.id.size);
            }
        }
    }

}
