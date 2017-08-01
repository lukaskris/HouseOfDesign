package com.example.lukaskris.houseofdesign.Shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.Callback.Callback;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Services.EndpointAsyncTask;
import com.example.lukaskris.houseofdesign.Services.MyServicesAPI;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import endpoint.backend.itemApi.model.Item;

public class DetailActivity extends AppCompatActivity {
    private ViewPager mViewPager;

    ViewPagerAdapter mViewPagerAdapter;
    CirclePageIndicator mIndicator;
    ArrayList<String> imageUrl;

    private TextView mName;
    private TextView mPrice;
    private TextView mDescription;
    private TextView mSelect;

    private Item mItem;

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
//        new EndpointAsyncTask(this).execute();
        String iditem = getIntent().getStringExtra("iditem");

        iditem = "PKA001";

        mName = (TextView) findViewById(R.id.detail_name);
        mPrice = (TextView) findViewById(R.id.detail_price);
        mDescription = (TextView) findViewById(R.id.detail_description);

//        mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setTitle("Fetch Data");
//        mProgressDialog.setMessage("Please wait");
//        mProgressDialog.show();
        MyServicesAPI.getInstance().getItem(this, iditem, new Callback() {
            @Override
            public void onSuccess(Object... params) {
//                mProgressDialog.dismiss();
                Item item = (Item) params[0];
                mItem = item;
                mName.setText(item.getName());
                mPrice.setText(String.valueOf(item.getPrice()));
                mDescription.setText(item.getDesc());
            }

            @Override
            public void onError(Object... params) {
//                mProgressDialog.dismiss();
                Log.d("Result",params[0].toString());
            }
        });
        mViewPager = (ViewPager) findViewById(R.id.detail_viewpager);

        mViewPagerAdapter = new ViewPagerAdapter(this);
        mViewPager.setAdapter(mViewPagerAdapter);
        mIndicator = (CirclePageIndicator) findViewById(R.id.detail_circlePageIndicator);
        mIndicator.setViewPager(mViewPager);

        mSelect = (TextView) findViewById(R.id.detail_select);
        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailActivity.this, TypeActivity.class));
            }
        });
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
        return super.onOptionsItemSelected(item);
    }


    private class ViewPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        ViewPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
