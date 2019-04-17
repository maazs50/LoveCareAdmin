package com.evolet.myapplication.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.evolet.myapplication.Adapters.OrderDetailsAdapter;
import com.evolet.myapplication.Items.OrderItems;
import com.evolet.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetails extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    TextView orderIdTxt, orderAddressTxt, orderPhoneTxt, orderTotalTxt;
    ListView listView;
    ArrayList<OrderItems> productsList;
    OrderDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ActionBar actionBar = getSupportActionBar();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        productsList=new ArrayList<>();
        orderIdTxt = findViewById(R.id.order_details_id);
        orderAddressTxt = findViewById(R.id.order_address);
        orderPhoneTxt = findViewById(R.id.order_phone_no);
        orderTotalTxt = findViewById(R.id.order_details_total);
        listView = findViewById(R.id.ordered_list);
        adapter=new OrderDetailsAdapter(OrderDetails.this, productsList);
        listView.setAdapter(adapter);
        String user_id = mAuth.getUid();

        Intent data = getIntent();
        final String order_id = data.getExtras().getString("order_id");

        Query query = firebaseFirestore.collection("Orders").
                whereEqualTo("order_id", order_id);
        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    //Getting the data based on the above query and setting the text
                    String orderId = documentSnapshots.getDocumentChanges().get(0).getDocument().getString("order_id");
                    String phoneNo = documentSnapshots.getDocumentChanges().get(0).getDocument().getString("phone");
                    String address = documentSnapshots.getDocumentChanges().get(0).getDocument().getString("address");
                    String total = documentSnapshots.getDocumentChanges().get(0).getDocument().getString("total_price");
                    HashMap<String,Integer> products=(HashMap<String, Integer>)documentSnapshots.getDocumentChanges().get(0).getDocument().get("products");
                    for (String item:products.keySet()){
                        productsList.add(new OrderItems(item,String.valueOf(products.get(item)) ));
                    }
                    orderIdTxt.setText("Order Id : " + orderId);
                    orderPhoneTxt.setText("Contact No : " + phoneNo);
                    orderAddressTxt.setText("Address\n" + address);
                    orderTotalTxt.setText("Total : ₹" + total);

                }
            }
        });
        actionBar.setTitle("Order Id " + order_id);
    }
}