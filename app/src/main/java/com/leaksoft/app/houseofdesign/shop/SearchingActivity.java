package com.leaksoft.app.houseofdesign.shop;

import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.leaksoft.app.houseofdesign.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SearchingActivity extends AppCompatActivity {
    List<Search> mList;
    ImageButton mBack;
    SearchView mSearch;
    RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        mBack = (ImageButton) findViewById(R.id.back);
        mSearch = (SearchView) findViewById(R.id.search);
        mRecycler = (RecyclerView) findViewById(R.id.recyclerview) ;

        mList = new ArrayList<>();
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mList.add(new Search("Halo 1", "history"));
        mList.add(new Search("Halo 1", "history"));
        mList.add(new Search("Halo 1", "history"));
        mList.add(new Search("Halo 1", "history"));
        mList.add(new Search("Halo 1", "history"));

        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(SearchingActivity.this, query, Toast.LENGTH_SHORT).show();
                mSearch.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(new SearchAdapter());

    }

    class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{
        boolean isHeaderVisible = false;
        @Override
        public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, null);
            return new SearchViewHolder(layoutView);
        }

        @Override
        public void onBindViewHolder(SearchViewHolder holder, int position) {
            Search item = mList.get(position);
            if(!isHeaderVisible && item.getType().equals("history")){
                holder.header.setVisibility(View.VISIBLE);
                isHeaderVisible=true;
            }

            if(item.getType().equals("history")){
                holder.image.setImageDrawable(ContextCompat.getDrawable(SearchingActivity.this,R.drawable.ic_history_black_24dp));
            }else {
                holder.image.setImageDrawable(ContextCompat.getDrawable(SearchingActivity.this,R.drawable.ic_search_black_24dp));
            }
            holder.detail.setText(item.getDetail());
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class SearchViewHolder extends RecyclerView.ViewHolder{
            LinearLayout header;
            ImageView image;
            TextView detail;
            public SearchViewHolder(View itemView) {
                super(itemView);
                header = (LinearLayout) itemView.findViewById(R.id.search_row_header);
                image = (ImageView) itemView.findViewById(R.id.search_row_image);
                detail = (TextView) itemView.findViewById(R.id.search_row_detail);
            }
        }
    }
    class Search{
        String detail;
        String type;

        public Search(String detail, String type) {
            this.detail = detail;
            this.type = type;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
