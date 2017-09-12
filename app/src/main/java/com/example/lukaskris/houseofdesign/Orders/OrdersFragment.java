package com.example.lukaskris.houseofdesign.Orders;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lukaskris.houseofdesign.Orders.OrdersPembelianFragment;
import com.example.lukaskris.houseofdesign.Orders.OrdersTagihanFragment;
import com.example.lukaskris.houseofdesign.R;

public class OrdersFragment extends Fragment {
    private ViewPager viewPager;

    public OrdersFragment() {
        // Required empty public constructor
    }

    public static OrdersFragment newInstance() {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Orders");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.orders_viewpager);
        OrdersViewPager adapter = new OrdersViewPager(getContext(), getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.orders_tablayout);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private class OrdersViewPager extends FragmentStatePagerAdapter {

        private Context mContext;

        OrdersViewPager(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        // This determines the fragment for each tab
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new OrdersTagihanFragment();
            } else {
                return new OrdersPembelianFragment();
            }
        }

        // This determines the number of tabs
        @Override
        public int getCount() {
            return 2;
        }

        // This determines the title for each tab
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            switch (position) {
                case 0:
                    return mContext.getString(R.string.orders_tagihan);
                case 1:
                    return mContext.getString(R.string.orders_pembelian);
                default:
                    return null;
            }
        }

    }
}