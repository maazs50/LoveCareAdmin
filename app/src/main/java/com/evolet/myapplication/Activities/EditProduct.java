package com.evolet.myapplication.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class EditProduct extends AppCompatActivity {
    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/evolet";
    ImageView selectedImage;
    EditText prodName,prodPrice,prodUnit,prodCat;
    Button submit,addImage;
    FirebaseFirestore mFirestore;
    private Uri postImageuri=null;
    StorageReference mStorageRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Edit Product");
        setContentView(R.layout.activity_edit_product);
        prodName=findViewById(R.id.prodName);
        prodPrice=findViewById(R.id.prodPrice);
        prodUnit=findViewById(R.id.prodUnit);
        submit=findViewById(R.id.submit);
        selectedImage = (ImageView)findViewById(R.id.selectedImage);
        prodCat=findViewById(R.id.prodCategory);
        mFirestore=FirebaseFirestore.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();
        /*****
         *Getting the values to edit
         */

        String name=getIntent().getExtras().getString("name");
        String price=getIntent().getExtras().getString("price");
        String unit=getIntent().getExtras().getString("unit");
        String cat=getIntent().getExtras().getString("cat");
        prodName.setText(name);
        prodPrice.setText(price);
        prodUnit.setText(unit);
        prodCat.setText(cat);

        addImage=findViewById(R.id.addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showPictureDialog();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name=prodName.getText().toString();
                final String price=prodPrice.getText().toString();
                final String unit=prodUnit.getText().toString();
                final String category=prodCat.getText().toString();
                //Check there is no empty list
                if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(price)&&!TextUtils.isEmpty(unit)&&!TextUtils.isEmpty(category)){
                    final ProgressDialog progresRing = ProgressDialog.show(EditProduct.this, "Saving a product", "saving...", true);
                    progresRing.setCancelable(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                    Map<String, Object> data=new HashMap<>();
                   final DocumentReference docRef=mFirestore.collection(category).document(name);
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

                                                    progresRing.dismiss();
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
                                progresRing.dismiss();
                                Toast.makeText(getApplicationContext(),"Product Saved!" ,Toast.LENGTH_SHORT ).show();
                            }
                        }
                    });

                            } catch (Exception e) {

                            }

                        }
                    }).start();

                }else{
                    Toast.makeText(getApplicationContext(),"Enter all values" ,Toast.LENGTH_SHORT ).show();

                }



            }
        });

    }
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery"};
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
        if (ContextCompat.checkSelfPermission(EditProduct.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProduct.this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(EditProduct.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},GALLERY
                );
            }
        }else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(galleryIntent, GALLERY);
        }
    }

    private void takePhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(EditProduct.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProduct.this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(EditProduct.this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA);
            }
        }else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                    Toast.makeText(EditProduct.this, "Failed!", Toast.LENGTH_SHORT).show();
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
