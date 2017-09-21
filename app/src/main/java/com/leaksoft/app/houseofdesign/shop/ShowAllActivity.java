package com.leaksoft.app.houseofdesign.shop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.leaksoft.app.houseofdesign.model.Items;
import com.leaksoft.app.houseofdesign.R;
import com.leaksoft.app.houseofdesign.util.CurrencyUtil;
import com.wang.avi.AVLoadingIndicatorView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.leaksoft.app.houseofdesign.services.ServiceFactory.service;

public class ShowAllActivity extends AppCompatActivity {


    private ItemAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipe;
    private AVLoadingIndicatorView mLoading;
    private TextView mNoData;

    private int PAGE=0;
    protected Handler handler;
    private List<Items> mList;
    private int category;
    int cursize=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mList = new ArrayList<>();


        category = getIntent().getIntExtra("category",0);
        String section = getIntent().getStringExtra("section");
        getSupportActionBar().setTitle(section);

        handler = new Handler();

        mLoading = (AVLoadingIndicatorView) findViewById(R.id.show_all_loading);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.show_all_refresh);
        mNoData = (TextView) findViewById(R.id.show_all_no_data);

        mSwipe.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light));
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetState();
                        loadProduct(category, PAGE);
                    }
                },2000);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ShowAllActivity.this, 2);
        recyclerView = (RecyclerView) findViewById(R.id.show_all_recyclerview);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ItemAdapter(ShowAllActivity.this,mList,recyclerView);
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mList.size() >= 9) {
                    mList.add(null);
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(mList.size() - 1);
                        }
                    });
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mList.remove(mList.size() - 1);
                            adapter.notifyItemRemoved(mList.size());
                            //add items one by one
                            PAGE++;
                            loadProduct(category, PAGE);

                        }
                    }, 2000);
                }

            }
        });
        recyclerView.setAdapter(adapter);
        loadProduct(category,PAGE);
    }
    private void resetState(){
        mList.clear();
        adapter.resetLoaded();
        adapter.notifyDataSetChanged();
    }
    private void loadProduct(int category, int page) {

        service.getItems(category,page*10,10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Items>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Items> itemses) {
                        mLoading.setVisibility(View.GONE);
                        if(itemses.size()>0){
                            cursize = adapter.getItemCount();
                            mList.addAll(itemses);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyItemRangeInserted(cursize+1, adapter.getItemCount());
                                }
                            });
                        }else {
                            adapter.setIsEnd(true);
                        }
                        adapter.setLoaded();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Snackbar.make(recyclerView, e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        if(mSwipe.isRefreshing())
                            mSwipe.setRefreshing(false);
                        mLoading.setVisibility(View.GONE);
                        if(mList.size() == 0){
                            mNoData.setVisibility(View.VISIBLE);
                        }else {
                            mNoData.setVisibility(View.GONE);
                        }
                    }
                });

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

    private interface OnLoadMoreListener {
        void onLoadMore();
    }

    private class ItemAdapter extends RecyclerView.Adapter{

        private List<Items> itemList;
        private Context context;
        private final int VIEW_ITEM = 1;
        private final int VIEW_PROG = 0;

        private int visibleThreshold = 10;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;
        private boolean isEnd = false;

        ItemAdapter(Context context, List<Items> item, RecyclerView recyclerView) {
            this.context = context;
            this.itemList = item;

            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                        .getLayoutManager();

                recyclerView
                        .addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView,
                                                   int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                totalItemCount = linearLayoutManager.getItemCount();
                                lastVisibleItem = linearLayoutManager
                                        .findLastVisibleItemPosition();

                                if (!isEnd && !loading && (lastVisibleItem + visibleThreshold) > totalItemCount) {
                                    // End has been reached
                                    // Do something
                                    if (onLoadMoreListener != null) {
                                        onLoadMoreListener.onLoadMore();
                                    }
                                    loading = true;
                                }
                            }
                        });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return new StoreViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_vendor_item_v2, parent, false));

            RecyclerView.ViewHolder vh;
            if (viewType == VIEW_ITEM) {
                View v = LayoutInflater.from(context).inflate(
                        R.layout.show_all_recyclerview_row, parent, false);

                vh = new ItemViewHolder(v);
            } else {
                View v = LayoutInflater.from(context).inflate(
                        R.layout.footer_progressbar, parent, false);

                vh = new ProgressViewHolder(v);
            }
            return vh;
        }

        @Override
        public int getItemViewType(int position) {
            return itemList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }

        void setLoaded() {
            loading = false;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemViewHolder) {
                Items item = itemList.get(position);

                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.mItem = item;
                itemViewHolder.item_nama.setText(item.getName());
                itemViewHolder.item_price.setText(CurrencyUtil.rupiah(new BigDecimal(item.getPrice())));
                Glide.with(context).load("https://storage.googleapis.com/houseofdesign/"+item.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(itemViewHolder.item_image);
                itemViewHolder.itemView.setTag(item);
                itemViewHolder.itemView.setOnClickListener(detail);
            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }

        private View.OnClickListener detail = new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        };

        void setIsEnd(Boolean is){
            isEnd=is;
        }

        void resetLoaded(){
            PAGE = 0;
            this.loading = false;
            isEnd=false;
        }
        @Override
        public int getItemCount() {
            return itemList.size();
        }

        void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        class ProgressViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            ProgressViewHolder(View v) {
                super(v);
                progressBar = (ProgressBar) v.findViewById(R.id.footer_progressbar);
            }
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView item_nama;
            TextView item_price;
            ImageView item_image;
            Items mItem;
            View mView;
            ItemViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                item_nama = (TextView) mView.findViewById(R.id.show_all_row_item_nama);
                item_price = (TextView) mView.findViewById(R.id.show_all_row_item_price);
                item_image = (ImageView) mView.findViewById(R.id.show_all_row_item_image);
                //add event click to item
                //TODO: going to detail item
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(ShowAllActivity.this, TypeActivity.class);

                        startActivity(intent);
                    }
                });
            }
        }
    }
}
