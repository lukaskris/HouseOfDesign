package com.example.lukaskris.houseofdesign.Orders;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lukaskris.houseofdesign.R;

import java.util.ArrayList;
import java.util.List;

public class OrdersTagihanFragment extends Fragment {
    private RecyclerView recyclerView;

    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public OrdersTagihanFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OrdersPembelianFragment newInstance(String param1) {
        OrdersPembelianFragment fragment = new OrdersPembelianFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_tagihan, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.orders_item_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        List<String> input = new ArrayList<>();
        input.add("Samsung 1");
        input.add("Samsung 2");
        OrderItemAdapter adapter = new OrderItemAdapter(input);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ItemHolder>{
        private List<String> values;

        OrderItemAdapter(List<String> data){
            values = data;
        }

        public void add(int position, String item) {
            values.add(position, item);
            notifyItemInserted(position);
        }

        @Override
        public OrderItemAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v =
                    inflater.inflate(R.layout.orders_row_item_tagihan, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ItemHolder(v);
        }

        @Override
        public void onBindViewHolder(OrderItemAdapter.ItemHolder holder, int position) {
            final String name = values.get(position);
            holder.mName.setText(name);
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        class ItemHolder extends RecyclerView.ViewHolder{
            TextView mName;
            TextView mPrice;
            TextView mDate;
            TextView mStatus;

            ItemHolder(View itemView) {
                super(itemView);
                mName = (TextView) itemView.findViewById(R.id.orders_item_row_title);
                mPrice = (TextView) itemView.findViewById(R.id.orders_item_row_price);
                mDate = (TextView) itemView.findViewById(R.id.orders_item_row_batas);
                mStatus = (TextView) itemView.findViewById(R.id.orders_item_row_status);
            }
        }
    }
}
