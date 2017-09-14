package com.example.lukaskris.houseofdesign.Orders;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.Model.Orders;
import com.example.lukaskris.houseofdesign.Model.OrdersDetail;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;

public class OrdersTagihanFragment extends Fragment {
    private RecyclerView recyclerView;
    int PAGE=0;
    Handler handler;
    List<Orders> mOrders;
    OrderItemAdapter adapter;

    public OrdersTagihanFragment() {
        // Required empty public constructor
    }

    public static OrdersPembelianFragment newInstance() {
        OrdersPembelianFragment fragment = new OrdersPembelianFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_tagihan, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.orders_item_recyclerview);
        recyclerView.setHasFixedSize(true);

//        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mOrders = new ArrayList<>();
        handler = new Handler();
        adapter = new OrderItemAdapter(getContext(),mOrders);
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mOrders.size() >= 9) {
                    mOrders.add(null);
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(mOrders.size() - 1);
                        }
                    });
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mOrders.remove(mOrders.size() - 1);
                            adapter.notifyItemRemoved(mOrders.size());
                            //add items one by one

                            getOrders();

                        }
                    }, 2000);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        getOrders();
        return view;
    }

    private interface OnLoadMoreListener {
        void onLoadMore();
    }

    @SuppressWarnings("unchecked")
    private void getOrders(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
//            service.getOrders(user.getEmail(), PAGE*10, 10)
//                    .flatMap(new Function<List<Orders>, ObservableSource<Orders>>() {
//                        @Override
//                        public ObservableSource<Orders> apply(List<Orders> orderses) throws Exception {
//                            return Observable.fromIterable(orderses);
//                        }
//                    })
//                    .flatMap(new Function<Orders, ObservableSource<?>>() {
//                        @Override
//                        public ObservableSource<List<OrdersDetail>> apply(Orders orders) throws Exception {
//                            mOrders.add(orders);
//                            return service.getOrdersDetail(orders.getInvoice());
//                        }
//                    })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<Object>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onNext(Object o) {
//                            List<OrdersDetail> mDetail = (List<OrdersDetail>) o;
//                            for(Orders orders : mOrders){
//                                if(orders.getInvoice().equalsIgnoreCase(mDetail.get(0).getInvoice())){
//                                    orders.setDetail(mDetail);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onComplete() {
//                            adapter.notifyDataSetChanged();
//                            adapter.setLoaded();
//                        }
//                    });
            service.getOrders(user.getEmail(),PAGE*10,10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Orders>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(final List<Orders> orderses) {
                            if(orderses.size()>0) {
                                for (final Orders o : orderses) {
                                    String[] name = o.getName().split(", ");
                                    String[] thumbnail = o.getThumbnail().split(", ");
                                    List<OrdersDetail> temp = new ArrayList<OrdersDetail>();
                                    for (int i = 0; i < name.length; i++) {
                                        OrdersDetail d = new OrdersDetail(o.getInvoice(), name[i], thumbnail[i]);
                                        temp.add(d);
                                    }
                                    o.setDetail(temp);
                                    mOrders.add(o);
                                }
                            }else {
                                adapter.setLoaded();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Snackbar.make(recyclerView,e.getLocalizedMessage(),Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
        }
    }

    private class OrderItemAdapter extends RecyclerView.Adapter{
        private Context context;
        private List<Orders> ordersList;
        private final int VIEW_ITEM = 1;
        private final int VIEW_PROG = 0;

        private int visibleThreshold = 10;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;

        OrderItemAdapter(Context context, List<Orders> orders){
            this.context = context;
            ordersList = orders;
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
                                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                                if (!loading && (lastVisibleItem + visibleThreshold) > totalItemCount) {
                                    if (onLoadMoreListener != null) {
                                        PAGE++;
                                        onLoadMoreListener.onLoadMore();
                                    }
                                    loading = true;
                                }
                            }
                        });
            }
        }

        public void add(int position, Orders item) {
            ordersList.add(position, item);
            notifyItemInserted(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            RecyclerView.ViewHolder vh;
            if (viewType == VIEW_ITEM) {
                View v = inflater.inflate(R.layout.orders_row_item_tagihan, parent, false);
                vh = new ItemHolder(v);
            } else {
                View v = LayoutInflater.from(context).inflate(
                        R.layout.footer_progressbar, parent, false);

                vh = new ProgressViewHolder(v);
            }
            return vh;
        }

        @Override
        public int getItemViewType(int position) {
            return ordersList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemHolder) {
                ItemHolder itemHolder = (ItemHolder) holder;
                Orders tempOrder = ordersList.get(position);

                List<OrdersDetail> tempDetail = tempOrder.getDetail();
                if(tempOrder.getStatusCode() == 0) {
                    itemHolder.mStatus.setText(tempOrder.getStatus());
                    itemHolder.mDate.setText(tempOrder.getExpired());
                }else {
                    itemHolder.mLayoutInfo.setVisibility(View.GONE);
                    itemHolder.mStatusInfo.setText(tempOrder.getStatus());
                }
                itemHolder.mPrice.setText(CurrencyUtil.rupiah(new BigDecimal(tempOrder.getTotal())));

                itemHolder.mName.setText(tempOrder.getName());
                Glide.with(getContext()).load("https://storage.googleapis.com/houseofdesign/"+tempDetail.get(0)
                        .getThumbnail()).override(150,150)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(itemHolder.mImage);

            }
        }

        void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return ordersList.size();
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

        class ItemHolder extends RecyclerView.ViewHolder{
            TextView mName;
            TextView mPrice;
            TextView mDate;
            TextView mStatus;
            TextView mStatusInfo;
            ImageView mImage;
            LinearLayout mLayoutInfo;
            ItemHolder(View itemView) {
                super(itemView);
                mName = (TextView) itemView.findViewById(R.id.orders_item_row_title);
                mPrice = (TextView) itemView.findViewById(R.id.orders_item_row_price);
                mDate = (TextView) itemView.findViewById(R.id.orders_item_row_batas);
                mImage = (ImageView) itemView.findViewById(R.id.tagihan_image);
                mStatus = (TextView) itemView.findViewById(R.id.orders_item_row_status);
                mStatusInfo = (TextView) itemView.findViewById(R.id.orders_item_row_status_info);
                mLayoutInfo = (LinearLayout) itemView.findViewById(R.id.orders_item_row_info);
            }
        }
    }
}
