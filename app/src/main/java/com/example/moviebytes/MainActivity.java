package com.example.moviebytes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.moviebytes.crud.MovieActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private LoginPreference logPref;
    private boolean isLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logPref = LoginPreference.getInstance(this);
        Toast.makeText(MainActivity.this, logPref.getString("email", "Not set"), Toast.LENGTH_LONG).show();

//      Set the toolbar for this activity only
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//      Set the drawer and create a HAMBURGER actionbar drawer to toggle for closing and opening it.
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//      Setup the navigation menu to listen on item click inside the drawer
        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this::onMenuItemSelected);

//       set initial fragment to be loaded on the activity
//        and saveinstances for incase of the change in orientation
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new MovieFragment()).commit();
            nav_view.setCheckedItem(R.id.movie);
        }
    }

//  respond to the item selected inside the drawer,
//  just a simple switch statement to get the id of selected menu item
    private boolean onMenuItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.movie:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new MovieFragment()).commit();
                break;
            case R.id.actor:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new ActorFragment()).commit();
                break;
            case R.id.crudMovie:
                Intent intent = new Intent(getApplicationContext(), MovieActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                logout();
                break;
        }
//      close drawer after select
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
//      toggle state of drawer if open or not
//       the GravityCompat.START means only the position of drawer start means left
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    dialog for logout confirmation this will prevent unintended clicked of logout
    private void logout() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Logout")
                .setMessage(R.string.logout)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logPref.clearPref();
                        Intent login = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(login);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}








