package com.evolet.myapplication.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.evolet.myapplication.Fragments.CareFragment;
import com.evolet.myapplication.Fragments.GroceryFragment;
import com.evolet.myapplication.Fragments.MedicinesFragment;
import com.evolet.myapplication.Fragments.OrdersFragment;
import com.evolet.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    FragmentManager fragmentManager = HomeActivity.this.getSupportFragmentManager();
    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    FirebaseAuth mAuth;
    FloatingActionButton addProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth=FirebaseAuth.getInstance();


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        MedicinesFragment medicinesFragment = new MedicinesFragment();
        fragmentTransaction.replace(R.id.container, medicinesFragment);
        fragmentTransaction.commit();

        addProduct = (FloatingActionButton)findViewById(R.id.addProduct);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addProductIntent = new Intent(HomeActivity.this, AddProduct.class);
                startActivity(addProductIntent);
            }
        });

//Select an option in the bottom navigation
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_medicines:
                        MedicinesFragment medicinesFragment = new MedicinesFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                        fragmentTransaction1.replace(R.id.container, medicinesFragment);
                        fragmentTransaction1.commit();
                        return true;

                    case R.id.navigation_grocery:
                        GroceryFragment groceryFragment = new GroceryFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                        fragmentTransaction2.replace(R.id.container, groceryFragment);
                        fragmentTransaction2.commit();
                        return true;

                    case R.id.navigation_care:
                        CareFragment careFragment = new CareFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction3 = fragmentManager.beginTransaction();
                        fragmentTransaction3.replace(R.id.container, careFragment);
                        fragmentTransaction3.commit();
                        return true;

                    case R.id.navigation_orders:
                        OrdersFragment ordersFragment = new OrdersFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction4 = fragmentManager.beginTransaction();
                        fragmentTransaction4.replace(R.id.container, ordersFragment);
                        fragmentTransaction4.commit();
                        return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser==null){
            Intent obj=new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(obj);
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.logout:
                // Save pet to database
                sendTologin();



                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendTologin() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        alertDialogBuilder.setMessage("Do you want to Logout?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mAuth.signOut();
                        Intent obj=new Intent(HomeActivity.this,LoginActivity.class);
                        startActivity(obj);
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
