package com.mul_alexautoprogramm.bulletinboardjavaversion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mul_alexautoprogramm.bulletinboardjavaversion.adapters.ImageAdapterPage;
import com.mul_alexautoprogramm.bulletinboardjavaversion.screens.ChooseImagesActivity;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.ImagesManager;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.MyConstance;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.OnBitmapLoaded;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditActivity extends AppCompatActivity implements OnBitmapLoaded {
    private final int MAX_UPLOAD_IMAGE_SIZE = 1920;
    private StorageReference myStorageRef;
    private String[] uploadUri = new String[3];
    private String[] uploadNewUri = new String[3];
    private Spinner spinner;
    private DatabaseReference databaseReference;
    private FirebaseAuth myAut;
    private EditText edTitle, edPrice,edTelNumb, edDescription;
    private Boolean edit_state = false;
    private String temp_category = "";
    private String temp_uid = "";
    private String temp_time = "";
    private String temp_key = "";

    private String temp_total_views = "";
    private Boolean is_image_update = false;
    private int loadImageCounter = 0;
    private ProgressDialog progressDialog;
    private List<String> imagesUris;
    private ImageAdapterPage imageAdapterPage;
    private TextView tvImagesCounter;
    private ViewPager viewPager;
    private List<Bitmap> bitmapArrayList;
    private ImagesManager imagesManager;
    private boolean isImagesLoaded = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_activity_layout);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void init() {

        imagesManager = new ImagesManager(this,this);
        tvImagesCounter = findViewById(R.id.tvImagesCounter);

        imagesUris = new ArrayList<>();
        bitmapArrayList = new ArrayList<>();
        viewPager = findViewById(R.id.view_pager);
        imageAdapterPage = new ImageAdapterPage(this);
        viewPager.setAdapter(imageAdapterPage);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String dataText = position + 1 + "/" + imagesUris.size();
                tvImagesCounter.setText(dataText);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        uploadUri[0] = "empty";
        uploadUri[1] = "empty";
        uploadUri[2] = "empty";

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.progress_bar));


        myStorageRef = FirebaseStorage.getInstance().getReference("Images");
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.category_spinner, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        edTitle = findViewById(R.id.edTitleAd);
        edPrice = findViewById(R.id.edPrice);
        edTelNumb = findViewById(R.id.edTelephoneNumber);
        edDescription = findViewById(R.id.edDescription);
        getMyIntent();

    }

    private void getMyIntent() {

        if (getIntent() != null) {

            Intent i = getIntent();
            edit_state = i.getBooleanExtra(MyConstance.EDIT_STATE, false);
            if (edit_state) setDataAds(i);


        }

    }

    private void setDataAds(Intent i) {

        //Picasso.get().load(i.getStringExtra(MyConstance.IMAGE_ID)).into(imItem); ????????????????????????
        edTelNumb.setText(i.getStringExtra(MyConstance.TEL_NUMB));
        edTitle.setText(i.getStringExtra(MyConstance.TITLE));
        edPrice.setText(i.getStringExtra(MyConstance.PRICE));
        edDescription.setText(i.getStringExtra(MyConstance.DESC));
        temp_category = i.getStringExtra(MyConstance.CATEGORY);
        temp_uid = i.getStringExtra(MyConstance.UID);
        temp_time = i.getStringExtra(MyConstance.TIME);
        temp_key = i.getStringExtra(MyConstance.KEY);
        temp_total_views = i.getStringExtra(MyConstance.TOTAL_VIEWS);

        uploadUri[0] = i.getStringExtra(MyConstance.IMAGE_ID);
        uploadUri[1] = i.getStringExtra(MyConstance.IMAGE_ID_2);
        uploadUri[2] = i.getStringExtra(MyConstance.IMAGE_ID_3);


        for (String s : uploadUri) {

            if (!s.equals("empty")) imagesUris.add(s);

        }

        imageAdapterPage.updateImages(imagesUris);

        if (imagesUris.size() > 0) {
            String dataText = 1 + "/" + imagesUris.size();
            tvImagesCounter.setText(dataText);
        } else {
            imagesUris.size();
            String dataText = 0 + "/" + imagesUris.size();
            tvImagesCounter.setText(dataText);
        }

    }


    private void upLoadImage() {

        if (loadImageCounter < uploadUri.length) {

            if (!uploadUri[loadImageCounter].equals("empty")) {

                Bitmap bitmap = bitmapArrayList.get(loadImageCounter);
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                assert bitmap != null;

                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                byte[] byteArray = out.toByteArray();

                final StorageReference myReference = myStorageRef.child(System.currentTimeMillis() + "_image");
                UploadTask uploadTask = myReference.putBytes(byteArray);
                Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return myReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.getResult() == null) return;

                        uploadUri[loadImageCounter] = task.getResult().toString();

                        loadImageCounter++;

                        if (loadImageCounter < uploadUri.length) {

                            upLoadImage();


                        } else {

                            savePost();
                            Toast.makeText(EditActivity.this, "Upload Done", Toast.LENGTH_SHORT).show();
                            finish();

                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }

                });
            } else {

                loadImageCounter++;
                upLoadImage();


            }
        } else {

            savePost();
            finish();

        }

    }

    private void upLoadUpdateImage() {

        Bitmap bitmap = null;

        if (loadImageCounter < uploadUri.length) {
            //1 if link old_position == link new_position
            if (uploadUri[loadImageCounter].equals(uploadNewUri[loadImageCounter])) {

                loadImageCounter++;
                upLoadUpdateImage();
                //2 if link old_position != link new_position && link in new_position not empty
            } else if (!uploadUri[loadImageCounter].equals(uploadNewUri[loadImageCounter]) && !uploadNewUri[loadImageCounter].equals("empty")) {

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uploadNewUri[loadImageCounter]));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }//3 - if in old_array not key "empty" but  in new_Array on the same position equals key "empty", it means delete old link and old images in old_array
            else if (!uploadUri[loadImageCounter].equals("empty") && uploadNewUri[loadImageCounter].equals("empty")) {

                StorageReference myReference = FirebaseStorage.getInstance().getReferenceFromUrl(uploadUri[loadImageCounter]);
                myReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        uploadUri[loadImageCounter] = "empty";
                        loadImageCounter++;
                        if (loadImageCounter < uploadUri.length) {

                            upLoadUpdateImage();

                        } else {

                            updatePost();

                        }
                    }
                });

            }

            if (bitmap == null) return;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            byte[] byteArray = out.toByteArray();
            final StorageReference myReference;

            if (!uploadUri[loadImageCounter].equals("empty")) {
                // 2-A if link in old_position != empty - then we overwrite old_link to new_link
                myReference = FirebaseStorage.getInstance().getReferenceFromUrl(uploadUri[loadImageCounter]);

            } else {
                // 2-B if link in old_position == empty - then we overwrite new Image[old_position] in FireBaseStorage
                myReference = myStorageRef.child(System.currentTimeMillis() + "_image");

            }


            //create image start
            UploadTask uploadTask = myReference.putBytes(byteArray);
            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return myReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    uploadUri[loadImageCounter] = task.getResult().toString();
                    loadImageCounter++;
                    if (loadImageCounter < uploadUri.length) {

                        upLoadUpdateImage();

                    } else {

                        updatePost();


                    }


                }

            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {

                }

            });
        } else {

            updatePost();

        }

    }

    //OnClick Save button in Edit Act
    public void onClickSavePost(View view) {
        if (isImagesLoaded) {
            progressDialog.show();
            if (!edit_state) {

                upLoadImage();


            } else {

                if (is_image_update) {

                    upLoadUpdateImage();


                } else {

                    updatePost();


                }
            }
        } else {
            Toast.makeText(this, "Please wait...images loading..", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 15 && data != null) {

            if (resultCode == RESULT_OK) {
                //We got a picture and show it in our ImageView

                is_image_update = true;
                imagesUris.clear();

                String[] tempUriArray = getUrisFromChoose(data);
                isImagesLoaded = false;
                imagesManager.resizeMultiLargeImages(Arrays.asList(tempUriArray));

                for (String s : tempUriArray) {

                    if (!s.equals("empty")) imagesUris.add(s);

                }
                imageAdapterPage.updateImages(imagesUris);
                String dataText;
                if (imagesUris.size() > 0) {

                    dataText = viewPager.getCurrentItem() + 1 + "/" + imagesUris.size();

                } else {

                    dataText = 0 + "/" + imagesUris.size();
                }

                tvImagesCounter.setText(dataText);

            }

        }

    }

    private String[] getUrisFromChoose(Intent data) {

        if (edit_state) {

            uploadNewUri[0] = data.getStringExtra("uri_main");
            uploadNewUri[1] = data.getStringExtra("uri_2");
            uploadNewUri[2] = data.getStringExtra("uri_3");

            return uploadNewUri;

        } else {

            uploadUri[0] = data.getStringExtra("uri_main");
            uploadUri[1] = data.getStringExtra("uri_2");
            uploadUri[2] = data.getStringExtra("uri_3");

            return uploadUri;

        }


    }


    //onClickImage in EditAct
    public void onClickImageViewEditAct(View view) {

        Intent i = new Intent(EditActivity.this, ChooseImagesActivity.class);

        i.putExtra(MyConstance.IMAGE_ID, uploadUri[0]);
        i.putExtra(MyConstance.IMAGE_ID_2, uploadUri[1]);
        i.putExtra(MyConstance.IMAGE_ID_3, uploadUri[2]);

        startActivityForResult(i, 15);

    }


    private void updatePost() {

        databaseReference = FirebaseDatabase.getInstance().getReference(temp_category);
        NewPost post = new NewPost();

        post.setImId(uploadUri[0]);
        post.setImId2(uploadUri[1]);
        post.setImId3(uploadUri[2]);
        post.setTitle(edTitle.getText().toString());
        post.setPrice(edPrice.getText().toString());
        post.setTel_numb(edTelNumb.getText().toString());
        post.setDesc(edDescription.getText().toString());
        post.setKey(temp_key);
        post.setTime(temp_time);
        post.setUid(temp_uid);
        post.setCategory(temp_category);
        post.setTotalViews(temp_total_views);
        databaseReference.child(temp_key).child("Ads").setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EditActivity.this, "Upload Done", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    private void savePost() {

        databaseReference = FirebaseDatabase.getInstance().getReference(spinner.getSelectedItem().toString());
        myAut = FirebaseAuth.getInstance();
        if (myAut.getUid() != null) {
            String key = databaseReference.push().getKey();
            NewPost post = new NewPost();

            post.setImId(uploadUri[0]);
            post.setImId2(uploadUri[1]);
            post.setImId3(uploadUri[2]);
            post.setTitle(edTitle.getText().toString());
            post.setPrice(edPrice.getText().toString());
            post.setTel_numb(edTelNumb.getText().toString());
            post.setDesc(edDescription.getText().toString());
            post.setKey(key);
            post.setTime(String.valueOf(System.currentTimeMillis()));
            post.setUid(myAut.getUid());
            post.setCategory(spinner.getSelectedItem().toString());
            post.setTotalViews("0");
            if (key != null) {
                databaseReference.child(key).child("Ads").setValue(post);
            }
            Intent i = new Intent();
            i.putExtra("cat", spinner.getSelectedItem().toString());
            setResult(RESULT_OK, i);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    public void onBitmapLoaded(final List<Bitmap> bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bitmapArrayList.clear();
                bitmapArrayList.addAll(bitmap);
                isImagesLoaded = true;
            }
        });


    }
}
