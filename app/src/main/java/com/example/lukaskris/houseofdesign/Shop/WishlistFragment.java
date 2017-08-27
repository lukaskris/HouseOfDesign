package com.example.lukaskris.houseofdesign.Shop;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Util.AdapterCachingUtil;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import endpoint.backend.itemApi.model.Item;
import me.himanshusoni.quantityview.QuantityView;

public class WishlistFragment extends Fragment {

    RecyclerView mRecyclerView;
    List<Item> items;


    public WishlistFragment() {
        // Required empty public constructor
    }

    public static WishlistFragment newInstance(String param1, String param2) {
        WishlistFragment fragment = new WishlistFragment();
        Bundle args = new Bundle();
        return fragment;
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
        mRecyclerView.setHasFixedSize(true);
        items = new ArrayList<>();
        WishlistAdapter adapter = new WishlistAdapter(getContext(),items);
        mRecyclerView.setAdapter(adapter);
        return view;
    }


    private class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {
        Context context;
        List<Item> itemList;

        WishlistAdapter(Context context, List<Item> itemList) {
            this.context = context;
            this.itemList = itemList;
        }

        @Override
        public WishlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(
                    R.layout.shopping_cart_row, parent, false);
            return new WishlistViewHolder(v);
        }

        @Override
        public void onBindViewHolder(WishlistViewHolder holder, final int position) {
            final Item item = itemList.get(position);
            holder.mName.setText(item.getName());
            holder.mPrice.setText(CurrencyUtil.rupiah(new BigDecimal(item.getPrice())));
            Glide.with(context).load(item.getImage().get(0)).diskCacheStrategy(DiskCacheStrategy.RESULT).override(75, 100).into(holder.mImage);

            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Do you want delete this item ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            itemList.remove(position);
                            notifyDataSetChanged();

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

            public WishlistViewHolder(View itemView) {
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
