package com.leaksoft.app.houseofdesign.shop;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.leaksoft.app.houseofdesign.R;
import com.leaksoft.app.houseofdesign.model.Items;
import com.leaksoft.app.houseofdesign.util.NetworkUtil;
import com.leaksoft.app.houseofdesign.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.leaksoft.app.houseofdesign.services.ServiceFactory.service;

public class SearchingActivity extends AppCompatActivity {
    List<Search> mList;
    ImageButton mBack;
    SearchView mSearch;
    RecyclerView mRecycler;
    List<String> mHistory;
    SearchAdapter adapter;

    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    String newText="";

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

        mHistory = PreferencesUtil.getSearch(this);
        if(mHistory!=null && mHistory.size()>0) {
            Collections.reverse(mHistory);
            for (String s : mHistory) {
                mList.add(new Search(s,"history"));
            }
        }

        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                PreferencesUtil.addSearch(SearchingActivity.this, query);

                Intent intent = new Intent(SearchingActivity.this, ShowAllActivity.class);
                intent.putExtra("name", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                handler.removeCallbacks(input_finish_checker);
                if(text.length()>1){
                    handler.postDelayed(input_finish_checker, delay);
                    newText = text;
                }else{
                    clearSuggestions();
                }
                return true;
            }
        });
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter();
        mRecycler.setAdapter(adapter);

        if(!NetworkUtil.isOnline(SearchingActivity.this)){
            noInternetConnection();
            mSearch.clearFocus();
        }
    }

    void noInternetConnection(){
        Snackbar.make(mSearch, "No Internet Connection", Snackbar.LENGTH_LONG).show();
    }

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                if(mHistory!=null)
                    searchSuggestions(newText);
            }
        }
    };

    void clearSuggestions(){
        mList.clear();
        for(String h: mHistory){
            mList.add(new Search(h,"history"));
        }
        adapter.notifyDataSetChanged();
    }

    void searchSuggestions(final String search){
        service.getSearch(search).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<List<Items>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Items> itemses) {
                        mList.clear();
                        for(String h: mHistory){
                            if(h.toLowerCase().contains(search)){
                                mList.add(new Search(h,"history"));
                            }
                        }
                        if(itemses != null && itemses.size()>0){
                            for(Items i : itemses){
                                mList.add(new Search(i.getName(), "item"));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //TODO find in api

    }

    class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{
        boolean isHeaderVisible = false;
        @Override
        public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, null);
            return new SearchViewHolder(layoutView);
        }

        @Override
        public void onBindViewHolder(final SearchViewHolder holder, int position) {
            final Search item = mList.get(position);
            if(position>0) {
                Search prevItem = mList.get(position - 1);
                if (!item.getType().equals(prevItem.getType())){
                    isHeaderVisible = false;
                }
            }
            if(!isHeaderVisible && item.getType().equals("history")){
                holder.header.setVisibility(View.VISIBLE);
                isHeaderVisible=true;
            }else if(!isHeaderVisible && item.getType().equals("item")){
                holder.header.setVisibility(View.VISIBLE);
                holder.headerTitle.setText("Hasil pencarian");
                isHeaderVisible=true;
            }

            if(item.getType().equals("history")){
                holder.image.setImageDrawable(ContextCompat.getDrawable(SearchingActivity.this,R.drawable.ic_history_black_24dp));
            }else {
                holder.image.setImageDrawable(ContextCompat.getDrawable(SearchingActivity.this,R.drawable.ic_search_black_24dp));
            }
            holder.detail.setText(item.getDetail());

            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchingActivity.this, ShowAllActivity.class);
                    intent.putExtra("name", item.getDetail());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class SearchViewHolder extends RecyclerView.ViewHolder{
            LinearLayout header;
            ImageView image;
            TextView detail;
            TextView headerTitle;
            LinearLayout mContainer;
            SearchViewHolder(View itemView) {
                super(itemView);
                mContainer = (LinearLayout) itemView.findViewById(R.id.search_container);
                header = (LinearLayout) itemView.findViewById(R.id.search_row_header);
                image = (ImageView) itemView.findViewById(R.id.search_row_image);
                detail = (TextView) itemView.findViewById(R.id.search_row_detail);
                headerTitle = (TextView) itemView.findViewById(R.id.search_row_header_title);
            }
        }
    }

    private class Search{
        String detail;
        String type;

        Search(String detail, String type) {
            this.detail = detail;
            this.type = type;
        }

        String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
