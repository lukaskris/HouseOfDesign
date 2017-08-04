package com.example.lukaskris.houseofdesign.Shop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.Callback.Callback;
import com.example.lukaskris.houseofdesign.Listener.EndlessScrollListener;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Services.MyServicesAPI;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import endpoint.backend.itemApi.model.Item;
import endpoint.backend.itemApi.model.Type;

public class ShowAllActivity extends AppCompatActivity {


    private ItemAdapter adapter;
    private RecyclerView recyclerView;
    private int PAGE=0;
    protected Handler handler;
    private List<Item> mList;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mList = new ArrayList<>();

        category = getIntent().getStringExtra("category");
        getSupportActionBar().setTitle(category);
        MyServicesAPI.getInstance().getItems(this, category,  0, new Callback() {


            @Override
            public void onSuccess(Object... params) {

                mList.addAll((List<Item>) params[0]);
                Log.d("MLIST", ""+mList.size());
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
            }

            @Override
            public void onError(Object... params) {
                Log.d("OnError", params[0].toString());
            }

        });
    }

    private void loadProduct(String category, int page) {


        MyServicesAPI.getInstance().getItems(this, category,  page*10, new Callback() {
            @Override
            public void onSuccess(Object... params) {
                int cursize = mList.size();
                //noinspection unchecked
                mList.addAll((List<Item>) params[0]);

                adapter.notifyItemRangeInserted(cursize, mList.size()-1);
                adapter.setLoaded();
            }

            @Override
            public void onError(Object... params) {
//                Snackbar.make(,params[0].toString(), Snackbar.LENGTH_SHORT);
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

        private List<Item> itemList;
        private Context context;
        private final int VIEW_ITEM = 1;
        private final int VIEW_PROG = 0;

        private int visibleThreshold = 10;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;

        ItemAdapter(Context context, List<Item> item, RecyclerView recyclerView) {
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

                                if (!loading && (lastVisibleItem + visibleThreshold) > totalItemCount) {
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
                Item item = itemList.get(position);

                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.mItem = item;
                itemViewHolder.item_nama.setText(item.getName());
                itemViewHolder.item_price.setText(CurrencyUtil.rupiah(new BigDecimal(item.getPrice())));
                Glide.with(context).load(item.getImage()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(itemViewHolder.item_image);
                itemViewHolder.itemView.setTag(item);
                itemViewHolder.itemView.setOnClickListener(detail);
            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }

        private View.OnClickListener detail = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item vendor = (Item) view.getTag();

            }
        };

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
            Item mItem;
            View mView;
            public ItemViewHolder(View itemView) {
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
                        List<Type> types = new ArrayList<>();
                        types.addAll(mItem.getType());

                        Intent intent = new Intent(ShowAllActivity.this, TypeActivity.class);
                        intent.putExtra("type", (Serializable) types);
                        intent.putExtra("nama", item_nama.getText());
                        intent.putExtra("harga",item_price.getText());
                        intent.putExtra("foto",mItem.getImage().get(0));
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
