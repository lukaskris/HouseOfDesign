package com.example.lukaskris.houseofdesign.Orders;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.Model.Cart;
import com.example.lukaskris.houseofdesign.Model.Items;
import com.example.lukaskris.houseofdesign.Model.Orders;
import com.example.lukaskris.houseofdesign.Model.OrdersDetail;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Transaction.PaymentActivity;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;

public class OrdersTagihanFragment extends Fragment {
    private RecyclerView recyclerView;
    private AVLoadingIndicatorView mLoading;
    private TextView mNoData;
    private EditText mSearch;
    private SwipeRefreshLayout mSwipe;

    int PAGE=0;
    Handler handler;
    List<Orders> mOrders;
    OrderItemAdapter adapter;
    int cursize=0;
    String filter;
    Drawable mDSearch;
    Drawable mDFilter;
    Drawable mDClear;
    private Disposable mDisposable;


    public OrdersTagihanFragment() {}

    public static OrdersTagihanFragment newInstance() {
        OrdersTagihanFragment fragment = new OrdersTagihanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_tagihan, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.orders_item_recyclerview);
        mLoading = (AVLoadingIndicatorView) view.findViewById(R.id.tagihan_loading);
        mNoData = (TextView) view.findViewById(R.id.orders_item_no_data);
        mSearch = (EditText) view.findViewById(R.id.orders_item_search);
        TextView mFilter = (TextView) view.findViewById(R.id.orders_item_filter);
        mSwipe = (SwipeRefreshLayout) view.findViewById(R.id.tagihan_swipe);

        filter = "";

