package com.example.lukaskris.houseofdesign;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lukaskris.houseofdesign.Account.ContainerLoginRegisterActivity;
import com.example.lukaskris.houseofdesign.Account.LoginFragment;
import com.example.lukaskris.houseofdesign.Account.ProfileFragment;
import com.example.lukaskris.houseofdesign.Shop.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View header = navigationView.getHeaderView(0);
        header.findViewById(R.id.nav_header_Login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ContainerLoginRegisterActivity.class));
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            TextView mEmail = (TextView) header.findViewById(R.id.nav_header_email);
            CircleImageView mPicture = (CircleImageView) header.findViewById(R.id.nav_header_picture);
            TextView mName = (TextView) header.findViewById(R.id.nav_header_name);

            header.findViewById(R.id.nav_header_Login).setVisibility(View.GONE);
            mEmail.setVisibility(View.VISIBLE);
            mPicture.setVisibility(View.VISIBLE);
            mName.setVisibility(View.VISIBLE);

//            String name = user.getDisplayName();
            String email = user.getEmail();
            String name = user.getDisplayName();
            Uri photo = user.getPhotoUrl();
//            String photo = "default";
            mEmail.setText(email);
            mName.setText(name);

            Glide.with(this)
                    .load(photo)
                    .override(100, 100)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mPicture);
            selectedFragment(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home_action_cart) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        selectedFragment(id);
        return true;
    }

    private void selectedFragment(int id){
        Fragment fragment = null;
        if(id == R.id.nav_home) {
            fragment = new HomeFragment();
        }else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
        } else if (id == R.id.nav_cart) {

        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_favorite) {

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

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

}
