package com.example.lukaskris.houseofdesign.Shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.Callback.Callback;
import com.example.lukaskris.houseofdesign.Model.Cart;
import com.example.lukaskris.houseofdesign.Model.Items;
import com.example.lukaskris.houseofdesign.Model.SubItem;
import com.example.lukaskris.houseofdesign.Multimedia.FullScreenImageViewerActivity;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Services.MyServicesAPI;
import com.example.lukaskris.houseofdesign.Util.AndroidUtil;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;
import com.example.lukaskris.houseofdesign.Util.PreferencesUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import endpoint.backend.itemApi.model.Item;
import endpoint.backend.itemApi.model.Type;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.himanshusoni.quantityview.QuantityView;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;

public class DetailActivity extends AppCompatActivity {
    private ViewPager mViewPager;

    ViewPagerAdapter mViewPagerAdapter;
    CirclePageIndicator mIndicator;
    ArrayList<String> imageUrl;
    ArrayList<SubItem> subItems;
    ArrayList<String> sizeList;
    ArrayList<String> colorList;

    private TextView mName;
    private TextView mPrice;
    private TextView mDescription;
    private RecyclerView mRecyclerSize;
    private RecyclerView mRecyclerColor;
    private Button mAddToCart;
    private TextView mQuantity;

    private List<Integer> mStock;
    private TypeAdapter sizeAdapter;
    private TypeAdapter colorAdapter;

    private QuantityView mQuantityInput;
    private DatabaseReference mDatabase;
    Items item;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imageUrl = new ArrayList<>();
        item = (Items) getIntent().getSerializableExtra("item");
//
//        String iditem = getIntent().getStringExtra("iditem");
//        final String idfb = getIntent().getStringExtra("idfirebase");
//        String category = getIntent().getStringExtra("category");

//        mDatabase = FirebaseDatabase.getInstance().getReference().child("category/"+category);

        mName = (TextView) findViewById(R.id.detail_name);
        mPrice = (TextView) findViewById(R.id.detail_price);
        mDescription = (TextView) findViewById(R.id.detail_description);
        mQuantity = (TextView)findViewById(R.id.detail_quantity);
        mAddToCart = (Button) findViewById(R.id.detail_add_to_cart);
        mQuantityInput = (QuantityView) findViewById(R.id.detail_quantity_input);


        mRecyclerColor = (RecyclerView) findViewById(R.id.detail_recycler_color);
        mRecyclerSize = (RecyclerView) findViewById(R.id.detail_recycler_size);

        mName.setText(item.getName());
        mPrice.setText(CurrencyUtil.rupiah(new BigDecimal(item.getPrice())));
        mDescription.setText(item.getDescription());

        mViewPagerAdapter = new ViewPagerAdapter(this);
        for(String s : item.getImages()){
            mViewPagerAdapter.addItem("https://storage.googleapis.com/houseofdesign/"+s.trim());
        }
        getSubItem(item);

