package com.example.lukaskris.houseofdesign.Shop;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.HomeActivity;
import com.example.lukaskris.houseofdesign.Model.Category;
import com.example.lukaskris.houseofdesign.Model.CategoryItem;
import com.example.lukaskris.houseofdesign.Model.Items;
import com.example.lukaskris.houseofdesign.Model.SubItem;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Services.MyService;
import com.example.lukaskris.houseofdesign.Services.ServiceFactory;
import com.example.lukaskris.houseofdesign.Splash.SplashActivity;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;
import com.example.lukaskris.houseofdesign.Util.NetworkUtil;
import com.example.lukaskris.houseofdesign.Util.PreferencesUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.viewpagerindicator.CirclePageIndicator;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import endpoint.backend.itemApi.model.Item;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;

public class HomeFragment extends Fragment {

    public static boolean calledActivity = false;
    ViewPagerAdapter mViewPagerAdapter;
    ViewPager mViewPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    CirclePageIndicator mIndicator;
    ProgressDialog mProgress;
    AVLoadingIndicatorView mLoading;
    LinearLayout mNoData;

    SwipeRefreshLayout swipeRefreshLayout;

    int [] mResources = {
            R.drawable.banner1,
            R.drawable.banner2
    };

    private CategoryListAdapter adapter;
    private ArrayList<CategoryItem> categoryItems;
    private ArrayList<Items> allItems;
    private ArrayList<Category> allCategory;
    Runnable runnable;
    Handler handler;
    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) { }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        /* Setting Firebase */
        if(!calledActivity){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledActivity=true;
        }

        getActivity().setTitle("Home");

        /* Setting Banner */
        mViewPager = (ViewPager) view.findViewById(R.id.home_viewpager);
        mViewPagerAdapter = new ViewPagerAdapter(getContext());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mIndicator = (CirclePageIndicator) view.findViewById(R.id.home_circlePageIndicator);
        mIndicator.setViewPager(mViewPager);

        mNoData = (LinearLayout) view.findViewById(R.id.home_no_data);
        mNoData.setVisibility(View.GONE);



        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.home_swipe);