        mDSearch = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_search_white_24dp));
        mDFilter= DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_filter_list_white_24dp));
        mDClear= DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_clear_white_24dp));

        int color = ContextCompat.getColor(getContext(),R.color.colorLightGrey);
        DrawableCompat.setTint(mDSearch, color);
        DrawableCompat.setTint(mDClear,color);
        DrawableCompat.setTint(mDFilter,color);

        mFilter.setCompoundDrawablesWithIntrinsicBounds(mDFilter,null,null,null);
        mSearch.setCompoundDrawablesWithIntrinsicBounds(mDSearch, null, null, null);
        mSwipe.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light));


        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final View view = inflater.inflate(R.layout.alert_dialog_filter_layout,null);
                final RadioButton semua = (RadioButton) view.findViewById(R.id.filter_semua);
                final RadioButton menunggu = (RadioButton) view.findViewById(R.id.filter_menunggu);
                final RadioButton dibayar = (RadioButton) view.findViewById(R.id.filter_dibayar);
                if(filter.equals(""))
                    semua.setChecked(true);
                else if(filter.equals("0"))
                    menunggu.setChecked(true);
                else
                    dibayar.setChecked(true);
                builder.setView(view)
                        .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(semua.isChecked()){
                                    filter="";
                                }else if(menunggu.isChecked()){
                                    filter="0";
                                }else if(dibayar.isChecked()){
                                    filter="1";
                                }

                                resetState();
                                mLoading.setVisibility(View.VISIBLE);
                                mNoData.setVisibility(View.GONE);
                                getOrders();
                            }
                        });
                Dialog dialog = builder.create();
                dialog.show();
            }
        });

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetState();
                        getOrders();
                    }
                },2000);
            }
        });

        Observable<String> textChange = createTextChangeObservable();
        mDisposable = textChange
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mLoading.setVisibility(View.VISIBLE);
                        mNoData.setVisibility(View.GONE);
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, Observable<List<Orders>> >() {

                    @Override
                    public Observable<List<Orders>> apply(String query) throws Exception {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user!=null){
                            PAGE=0;
                            String tempFilter = filter.equals("") ? "*" : filter;
                            String tempSearch = query.equals("") ? "*" : query;
                            return service.getOrders(user.getEmail(), tempSearch, tempFilter, PAGE*10,10);
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Observable<List<Orders>>>() {
                    @Override
                    public void accept(Observable<List<Orders>> listObservable) throws Exception {
                        resetState();
                        listObservable
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<List<Orders>>() {
                            @Override
                            public void accept(List<Orders> orderses) throws Exception {
                                cursize = adapter.getItemCount();
                                mLoading.setVisibility(View.GONE);
                                if(orderses.size()>0) {
                                    mNoData.setVisibility(View.GONE);
                                    cursize = adapter.getItemCount();
                                    for (final Orders o : orderses) {
                                        String[] name = o.getName().split(", ");
                                        String[] thumbnail = o.getThumbnail().split(", ");
                                        List<OrdersDetail> temp = new ArrayList<>();
                                        for (int i = 0; i < name.length; i++) {
                                            OrdersDetail d = new OrdersDetail(o.getInvoice(), name[i], thumbnail[i]);
                                            temp.add(d);
                                        }
                                        o.setDetail(temp);
                                        mOrders.add(o);
                                    }
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyItemRangeInserted(cursize+1, adapter.getItemCount());
                                        }
                                    });
                                }else {
                                    mNoData.setVisibility(View.VISIBLE);
                                }
                                adapter.setLoaded();

                            }
                        });
                    }
                });


        mSearch.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_RIGHT = 2;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mSearch.getText().length()>0) {
                    int leftEdgeOfRightDrawable = mSearch.getRight()
                            - mSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        mSearch.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mOrders = new ArrayList<>();
        handler = new Handler();
        adapter = new OrderItemAdapter(getContext(),mOrders);
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                try {
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
                                getOrders();

                            }
                        }, 2000);
                    }
                }catch (Exception e){
                    //TODO: Kasih crashnalystic
                }
            }
        });
        recyclerView.setAdapter(adapter);
        getOrders();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    private Observable<String> createTextChangeObservable(){
        Observable<String> textChangeObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                final TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void afterTextChanged(Editable s) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(mSearch.getText().toString().length()>0){
                            mSearch.setCompoundDrawablesWithIntrinsicBounds(mDSearch, null, mDClear, null);
                        }else {
                            mSearch.setCompoundDrawablesWithIntrinsicBounds(mDSearch, null, null, null);
                        }

                        emitter.onNext(s.toString());
                    }

                };

                mSearch.addTextChangedListener(watcher);
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        mSearch.removeTextChangedListener(watcher);
                    }
                });
            }
        });
        return textChangeObservable
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.length() >= 2 || s.length() == 0;
                    }
                })
                .debounce(1000, TimeUnit.MILLISECONDS);
    }

    private void resetState(){
        mOrders.clear();
        adapter.resetLoaded();
        adapter.notifyDataSetChanged();
    }

    private interface OnLoadMoreListener {
        void onLoadMore();
    }

    @SuppressWarnings("unchecked")
    private void getOrders(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            String search = mSearch.getText().toString();
            String tempFilter = filter.equals("") ? "*" : filter;
            String tempSearch = search.equals("") ? "*" : search;
            service.getOrders(user.getEmail(), tempSearch, tempFilter, PAGE*10,10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Orders>>() {

                        @Override
                        public void onSubscribe(Disposable d) {}

                        @Override
                        public void onNext(final List<Orders> orderses) {
                            if(orderses.size()>0) {
                                cursize = adapter.getItemCount();
                                for (final Orders o : orderses) {
                                    String[] name = o.getName().split(", ");
                                    String[] thumbnail = o.getThumbnail().split(", ");
                                    List<OrdersDetail> temp = new ArrayList<>();
                                    for (int i = 0; i < name.length; i++) {
                                        OrdersDetail d = new OrdersDetail(o.getInvoice(), name[i], thumbnail[i]);
                                        temp.add(d);
                                    }
                                    o.setDetail(temp);
                                    mOrders.add(o);
                                }
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
                            Snackbar.make(recyclerView,e.getLocalizedMessage(),Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {
                            if(mSwipe.isRefreshing())
                                mSwipe.setRefreshing(false);
                            mLoading.setVisibility(View.GONE);
                            if(mOrders.size() == 0){
                                mNoData.setVisibility(View.VISIBLE);
                            }else {
                                mNoData.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private class OrderItemAdapter extends RecyclerView.Adapter{
        private Context context;
        private List<Orders> ordersList;
        private final int VIEW_ITEM = 1;
        private final int VIEW_PROG = 0;

        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener onLoadMoreListener;
        private int visibleThreshold = 5;
        private boolean loading = true;
        private boolean isEnd = false;

        OrderItemAdapter(Context context, List<Orders> orders){
            this.context = context;
            ordersList = orders;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                recyclerView
                        .addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView,
                                                   int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                                        .getLayoutManager();
                                totalItemCount = linearLayoutManager.getItemCount();
                                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                                if (!isEnd && !loading && (lastVisibleItem + visibleThreshold) > totalItemCount) {
                                    if (onLoadMoreListener != null && !mSwipe.isRefreshing()) {
                                        PAGE++;
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
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof ItemHolder) {
                ItemHolder itemHolder = (ItemHolder) holder;
                final Orders tempOrder = ordersList.get(position);
                final List<OrdersDetail> tempDetail = tempOrder.getDetail();
                itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("OrdersTagihanFragment", "CLICK");
                        Intent intent = new Intent(getActivity(),PaymentActivity.class);
                        List<Cart> tempCart = new ArrayList<Cart>();
                        for(OrdersDetail d: tempDetail){
                            Items item = new Items(d.getName(),d.getPrice(),d.getThumbnail());
                            Cart c = new Cart(item,d.getQuantity());
                            tempCart.add(c);
                        }
                        intent.putExtra("orders", tempOrder);
                        intent.putExtra("items", (Serializable) tempCart);

                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                    }
                });


                itemHolder.mLayoutInfo.setVisibility(View.VISIBLE);

                if(tempOrder.getStatusCode() == 0) {
                    itemHolder.mStatus.setText(tempOrder.getStatus());
                    itemHolder.mDate.setText(tempOrder.getExpired());
                    itemHolder.mStatusInfo.setText("");
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
            View view;
            TextView mName;
            TextView mPrice;
            TextView mDate;
            TextView mStatus;
            TextView mStatusInfo;
            ImageView mImage;
            LinearLayout mLayoutInfo;
            ItemHolder(View itemView) {
                super(itemView);
                view = itemView;
                mName = (TextView) itemView.findViewById(R.id.orders_item_row_title);
                mPrice = (TextView) itemView.findViewById(R.id.orders_item_row_price);
                mDate = (TextView) itemView.findViewById(R.id.orders_item_row_batas);
                mImage = (ImageView) itemView.findViewById(R.id.tagihan_image);
                mStatus = (TextView) itemView.findViewById(R.id.orders_item_row_status);
                mStatusInfo = (TextView) itemView.findViewById(R.id.orders_item_row_status_info);
                mLayoutInfo = (LinearLayout) itemView.findViewById(R.id.orders_item_row_info);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(),"CLICK",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
