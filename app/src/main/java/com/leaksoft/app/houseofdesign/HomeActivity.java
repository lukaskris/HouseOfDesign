package com.leaksoft.app.houseofdesign;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.leaksoft.app.houseofdesign.account.ContainerLoginRegisterActivity;
import com.leaksoft.app.houseofdesign.account.EditProfileActivity;
import com.leaksoft.app.houseofdesign.account.ProfileFragment;
import com.leaksoft.app.houseofdesign.model.Customer;
import com.leaksoft.app.houseofdesign.orders.OrdersFragment;
import com.leaksoft.app.houseofdesign.shop.HomeFragment;
import com.leaksoft.app.houseofdesign.shop.SearchingActivity;
import com.leaksoft.app.houseofdesign.shop.ShoppingCartFragment;
import com.leaksoft.app.houseofdesign.shop.WishlistFragment;
import com.leaksoft.app.houseofdesign.util.PreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Fragment selectedFragment;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setElevation(0);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new SmoothActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View header = navigationView.getHeaderView(0);
        header.findViewById(R.id.nav_header_Login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(HomeActivity.this, ContainerLoginRegisterActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        selectedFragment(R.id.nav_home);
        navigationView.getMenu().getItem(0).setChecked(true);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        nav_Menu.findItem(R.id.nav_profile).setVisible(false);
        nav_Menu.findItem(R.id.nav_orders).setVisible(false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Customer customer = PreferencesUtil.getUser(this);
        if(customer != null && user!= null){
            TextView mEmail = (TextView) header.findViewById(R.id.nav_header_email);
            CircleImageView mPicture = (CircleImageView) header.findViewById(R.id.nav_header_picture);
            TextView mName = (TextView) header.findViewById(R.id.nav_header_name);

            header.findViewById(R.id.nav_header_Login).setVisibility(View.GONE);
            mEmail.setVisibility(View.VISIBLE);
            mPicture.setVisibility(View.VISIBLE);
            mName.setVisibility(View.VISIBLE);

            String email = customer.getEmail();
            String name = customer.getName();
            if(!user.isEmailVerified()){
                name += " (Unverified)";
                Snackbar.make(navigationView,R.string.error_invalid_verification,Snackbar.LENGTH_SHORT);
            }

            String photo = customer.getPicture();
            mEmail.setText(email);
            mName.setText(name);

            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            nav_Menu.findItem(R.id.nav_profile).setVisible(true);
            nav_Menu.findItem(R.id.nav_orders).setVisible(true);
            Glide.with(this)
                    .load(photo)
                    .override(100, 100)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(mPicture);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            FragmentManager fm = getSupportFragmentManager();
            HomeFragment homeFragment = HomeFragment.newInstance();

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (fm.getBackStackEntryCount() > 1) {
                fm.popBackStackImmediate();
            }else if(!selectedFragment.getClass().equals(homeFragment.getClass())) {
                fm.beginTransaction().replace(R.id.content_frame,homeFragment,"home").commit();
                selectedFragment = homeFragment;
                navigationView.getMenu().getItem(0).setChecked(true);
            }else if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this,"Press BACK again to exit the app",Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shoppingcart_menu, menu);
//        MenuItem item = menu.findItem(R.id.search);
//        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home_action_cart) {
            selectedFragment(R.id.home_action_cart);
            navigationView.getMenu().getItem(3).setChecked(true);
        }else if(id == R.id.search){
            startActivity(new Intent(HomeActivity.this, SearchingActivity.class));
        }else if(id == R.id.edit_profile){
            startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        selectedFragment(id);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK){
            selectedFragment(R.id.nav_orders);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void selectedFragment(int id){
        drawer.closeDrawers();
        Fragment fragment = null;
        String title = "";
        String tag = "";
        if(id == R.id.nav_home) {
            fragment = new HomeFragment();
            title = "Home";
            tag="home";
        }else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
            title="Profile";
            tag="profile";
        } else if (id == R.id.nav_cart || id == R.id.home_action_cart) {
            fragment = new ShoppingCartFragment();
            title="Shopping Cart";
            tag="cart";
        } else if (id == R.id.nav_orders) {
            fragment = OrdersFragment.newInstance();
            title="Orders";
            tag="orders";
        } else if (id == R.id.nav_favorite) {
            title="Favorites";
            tag="favorites";
            fragment = WishlistFragment.newInstance();
        }else if (id == R.id.nav_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmation Logout");
            builder.setMessage("Do you want logut?");
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    finish();
                    startActivity(getIntent());
                }
            });
            builder.show();
        }

        if(fragment != null && !fragment.isAdded()){
            selectedFragment = fragment;
            final Fragment finalFragment = fragment;

            final String finalTitle = title;
            final String finalTag = tag;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.content_frame, finalFragment, finalTag)
                            .commit();
                    setTitle(finalTitle);
                }
            }, 200);


        }
    }

    private class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

        private Runnable runnable;

        SmoothActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            invalidateOptionsMenu();
        }
        @Override
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            invalidateOptionsMenu();
        }
        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
                runnable.run();
                runnable = null;
            }
        }
//
//        public void runWhenIdle(Runnable runnable) {
//            this.runnable = runnable;
////        }
    }

}
