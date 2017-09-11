package com.example.lukaskris.houseofdesign.Shop;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.lukaskris.houseofdesign.Model.Cart;
import com.example.lukaskris.houseofdesign.Model.SubItem;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Transaction.ConfirmationActivity;
import com.example.lukaskris.houseofdesign.Util.AdapterCachingUtil;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;
import com.example.lukaskris.houseofdesign.Util.PreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import endpoint.backend.itemApi.model.Item;
import endpoint.backend.itemApi.model.Type;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.himanshusoni.quantityview.QuantityView;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;

public class ShoppingCartFragment extends Fragment {
    private String CACHE_NAME_SHOPPING = "shoppingcart";

    private RecyclerView recyclerView;
    private ShoppingCartAdapter adapter;
    private Button mConfirm;
    private TextView mTotal;
    List<Cart> mCarts;
    private ProgressDialog mLoading;
    LinearLayout mNoData;
    int total = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        getActivity().setTitle("Shopping cart");

        mTotal = (TextView) view.findViewById(R.id.shopping_cart_total);
        mTotal.setText(CurrencyUtil.rupiah(new BigDecimal(total)));

        mLoading = new ProgressDialog(getContext());
        mLoading.setMessage("Please wait...");

        mCarts = new ArrayList<>();
        mCarts = PreferencesUtil.getCarts(getContext());

        mNoData = (LinearLayout) view.findViewById(R.id.shopping_no_data);


        if(mCarts == null || mCarts.size()==0){
            mNoData.setVisibility(View.VISIBLE);
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.shopping_cart_recyclerview);
        adapter = new ShoppingCartAdapter(getContext() ,mCarts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        mConfirm = (Button) view.findViewById(R.id.shopping_cart_confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {

                    if (mCarts.size() > 0) {
                        Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
                        intent.putExtra("total", total);
                        int weight = 0;
                        for (Cart c : mCarts) {
                            weight = weight + c.getItem().getWeight() * c.getQuantity();
                        }
                        Log.d("DEBUG WEIGHT", weight + "");
                        intent.putExtra("weight", weight);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    } else {
                        Snackbar.make(mNoData, "No item in Your cart.", Snackbar.LENGTH_SHORT).show();
                    }
                }else {
                    Snackbar.make(mNoData, "Anda harus login terlebih dahulu.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ShoppingCartViewHolder>{
        Context context;
        List<Cart> itemList;

        ShoppingCartAdapter(Context context, List<Cart> itemList){
            this.context = context;
            this.itemList = itemList;
        }

        @Override
        public ShoppingCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(
                    R.layout.shopping_cart_row, parent, false);
            return new ShoppingCartViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ShoppingCartViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final Cart item = itemList.get(position);
            holder.mName.setText(item.getItem().getName());
            holder.mPrice.setText(CurrencyUtil.rupiah(new BigDecimal(item.getItem().getPrice())));
            total = total + item.getItem().getPrice() * item.getQuantity();
            mTotal.setText(CurrencyUtil.rupiah(new BigDecimal(total)));
            if(item.getItem().getThumbnail() != null)
                Glide.with(context).load("https://storage.googleapis.com/houseofdesign/"+item.getItem().getThumbnail()).diskCacheStrategy(DiskCacheStrategy.RESULT).override(75,100).into(holder.mImage);
            String size = item.getSize();
            String color = item.getColor();
            holder.mColorSize.setText(color + "; " + size);
            holder.mQty.setQuantity(item.getQuantity());
            holder.mQty.setMinQuantity(1);
            holder.mQty.setOnQuantityChangeListener(new QuantityView.OnQuantityChangeListener() {
                @Override
                public void onQuantityChanged(final int oldQuantity, final int newQuantity, boolean programmatically) {
                    final Cart c = mCarts.get(position);
                    mLoading.show();
                    service.getSubItems(c.getItem().getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<SubItem>>() {
                                @Override
                                public void accept(List<SubItem> subItems) throws Exception {
                                    if(subItems.size()>0){
                                        for(SubItem s : subItems){
                                            if(s.getId() == c.getSubitem_id()){
                                                if(newQuantity <= s.getQuantity()){
                                                    total = total + item.getItem().getPrice() * (newQuantity - oldQuantity);
                                                    mTotal.setText(CurrencyUtil.rupiah(new BigDecimal(total)));
                                                }else{
                                                    holder.mQty.setQuantity(oldQuantity);
                                                    holder.mQty.setMaxQuantity(s.getQuantity());
                                                }
                                            }
                                        }
                                    }
                                    mLoading.dismiss();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(getContext(),throwable.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                    mLoading.dismiss();
                                }
                            });

                }

                @Override
                public void onLimitReached() {

                }
            });
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
                    builder.setMessage("Do you want delete this item ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            itemList.remove(position);
                            if(itemList.size()==0)
                                mNoData.setVisibility(View.VISIBLE);
                            PreferencesUtil.saveCart(getContext(),itemList);
                            notifyDataSetChanged();
                            total = total - item.getItem().getPrice() * item.getQuantity();
                            mTotal.setText(CurrencyUtil.rupiah(new BigDecimal(total)));
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList == null ? 0: itemList.size();
        }


        class ShoppingCartViewHolder extends RecyclerView.ViewHolder{
            TextView mName;
            TextView mPrice;
            ImageView mImage;
            TextView mColorSize;
            ImageView mDelete;
            View mView;
            QuantityView mQty;
            ShoppingCartViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                mName = (TextView) mView.findViewById(R.id.shopping_cart_row_name);
                mPrice = (TextView) mView.findViewById(R.id.shopping_cart_row_price);
                mImage = (ImageView) mView.findViewById(R.id.shopping_cart_row_image);
                mColorSize = (TextView) mView.findViewById(R.id.shopping_cart_row_color_size);
                mDelete = (ImageView) mView.findViewById(R.id.shopping_cart_row_delete);
                mQty = (QuantityView) mView.findViewById(R.id.shopping_cart_row_quantity);
            }
        }
    }
}
