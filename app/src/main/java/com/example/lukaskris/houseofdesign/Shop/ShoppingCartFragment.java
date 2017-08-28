package com.example.lukaskris.houseofdesign.Shop;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Transaction.ConfirmationActivity;
import com.example.lukaskris.houseofdesign.Util.AdapterCachingUtil;
import com.example.lukaskris.houseofdesign.Util.CurrencyUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import endpoint.backend.itemApi.model.Item;
import endpoint.backend.itemApi.model.Type;
import me.himanshusoni.quantityview.QuantityView;

public class ShoppingCartFragment extends Fragment {
    private String CACHE_NAME_SHOPPING = "shoppingcart";

    private RecyclerView recyclerView;
    private ShoppingCartAdapter adapter;
    private Button mConfirm;
    private TextView mTotal;
    int total = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        getActivity().setTitle("Shopping cart");

        mTotal = (TextView) view.findViewById(R.id.shopping_cart_total);
        mTotal.setText(CurrencyUtil.rupiah(new BigDecimal(total)));

        List<Item> items = AdapterCachingUtil.load(getContext(),CACHE_NAME_SHOPPING);
        items = new ArrayList<>();
        Item item = new Item();
        item.setId("BA");
        item.setName("Baju 1");
        item.setPrice("35000");
        item.setCategory("Pria");
        Type type = new Type();
        type.setColor("Red");
        type.setQty(5);
        type.setSize("M");
        List<Type> types = new ArrayList<>();
        types.add(type);
        item.setType(types);
        items.add(item);
        recyclerView = (RecyclerView) view.findViewById(R.id.shopping_cart_recyclerview);
        adapter = new ShoppingCartAdapter(getContext() ,items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        mConfirm = (Button) view.findViewById(R.id.shopping_cart_confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ConfirmationActivity.class));
            }
        });

        return view;
    }

    private class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ShoppingCartViewHolder>{
        Context context;
        List<Item> itemList;

        ShoppingCartAdapter(Context context, List<Item> itemList){
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
        public void onBindViewHolder(ShoppingCartViewHolder holder, final int position) {
            final Item item = itemList.get(position);
            holder.mName.setText(item.getName());
            holder.mPrice.setText(CurrencyUtil.rupiah(new BigDecimal(item.getPrice())));
            total = total + Integer.parseInt(item.getPrice()) * item.getType().get(0).getQty();
            mTotal.setText(CurrencyUtil.rupiah(new BigDecimal(total)));
            if(item.getImage() != null)
                Glide.with(context).load(item.getImage().get(0)).diskCacheStrategy(DiskCacheStrategy.RESULT).override(75,100).into(holder.mImage);
            String size = item.getType().get(0).getSize();
            String color = item.getType().get(0).getColor();
            holder.mColorSize.setText(color + "; " + size);
            holder.mQty.setQuantity(item.getType().get(0).getQty());
            holder.mQty.setMinQuantity(1);
            holder.mQty.setOnQuantityChangeListener(new QuantityView.OnQuantityChangeListener() {
                @Override
                public void onQuantityChanged(int oldQuantity, int newQuantity, boolean programmatically) {
                    total = total + Integer.parseInt(item.getPrice()) * (newQuantity - oldQuantity);
                    mTotal.setText(CurrencyUtil.rupiah(new BigDecimal(total)));
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
                            notifyDataSetChanged();
                            total = total - Integer.parseInt(item.getPrice()) * item.getType().get(0).getQty();
                            mTotal.setText(CurrencyUtil.rupiah(new BigDecimal(total)));
                            AdapterCachingUtil.store(getContext(), CACHE_NAME_SHOPPING, itemList);
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
            public ShoppingCartViewHolder(View itemView) {
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
