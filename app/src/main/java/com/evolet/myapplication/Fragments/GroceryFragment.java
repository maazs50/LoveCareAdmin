package com.evolet.myapplication.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.evolet.myapplication.Adapters.ProductsAdapter;
import com.evolet.myapplication.Items.ProductItem;
import com.evolet.myapplication.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroceryFragment extends Fragment {

    public FirebaseFirestore firebaseFirestore;
    ArrayList<ProductItem> groceries;
    ProductsAdapter adapter;
    ListView groceriesListView;
    public GroceryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery, container, false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        groceries = new ArrayList<>();
        adapter = new ProductsAdapter(getContext(), groceries);
        groceriesListView = (ListView)view.findViewById(R.id.groceriesListView);
        groceriesListView.setAdapter(adapter);

        Query query=firebaseFirestore.collection("Grocery").orderBy("name", Query.Direction.DESCENDING);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String id=doc.getDocument().get("id").toString();
                            String name=doc.getDocument().getString("name");
                            String price=doc.getDocument().getString("price");
                            String unit=doc.getDocument().getString("unit");
                            String category=doc.getDocument().getString("category");
                            String url=doc.getDocument().getString("url");



                            ProductItem item = new ProductItem(id,name,price,unit,category,url);
                            groceries.add(item);


                            adapter.notifyDataSetChanged();

                        }
                    }

                }else{
                    Toast.makeText(getContext(), "No data",Toast.LENGTH_SHORT ).show();
                }
            }
        });




        // Inflate the layout for this fragment
        return view;
    }

}
