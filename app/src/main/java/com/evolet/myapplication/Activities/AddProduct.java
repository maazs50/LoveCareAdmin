package com.evolet.myapplication.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.evolet.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AddProduct extends AppCompatActivity {

    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/evolet";
    Button addImage;
    ImageView selectedImage;
    EditText prodName,prodPrice,prodUnit;
    Button submit;
    FirebaseFirestore mFirestore;
    ProgressDialog progressDialog;
    private Uri postImageuri=null;
    StorageReference mStorageRef;
    Thread progressThread;
    //dRef holds the id of the document and is used when updating the document for url
    String dRef="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        final Spinner productCategorySpinner = (Spinner)findViewById(R.id.prodCategory);
        String[] categories = new String[]{"Medicine","Grocery","Care"};
        prodName=findViewById(R.id.prodName);
        prodPrice=findViewById(R.id.prodPrice);
        prodUnit=findViewById(R.id.prodUnit);
        submit=findViewById(R.id.submit);


        mStorageRef=FirebaseStorage.getInstance().getReference();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddProduct.this,android.R.layout.simple_spinner_dropdown_item,categories);
        productCategorySpinner.setAdapter(adapter);
        mFirestore=FirebaseFirestore.getInstance();


        selectedImage = (ImageView)findViewById(R.id.selectedImage) ;

        addImage = (Button)findViewById(R.id.addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPictureDialog();
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //include a progress bar whenever network call is there

                progressDialog=new ProgressDialog(AddProduct.this);
                progressDialog.setTitle("Add a product");
                progressDialog.setMessage("Saving....");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                String name=prodName.getText().toString();
                String price=prodPrice.getText().toString();
                String unit=prodUnit.getText().toString();
                final String category=productCategorySpinner.getSelectedItem().toString();
                //Check there is no empty list
                if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(price)&&!TextUtils.isEmpty(unit)&&!TextUtils.isEmpty(category)){

                    final DocumentReference docRef=mFirestore.collection(category).document(name);
                    dRef=docRef.toString();
                    final Map<String, Object> data=new HashMap<>();
                    data.put("id", docRef);
                    data.put("name", name);
                    data.put("price", price);
                    data.put("unit",unit );
                    data.put("category",category);

                    data.put("time", FieldValue.serverTimestamp());
                    docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()&&postImageuri!=null){

                                final String randonName= UUID.randomUUID().toString();
                                //Saving the image
                                StorageReference filepath=mStorageRef.child(category).child("images").child(randonName+".jpg");
                                filepath.putFile(postImageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        final String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                        //Add the url to the existing document
                                        Map<String, Object> data1=new HashMap<>();
                                        data1.put("url",downloadUrl );
                                        docRef.update(data1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    prodName.setText("");
                                                    prodPrice.setText("");
                                                    prodUnit.setText("");
                                                    selectedImage.setImageDrawable(null);
                                                    Toast.makeText(getApplicationContext(),"Product Saved!" ,Toast.LENGTH_SHORT ).show();

                                                }
                                            }
                                        });
                                    }
                                });
                            }
                            else if (task.isSuccessful()){
                                prodName.setText("");
                                prodPrice.setText("");
                                prodUnit.setText("");
                                Toast.makeText(getApplicationContext(),"Product Saved!" ,Toast.LENGTH_SHORT ).show();
                            }
                        }
                    });
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Enter all values" ,Toast.LENGTH_SHORT ).show();

                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }


            }
        });


    }


    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {
        if (ContextCompat.checkSelfPermission(AddProduct.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddProduct.this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(AddProduct.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},GALLERY
                        );
            }
        }else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(galleryIntent, GALLERY);
        }
    }

    private void takePhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(AddProduct.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddProduct.this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(AddProduct.this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA);
            }
        }else{
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                postImageuri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), postImageuri);
                    String path = saveImage(bitmap);
                    selectedImage.setVisibility(View.VISIBLE);
                    selectedImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddProduct.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        }
        else if (requestCode == CAMERA) {

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            selectedImage.setVisibility(View.VISIBLE);
            selectedImage.setImageBitmap(thumbnail);
            String path = saveImage(thumbnail);
//            postImageuri=Uri.fromFile(new File(path));


        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

}