        mStock = new ArrayList<>();
        mAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mQuantityInput.getQuantity()>0) {
//                    PreferencesUtil.getCarts()
                    String color = colorList.get(colorAdapter.selected_position);
                    String size = sizeList.get(sizeAdapter.selected_position);
//                    List<Cart> cart = new ArrayList<Cart>();

                    int id=0;
                    for(SubItem s: subItems){
                        if(s.getColor().equals(color) && s.getSize().equals(size)){
                            id=s.getId();
                        }
                    }
                    PreferencesUtil.addCart(DetailActivity.this, new Cart(item,color,size,id,mQuantityInput.getQuantity(),mQuantityInput.getMaxQuantity()));

                    AndroidUtil.showMessage(v, "Item telah dimasukan ke keranjang");
                }else
                    AndroidUtil.showMessage(v,"Jumlah barang harus lebih dari 0");

            }
        });

        mViewPager = (ViewPager) findViewById(R.id.detail_viewpager);

        mViewPager.setAdapter(mViewPagerAdapter);
        mIndicator = (CirclePageIndicator) findViewById(R.id.detail_circlePageIndicator);
        mIndicator.setViewPager(mViewPager);

        subItems = new ArrayList<>();
        sizeList = new ArrayList<>();
        colorList = new ArrayList<>();

        sizeAdapter = new TypeAdapter(this, sizeList,0);
        mRecyclerSize.setLayoutManager(new GridLayoutManager(this,4));
        mRecyclerSize.setHasFixedSize(true);
        mRecyclerSize.setAdapter(sizeAdapter);

        colorAdapter = new TypeAdapter(this,colorList,1);
        mRecyclerColor.setLayoutManager(new GridLayoutManager(this,4));
        mRecyclerColor.setHasFixedSize(true);
        mRecyclerColor.setAdapter(colorAdapter);

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
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
            case R.id.detail_cart:
                startActivity(new Intent(DetailActivity.this,ShoppingCartActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            default:
                break;
        }
        return true;
    }

    private void getSubItem(Items item){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.show();
        service.getSubItems(item.getId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<SubItem>>() {
                @Override
                public void accept(List<SubItem> result) throws Exception {
                    mProgressDialog.dismiss();
//                    Toast.makeText(DetailActivity.this, subItems.size(), Toast.LENGTH_SHORT).show();
                    if(result.size()>0){
                        subItems.addAll(result);
                        setTypeList();
                        sizeAdapter.notifyDataSetChanged();
                        colorAdapter.notifyDataSetChanged();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    mProgressDialog.dismiss();
                    Toast.makeText(DetailActivity.this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void setTypeList(){
        sizeList.clear();
        for(SubItem s:subItems){
            if(!sizeList.contains(s.getSize())){
                sizeList.add(s.getSize());
            }
        }
    }

    private void setTypeColor(String size){
        colorList.clear();

        colorAdapter = new TypeAdapter(this,colorList,1);
        mRecyclerColor.setAdapter(colorAdapter);
        mStock.clear();
        for (SubItem s : subItems) {
            if (s.getSize().equals(size)) {
                if (!colorList.contains(s.getColor())) {
                    colorList.add(s.getColor());
                    mStock.add(s.getQuantity());
                }
            }
        }
        colorAdapter.notifyDataSetChanged();

    }

    private void updateStock(){
        String size = sizeList.get(sizeAdapter.selected_position);
        if(colorList.size()>0) {
            String color = colorList.get(colorAdapter.selected_position);
            for (SubItem s : subItems) {
                if (s.getColor().equals(color) && s.getSize().equals(size)) {
                    mQuantity.setText("Sisa: " + s.getQuantity());
                    mQuantityInput.setMaxQuantity(s.getQuantity());
                }
            }
        }
    }

    private boolean checkAvailableStock(String color){
        String size = sizeList.get(sizeAdapter.selected_position);
        for(SubItem s: subItems){
            if(s.getSize().equals(size)){

                if (s.getColor().equals(color)){
                    return s.getQuantity() > 0;
                }
            }
        }

        return false;
    }

    private class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeViewHolder>{
        List<String> mList;
        Context context;
        boolean selected;
        TypeViewHolder prevHolder;
        int mark;
        int selected_position=0;

        public TypeAdapter(Context context, List<String> mList, int type){
            this.context = context;
            this.mList = mList;
            mark = type;
        }

        @Override
        public TypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.button_layout,parent,false);

            return new TypeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TypeViewHolder holder, final int position) {
            final String type = mList.get(position);
            holder.type.setText(type);

            holder.button.setSelected(selected_position == position);

            if(mark==1){
                if(!checkAvailableStock(type)){
                    holder.button.setEnabled(false);
                }
            }

            if(selected_position == position){
                if(mark==0){
                    setTypeColor(type);
                }
                updateStock();
            }

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ContextCompat.getDrawable(v.getContext(), R.drawable.button_pressed);
                    notifyItemChanged(selected_position);
                    selected_position = position;
                    notifyItemChanged(selected_position);

//                    if(holder.clicked) {
//                        holder.disableState();
//                        if(mark==0)
//                            clearTypeColor();
//                    }else {
//                        if(mark == 0){
//                            setTypeColor(type);
//                        }else{
//                            mQuantity.setText("Sisa: "+mStock.get(position));
//                            if(mStock.get(position) <=0){
//                                holder.button.setEnabled(false);
//                            }else {
//                                holder.button.setEnabled(true);
//                            }
//                        }
//
////                        holder.enableState();
////                        if(prevHolder != null && !prevHolder.equals(holder)){
////                            prevHolder.disableState();
////                        }
//                    }
//                    prevHolder = holder;
                }
            });
        }

//        @Override
//        public void onBindViewHolder(TypeViewHolder holder, int position, List<Object> payloads) {
//            if(payloads.isEmpty()){
//                selected=false;
//                onBindViewHolder(holder,position);
//            }else {
//                for(Object payload: payloads){
//                    if(payload instanceof Type){
//                        holder.disableState();
//                    }
//                }
//            }
//        }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() : 0);
        }

        class TypeViewHolder extends RecyclerView.ViewHolder{
            TextView type;
            View mView;
            ViewGroup button;
            Boolean clicked=false;

            public TypeViewHolder (View itemView) {
                super(itemView);
                mView = itemView;
                button = (ViewGroup) itemView.findViewById(R.id.button);
                type= (TextView) itemView.findViewById(R.id.size);
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

        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.home_viewpager_row, container, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DetailActivity.this, FullScreenImageViewerActivity.class);
                    intent.putExtra("images",imageUrl);
                    intent.putExtra("posisi",position);
                    startActivity(intent);
                }
            });
            ImageView imageView = (ImageView) itemView.findViewById(R.id.home_viewpager_imageView);
//            imageView.setImageResource();
            Glide.with(DetailActivity.this).load(imageUrl.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            container.addView(itemView);

            return itemView;
        }
    }
}
