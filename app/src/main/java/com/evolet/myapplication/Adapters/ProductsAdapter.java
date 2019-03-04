package com.evolet.myapplication.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.*;

import android.support.annotation.NonNull;
import android.support.v4.app.*;
import android.view.LayoutInflater;
import android.view.*;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.evolet.myapplication.Items.ProductItem;
import com.evolet.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.*;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class ProductsAdapter extends ArrayAdapter<ProductItem> {

    Context context;
    ArrayList<ProductItem> productItems;
    FirebaseFirestore db;

    public ProductsAdapter(Context context, ArrayList<ProductItem> productItems){
        super(context, 0, productItems);
        this.context = context;
        this.productItems = productItems;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        db=FirebaseFirestore.getInstance();

        View rootView = LayoutInflater.from(context).inflate(R.layout.products_items,null);
        ImageView prodImage = (ImageView)rootView.findViewById(R.id.prodImage);
        final TextView prodName = (TextView)rootView.findViewById(R.id.prodName);
        TextView prodPrice = (TextView)rootView.findViewById(R.id.prodPricePerUnit);
        TextView prodUnit = (TextView)rootView.findViewById(R.id.prodUnit);
        final ImageView prodDel=(ImageView)rootView.findViewById(R.id.deleteProduct);
        ImageView prodEdit=(ImageView)rootView.findViewById(R.id.editProduct);

        String url=productItems.get(position).getProdImage();
        Glide
                .with(parent)
                .load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(prodImage);


        prodName.setText(productItems.get(position).getProdName());
        prodPrice.setText(productItems.get(position).getProdPrice());
        prodUnit.setText(productItems.get(position).getUnit());
        //Action when user clicks on button
        prodDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setMessage("Do you want to delete this item?");
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        String name=productItems.get(position).getProdName();
                                        String category=productItems.get(position).getProdCategory();
                                        db.document("/"+category+"/"+name).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getContext(),"Item deleted!",Toast.LENGTH_LONG).show();
                                                notifyDataSetChanged();

                                            }
                                        });

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
        });

        prodEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Edit is click" , Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }


}
