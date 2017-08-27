package com.example.lukaskris.houseofdesign.Shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import endpoint.backend.itemApi.model.Item;

public class HomeFragment extends Fragment {

    public static boolean calledActivity = false;
    ViewPagerAdapter mViewPagerAdapter;
    ViewPager mViewPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    CirclePageIndicator mIndicator;
    RecyclerView mRecycler;
    ItemViewAdapter mRecyclerViewAdapter;
    ProgressDialog mProgress;

    private DatabaseReference mDatabase;

    int [] mResources = {
            R.drawable.pemandangan1,
            R.drawable.pemandangan1,
            R.drawable.pemandangan1,
            R.drawable.pemandangan1,
            R.drawable.pemandangan1,
            R.drawable.pemandangan1
    };


    private ArrayList<CategoryItem> allCategory;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /* Setting Firebase */
        if(!calledActivity){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledActivity=true;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("category");
        mDatabase.keepSynced(true);
        /* End Setting Firebase*/

        getActivity().setTitle("Home");

        /* Setting Banner */
        mViewPager = (ViewPager) view.findViewById(R.id.home_viewpager);
        mViewPagerAdapter = new ViewPagerAdapter(getContext());
        mViewPager.setAdapter(mViewPagerAdapter);

        mIndicator = (CirclePageIndicator) view.findViewById(R.id.home_circlePageIndicator);
        mIndicator.setViewPager(mViewPager);

        NUM_PAGES = mResources.length;
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mViewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
        // Pager listener over indicator
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
        /* End Setting Banner*/

        /* Setting ProgressBar*/
        mProgress = new ProgressDialog(getContext());
        mProgress.setMessage("Loading...");
        /* End Setting ProgressBar*/

        createCategory();

        RecyclerView my_recycler_view = (RecyclerView) view.findViewById(R.id.home_recyclerview);
        my_recycler_view.setHasFixedSize(true);
        my_recycler_view.setNestedScrollingEnabled(false);
        CategoryListAdapter adapter = new CategoryListAdapter(getContext(), allCategory);

        my_recycler_view.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        my_recycler_view.setAdapter(adapter);
        return view;
    }

    private void createCategory(){
        allCategory = new ArrayList<>();
        CategoryItem cm = new CategoryItem("Pria");
        CategoryItem cm1 = new CategoryItem("Wanita");
        CategoryItem cm2 = new CategoryItem("Anak");
        allCategory.add(cm);
        allCategory.add(cm1);
        allCategory.add(cm2);
    }

    private class CategoryItem{
        private String headerTitle;
        private ArrayList<Item> allItemsInSection;

        public CategoryItem(String headerTitle) {
            this.headerTitle = headerTitle;
        }

        public String getHeaderTitle() {
            return headerTitle;
        }

        public void setHeaderTitle(String headerTitle) {
            this.headerTitle = headerTitle;
        }

        public ArrayList<Item> getAllItemsInSection() {
            return allItemsInSection;
        }

        public void setAllItemsInSection(ArrayList<Item> allItemsInSection) {
            this.allItemsInSection = allItemsInSection;
        }
    }

    class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>{
        private ArrayList<CategoryItem> dataList;
        private Context mContext;

        CategoryListAdapter(Context ctx, ArrayList<CategoryItem> dataList){
            this.mContext = ctx;
            this.dataList = dataList;
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_category, null);
            return new CategoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            final String sectionName = dataList.get(position).getHeaderTitle();

            holder.itemTitle.setText(sectionName);

            /* Setting Adapter RecyclerView*/
            final Query recentItem = mDatabase.child(sectionName).limitToLast(5);
                                                    //            .limitToLast(5);
            FirebaseRecyclerAdapter<Item,ItemViewHolder> adpt = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(Item.class,
                                                                                            R.layout.home_recyclerview_row,
                                                                                            ItemViewHolder.class,
                                                                                            recentItem) {
                @Override
                protected void populateViewHolder(final ItemViewHolder viewHolder, Item model, int position) {
                    int[] posisi = new int[]{4,3,2,1,0};
                    model = getItem(posisi[viewHolder.getAdapterPosition()]);

                    final String iditem = this.getRef(posisi[viewHolder.getAdapterPosition()]).getKey();
//                    model.setId(this.getRef(posisi[viewHolder.getAdapterPosition()]).getKey());
                    viewHolder.setImage(getContext(), model.getImage().get(0));
                    viewHolder.setNama(model.getName());
                    viewHolder.setHarga(model.getPrice());
                    final Item finalModel = model;
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: Detail Item
                            Intent intent = new Intent(getActivity(),DetailActivity.class);
                            intent.putExtra("iditem", finalModel.getId());
                            intent.putExtra("idfirebase", iditem);
                            intent.putExtra("category",sectionName);
                            startActivity(intent);
                        }
                    });
                }
            };

            holder.recycler_view_list.setHasFixedSize(true);
            LinearLayoutManager lm = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
            holder.recycler_view_list.setLayoutManager(lm);

            holder.recycler_view_list.setAdapter(adpt);

            holder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: MORE ITEM
//                    Toast.makeText(v.getContext(), "click event on more, "+sectionName , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),ShowAllActivity.class);
                    intent.putExtra("category",sectionName);
                    startActivity(intent);
                }
            });
//        getDataFromFirebase(adapter.itemList);
    /* End Setting Adapter*/
        }

        @Override
        public int getItemCount() {
            return (null != dataList ? dataList.size() : 0);
        }


        class CategoryViewHolder extends RecyclerView.ViewHolder{
            TextView itemTitle;
            RecyclerView recycler_view_list;
            Button btnMore;

            CategoryViewHolder(View itemView) {
                super(itemView);
                this.itemTitle = (TextView) itemView.findViewById(R.id.category_title);
                this.recycler_view_list = (RecyclerView) itemView.findViewById(R.id.category_recycler);
                this.btnMore= (Button) itemView.findViewById(R.id.btnMore);
            }
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        ViewPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.home_viewpager_row, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.home_viewpager_imageView);
            imageView.setImageResource(mResources[position]);

            container.addView(itemView);

            return itemView;
        }
    }

    public class ItemViewAdapter  extends RecyclerView.Adapter<ItemViewHolder>{
        private List<Item> itemList;
        private Context context;

        public ItemViewAdapter(Context context, List<Item> list) {
            itemList = new ArrayList<>();
            itemList = list;
            this.context = context;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_row, null);
            return new ItemViewHolder(layoutView);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            final Item item = itemList.get(position);
            holder.setImage(context,item.getImage().get(0));
            holder.setNama(item.getName());
            holder.setHarga(item.getPrice());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: click per item menuju detail
//                    Intent inten = new Intent(HomeActivity.this,DetailItemActivity.class);
//                    inten.putExtra("iditem",item.getId());
//
//                    startActivity(inten);
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }


    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView item_nama;
        TextView item_price;
        ImageView item_image;
        View mView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            item_nama = (TextView)mView.findViewById(R.id.item_nama);
            item_price = (TextView)mView.findViewById(R.id.item_price);
            item_image =(ImageView) mView.findViewById(R.id.item_image);
            //add event click to item
            //TODO: going to detail item
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        void setNama(String nama){
            item_nama.setText(nama);
        }

        void setHarga(String harga){
            item_price.setText(harga);
        }

        void setImage(final Context ctx, final String image) {
            Glide.with(ctx)
                    .load(image)
                    .override(100, 100)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(item_image);
        }
    }


}
