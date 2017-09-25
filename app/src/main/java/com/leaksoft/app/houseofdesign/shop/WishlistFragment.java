package com.leaksoft.app.houseofdesign.shop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.leaksoft.app.houseofdesign.model.Items;
import com.leaksoft.app.houseofdesign.R;
import com.leaksoft.app.houseofdesign.util.CurrencyUtil;
import com.leaksoft.app.houseofdesign.util.PreferencesUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    RecyclerView mRecyclerView;
    List<Items> items;
    LinearLayout mNodata;

    public WishlistFragment() {
        // Required empty public constructor
    }

    public static WishlistFragment newInstance() {

        return new WishlistFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.wishlist_recyclerview);
        mNodata = (LinearLayout) view.findViewById(R.id.wishlist_no_data);
        mRecyclerView.setHasFixedSize(true);
        items = new ArrayList<>();
        items = PreferencesUtil.getFavorites(getContext());
        if(items.size()==0){
            mNodata.setVisibility(View.VISIBLE);
        }
        WishlistAdapter adapter = new WishlistAdapter(getContext(),items);
        mRecyclerView.setAdapter(adapter);
        return view;
    }


    private class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {
        Context context;
        List<Items> itemList;

        WishlistAdapter(Context context, List<Items> itemList) {
            this.context = context;
            this.itemList = itemList;
        }

        @Override
        public WishlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(
                    R.layout.wishlist_recyclerview_row, parent, false);
            return new WishlistViewHolder(v);
        }

        @Override
        public void onBindViewHolder(WishlistViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final Items item = itemList.get(position);
            holder.mName.setText(item.getName());
            holder.mPrice.setText(CurrencyUtil.rupiah(new BigDecimal(item.getPrice())));
            Glide.with(context).load("https://storage.googleapis.com/houseofdesign/"+item.getThumbnail())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).override(75, 100).into(holder.mImage);

            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Apa ingin menghapus item ini dari daftar wishlist ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            itemList.remove(position);
                            PreferencesUtil.saveFavorites(getContext(),itemList);
                            Snackbar.make(mRecyclerView,"Item berhasil dihapus", Snackbar.LENGTH_SHORT).show();
                            notifyItemRemoved(position);
                            if(itemList.size()==0){
                                mNodata.setVisibility(View.VISIBLE);
                            }else {
                                mNodata.setVisibility(View.GONE);
                            }

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
            return itemList == null ? 0 : itemList.size();
        }


        class WishlistViewHolder extends RecyclerView.ViewHolder {
            TextView mName;
            TextView mPrice;
            ImageView mImage;
            ImageView mDelete;
            View mView;

            WishlistViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                mName = (TextView) mView.findViewById(R.id.wishlist_row_name);
                mPrice = (TextView) mView.findViewById(R.id.wishlist_row_price);
                mImage = (ImageView) mView.findViewById(R.id.wishlist_row_image);
                mDelete = (ImageView) mView.findViewById(R.id.wishlist_row_delete);
            }
        }
    }

}