//        swipeRefreshLayout.setColorScheme(getResources().getColor(android.R.color.holo_blue_bright),
//                getResources().getColor(android.R.color.holo_green_light),
//                getResources().getColor(android.R.color.holo_orange_light),
//                getResources().getColor(android.R.color.holo_red_light));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(NetworkUtil.isOnline(getContext())) {
                            fetchData();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },3000);
            }
        });

        mLoading = (AVLoadingIndicatorView) view.findViewById(R.id.home_loading);

        NUM_PAGES = mResources.length;
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mViewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {}

            @Override
            public void onPageScrollStateChanged(int pos) {}
        });
        /* End Setting Banner*/

        /* Setting ProgressBar*/
        mProgress = new ProgressDialog(getContext());
        mProgress.setMessage("Loading...");
        /* End Setting ProgressBar*/

        allCategory = new ArrayList<>();
        categoryItems = new ArrayList<>();
        allItems = new ArrayList<>();

        if(getActivity().getIntent().hasExtra("category") && getActivity().getIntent().hasExtra("items")) {
            allCategory = (ArrayList<Category>) getActivity().getIntent().getSerializableExtra("category");
            allItems = (ArrayList<Items>) getActivity().getIntent().getSerializableExtra("items");
            migrateToCategoryItem();
        }else{
            if(getActivity().getIntent().hasExtra("error")){
                noInternetConnection();
//                Toast.makeText(getContext(), getActivity().getIntent().getStringExtra("error"),Toast.LENGTH_LONG).show();
            }
            ArrayList<CategoryItem> temp =PreferencesUtil.getHome(getContext());
            mLoading.setVisibility(View.GONE);
            if(temp!=null && temp.size()>0){
                categoryItems.addAll(temp);
            }
            else {
                mNoData.setVisibility(View.VISIBLE);
            }
        }
        RecyclerView my_recycler_view = (RecyclerView) view.findViewById(R.id.home_recyclerview);
        my_recycler_view.setHasFixedSize(true);
        my_recycler_view.setNestedScrollingEnabled(false);
        adapter = new CategoryListAdapter(getContext(), categoryItems);
        my_recycler_view.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        my_recycler_view.setAdapter(adapter);
        autoRefreshWhenOff();
        return view;
    }

    void autoRefreshWhenOff(){
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                if(getContext()!=null ){
                    if(NetworkUtil.isOnline(getContext())){
                        fetchData();
                        handler.removeCallbacks(runnable);
                    }else {
                        noInternetConnection();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        };
        runnable.run();

    }

    void migrateToCategoryItem(){
        categoryItems.clear();
        for(Category c:allCategory){
            CategoryItem cm = new CategoryItem(c.getId(),c.getName());

            ArrayList<Items> temp = new ArrayList<>();
            for(Items i: allItems){
                if(i.getCategory() == c.getId()){
                    temp.add(i);
                }
            }
            cm.setAllItemsInSection(temp);
            mLoading.setVisibility(View.GONE);
            categoryItems.add(cm);
        }
        PreferencesUtil.saveHome(getContext(),categoryItems);
    }

    void fetchData(){

        service.getCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> categories) throws Exception {
                        if(categories.size()>0){
                            allCategory.clear();
                            allCategory.addAll(categories);
                            getItem();
                            migrateToCategoryItem();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(throwable.getLocalizedMessage().equalsIgnoreCase("android_getaddrinfo failed: EAI_NODATA (No address associated with hostname)")) {
                            noInternetConnection();
                        }

                    }
                });


    }

    void getItem(){
        service.getItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Items>>() {
                    @Override
                    public void accept(List<Items> itemses) throws Exception {
                        if (itemses.size() > 0) {
                            allItems.clear();
                            allItems.addAll(itemses);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

    }

    void noInternetConnection(){
        Snackbar
            .make(mNoData, "No Internet Connection", Snackbar.LENGTH_LONG)
            .setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fetchData();
                }
            }).show();
    }

    //ADAPTER CLASS

    class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>{
        private ArrayList<CategoryItem> dataList;
        private Context mContext;

        CategoryListAdapter(Context ctx, ArrayList<CategoryItem> dataList){
            this.mContext = ctx;
            this.dataList = dataList;
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_category, null);
            return new CategoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final CategoryViewHolder holder, final int position) {
            final String sectionName = dataList.get(position).getHeaderTitle();

            holder.itemTitle.setText(sectionName);

            /* Setting Adapter RecyclerView*/
//            final Query recentItem = mDatabase.child(sectionName).limitToLast(5);
//                                                    //            .limitToLast(5);
//            FirebaseRecyclerAdapter<Item,ItemViewHolder> adpt = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(Item.class,
//                                                                                            R.layout.home_recyclerview_row,
//                                                                                            ItemViewHolder.class,
//                                                                                            recentItem) {
//                @Override
//                protected void populateViewHolder(final ItemViewHolder viewHolder, Item model, int position) {
//                    int[] posisi = new int[]{4,3,2,1,0};
//                    model = getItem(posisi[viewHolder.getAdapterPosition()]);
//
//                    final String iditem = this.getRef(posisi[viewHolder.getAdapterPosition()]).getKey();
////                    model.setId(this.getRef(posisi[viewHolder.getAdapterPosition()]).getKey());
//                    viewHolder.setImage(getContext(), model.getImage().get(0));
//                    viewHolder.setNama(model.getName());
//                    viewHolder.setHarga(CurrencyUtil.rupiah(new BigDecimal(model.getPrice())));
//                    final Item finalModel = model;
//                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //TODO: Detail Item
//                            Intent intent = new Intent(getActivity(),DetailActivity.class);
//                            intent.putExtra("iditem", finalModel.getId());
//                            intent.putExtra("idfirebase", iditem);
//                            intent.putExtra("category",sectionName);
//                            startActivity(intent);
//                        }
//                    });
//                }
//            };


            final ItemViewAdapter adpt = new ItemViewAdapter(mContext,dataList.get(position).getAllItemsInSection());
            holder.recycler_view_list.setHasFixedSize(true);
            LinearLayoutManager lm = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
            holder.recycler_view_list.setLayoutManager(lm);

            holder.recycler_view_list.setAdapter(adpt);

            holder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),ShowAllActivity.class);
                    intent.putExtra("category",sectionName);
                    startActivity(intent);
                }
            });
    /* End Setting Adapter*/
        }

        @Override
        public int getItemCount() {
            return (null != dataList ? dataList.size() : 0);
        }


        class CategoryViewHolder extends RecyclerView.ViewHolder{
            TextView itemTitle;
            RecyclerView recycler_view_list;
            Button btnMore;
            CategoryViewHolder(View itemView) {
                super(itemView);
                this.itemTitle = (TextView) itemView.findViewById(R.id.category_title);
                this.recycler_view_list = (RecyclerView) itemView.findViewById(R.id.category_recycler);
                this.btnMore= (Button) itemView.findViewById(R.id.btnMore);
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

        @Override
        public int getCount() {
            return mResources.length;
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
            imageView.setImageResource(mResources[position]);

            container.addView(itemView);

            return itemView;
        }
    }

    private class ItemViewAdapter  extends RecyclerView.Adapter<ItemViewHolder>{
        private List<Items> itemList;
        private Context context;

        ItemViewAdapter(Context context, List<Items> list) {
            this.context = context;
            if(list != null)
                this.itemList = list;
            else
                this.itemList = new ArrayList<>();
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_row, null);
            return new ItemViewHolder(layoutView);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            final Items item = itemList.get(position);
            holder.setImage(context,item.getThumbnail());
            holder.setNama(item.getName());
            holder.setHarga(item.getPrice());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: click per item menuju detail
                    Intent inten = new Intent(getActivity(),DetailActivity.class);
                    inten.putExtra("item",item);
                    startActivity(inten);
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }


    }

    private class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView item_nama;
        TextView item_price;
        ImageView item_image;
        View mView;
        ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            item_nama = (TextView)mView.findViewById(R.id.item_nama);
            item_price = (TextView)mView.findViewById(R.id.item_price);
            item_image =(ImageView) mView.findViewById(R.id.item_image);
            //add event click to item
            //TODO: going to detail item
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        void setNama(String nama){
            item_nama.setText(nama);
        }

        void setHarga(String harga){
            item_price.setText(CurrencyUtil.rupiah(new BigDecimal(harga)));
        }

        void setImage(final Context ctx, final String image) {
            Glide.with(ctx)
                    .load("https://storage.googleapis.com/houseofdesign/"+image)
                    .override(100, 100)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(item_image);
        }
    }


}
