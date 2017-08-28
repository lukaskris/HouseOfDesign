package com.example.lukaskris.houseofdesign.Shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.Callback.Callback;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Services.MyServicesAPI;
import com.example.lukaskris.houseofdesign.Util.AndroidUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import endpoint.backend.itemApi.model.Item;
import endpoint.backend.itemApi.model.Type;

public class DetailActivity extends AppCompatActivity {
    private ViewPager mViewPager;

    ViewPagerAdapter mViewPagerAdapter;
    CirclePageIndicator mIndicator;
    ArrayList<String> imageUrl;

    private TextView mName;
    private TextView mPrice;
    private TextView mDescription;
    private TextView mSelect;

    private Button mAddToCart;
    private Button mBuyNow;

    private Item mItem;

    private DatabaseReference mDatabase;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imageUrl = new ArrayList<>();
        String iditem = getIntent().getStringExtra("iditem");
        final String idfb = getIntent().getStringExtra("idfirebase");
        String category = getIntent().getStringExtra("category");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("category/"+category);

        mName = (TextView) findViewById(R.id.detail_name);
        mPrice = (TextView) findViewById(R.id.detail_price);
        mDescription = (TextView) findViewById(R.id.detail_description);

        mAddToCart = (Button) findViewById(R.id.detail_add_to_cart);
        mBuyNow = (Button) findViewById(R.id.detail_buy_now);

        mBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.showMessage(v,"Item telah dimasukan ke keranjang");
            }
        });

//        mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setTitle("Fetch Data");
//        mProgressDialog.setMessage("Please wait");
//        mProgressDialog.show();

        mViewPager = (ViewPager) findViewById(R.id.detail_viewpager);

        mViewPagerAdapter = new ViewPagerAdapter(this);
        mViewPager.setAdapter(mViewPagerAdapter);
        mIndicator = (CirclePageIndicator) findViewById(R.id.detail_circlePageIndicator);
        mIndicator.setViewPager(mViewPager);

        mSelect = (TextView) findViewById(R.id.detail_select);
        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Type> types = new ArrayList<>();
//                if(!mItem.getType().equals(null))
//                    types.addAll(mItem.getType());
                Type type = new Type();
                type.setSize("L");
                type.setColor("Red");
                type.setQty(10);
                types.add(type);
                Intent intent = new Intent(DetailActivity.this, TypeActivity.class);
                intent.putExtra("type", (Serializable) types);
                intent.putExtra("nama", mName.getText());
                intent.putExtra("harga",mPrice.getText());
                intent.putExtra("foto",imageUrl.get(0));
                startActivity(intent);
            }
        });

        getImage(idfb);
        getSize(iditem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);

        MenuItem item = menu.findItem(R.id.detail_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        return true;
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

    private void getSize(String iditem){
        MyServicesAPI.getInstance().getItem(this, iditem, new Callback() {
            @Override
            public void onSuccess(Object... params) {
//                mProgressDialog.dismiss();
                Item item = (Item) params[0];
                mItem = item;
            }

            @Override
            public void onError(Object... params) {
//                mProgressDialog.dismiss();
                Log.d("Result",params[0].toString());
            }
        });
    }

    private void getImage(String id){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.show();
        mDatabase.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("image").exists()){
                    DataSnapshot image = dataSnapshot.child("image");
                    for (DataSnapshot img : image.getChildren()){
                        mViewPagerAdapter.addItem(img.getValue().toString().trim());
                    }
                    mName.setText(dataSnapshot.child("name").getValue().toString());
                    mPrice.setText(dataSnapshot.child("price").getValue().toString());
                    mDescription.setText(dataSnapshot.child("desc").getValue().toString());
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private class ViewPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        ViewPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(String s){
            imageUrl.add(s);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return imageUrl == null ? 0 : imageUrl.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.home_viewpager_row, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.home_viewpager_imageView);
//            imageView.setImageResource();
            Glide.with(DetailActivity.this).load(imageUrl.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            container.addView(itemView);

            return itemView;
        }
    }
}
