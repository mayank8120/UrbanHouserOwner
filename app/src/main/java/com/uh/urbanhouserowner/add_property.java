package com.uh.urbanhouserowner;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;

public class add_property extends AppCompatActivity {


    private static final int CHOOSE_IMAGE = 4;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private static final String CHANNEL_ID = "channel_id01";
    private static final int NOTIFICATION_ID = 1;

    Spinner spinnerDropDownViewcity, spinnerDropDownViewlocality;
    String[] spinnerValueHoldValue = {"Indore", "city unavailable"};
    String[] spinnerValueHoldValuelocality = {"Bhawarkuan", "rajiv gandhi"};

    RadioGroup radioGroup, radioGroupfurnish;
    RadioButton radioButton, radioButtonfurnish;


    String[] cameraPermission;
    String[] storagePermission;

    Uri img1uri, img2uri, img3uri = null;
    ProgressDialog pd;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference userDbref;

    EditText username, phonenumber, city, addressss, pincod, propprice;
    TextView datepicker;
    DatePickerDialog.OnDateSetListener dateSetListener;
    ImageView img1, img2, img3;
    Button submit;

    String email, uid, dp, date, days, months, years,phonenum;

    String editname, editphnone, editaddress, editdate, editpincode, editprice, editimg;

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkUserStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_property);

        username = findViewById(R.id.user_Name);
        phonenumber = findViewById(R.id.phone_Number);
        //city=findViewById(R.id.city);
        addressss = findViewById(R.id.Add_ress);
        pincod = findViewById(R.id.pin_code);
        propprice = findViewById(R.id.price);
        submit = findViewById(R.id.submit_button);
        datepicker = findViewById(R.id.date);
        img1 = (ImageView) findViewById(R.id.prop_pic1);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroupfurnish = findViewById(R.id.radioGroupgfurnished);

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                Toast.makeText(add_property.this, "The choosen date " + cal, Toast.LENGTH_SHORT).show();

                DatePickerDialog dialog = new DatePickerDialog(add_property.this, dateSetListener, year, month, day);
                /*dialog.getWindow();*/
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                date = day + "/" + month + "/" + year;
                days = Integer.toString(day);
                months = Integer.toString(month);
                years = Integer.toString(year);
                datepicker.setText(date);
                //Toast.makeText(add_property.this, "The choosen date " + date, Toast.LENGTH_SHORT).show();


            }
        };

        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add your property");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        pd = new ProgressDialog(this);


        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        Intent intent = getIntent();
        final String isUpdatedkey = "" + intent.getStringExtra("key");
        final String editpostid = "" + intent.getStringExtra("editPostId");

        if (isUpdatedkey.equals("editPost")) {
            //actionBar.setTitle("Update Post");
            submit.setText("Update");
            loadPostData(editpostid);
        } else {
            //actionBar.setTitle("Add new Property");
            submit.setText("Upload property");
        }


        userDbref = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbref.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    email = "" + ds.child("email").getValue();
                    uid = "" + ds.child("uid").getValue();
                    dp = "" + ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        spinnerDropDownViewcity = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(add_property.this, android.R.layout.simple_list_item_1, spinnerValueHoldValue);
        spinnerDropDownViewcity.setAdapter(adapter);

        spinnerDropDownViewcity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(add_property.this, spinnerDropDownViewcity.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                //String spinnercityy=spinnerDropDownViewcity.getSelectedItem().toString().trim();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        spinnerDropDownViewlocality = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> adapterlocal = new ArrayAdapter<>(add_property.this, android.R.layout.simple_list_item_1, spinnerValueHoldValuelocality);
        spinnerDropDownViewlocality.setAdapter(adapterlocal);

        spinnerDropDownViewlocality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(add_property.this, spinnerDropDownViewlocality.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                //String spinnercityy=spinnerDropDownViewlocality.getSelectedItem().toString().trim();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(add_property.this, "imsgepickdialog", Toast.LENGTH_SHORT).show();

                showimagepickdialog();


            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int radioId = radioGroup.getCheckedRadioButtonId();
                int radioidfurnish = radioGroupfurnish.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                radioButtonfurnish = findViewById(radioidfurnish);

                String name = username.getText().toString().trim();
                String phone = phonenumber.getText().toString().trim();
                //String cityname=city.getText().toString().trim();
                String address = addressss.getText().toString().trim();
                String pincode = pincod.getText().toString().trim();
                String proppricee = propprice.getText().toString().trim();
                String spinnercity = spinnerDropDownViewcity.getSelectedItem().toString().trim();
                String bhkconfig = radioButton.getText().toString();
                String furnish = radioButtonfurnish.getText().toString();
                String spinnerlocality = spinnerDropDownViewlocality.getSelectedItem().toString().trim();


                Toast.makeText(add_property.this, "BHK config  " + radioButton.getText(), Toast.LENGTH_SHORT).show();


                //String date=datepicker.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(add_property.this, "Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(add_property.this, "Enter Phone number", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(spinnercity)) {
                    Toast.makeText(add_property.this, "select city", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(spinnerlocality)) {
                    Toast.makeText(add_property.this, "select locality", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(add_property.this, "Enter address", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(pincode)) {
                    Toast.makeText(add_property.this, "Enter area pin code", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(date)) {
                    Toast.makeText(add_property.this, "Select date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(proppricee)) {
                    Toast.makeText(add_property.this, "Enter price", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isUpdatedkey.equals("editPost")) {
                    beginupdate(name, phone, address, pincode, date, proppricee, editpostid);
                } else {
                    uploadData(name, phone, address, pincode, spinnercity, proppricee, date, spinnerlocality, bhkconfig, days, months, years, furnish);
                }

                /*if (img1uri == null) {
                    uploadData(name, phone, address, pincode, spinnercity, proppricee, date, "noImage");
                } else {
                    uploadData(name, phone, address, pincode, spinnercity, proppricee, date, String.valueOf(img1uri));
                }*/
            }
        });

    }

    private void beginupdate(String name, String phone, String address, String pincode, String date, String proppricee, String editpostid) {
        pd.setMessage("Updating Post.........");
        pd.show();

        if (!editimg.equals("noImage")) {
            updatewaswithimage(name, phone, address, pincode, date, proppricee, editpostid);
        } else if (img1.getDrawable() != null) {
            updatewithnowimage(name, phone, address, pincode, date, proppricee, editpostid);
        } else {
            updatewithoutimage(name, phone, address, pincode, date, proppricee, editpostid);
        }

    }

    private void updatewithoutimage(String name, String phone, String address, String pincode, String date, String proppricee, String editpostid) {


        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("uname", name);
        hashMap.put("uemail", email);
        hashMap.put("udp", dp);
        hashMap.put("pid", timestamp);
        hashMap.put("ptime", timestamp);
        hashMap.put("phone", phone);
        hashMap.put("address", address);
        hashMap.put("pincode", pincode);
        hashMap.put("pimage", "noImage");
        hashMap.put("price", proppricee);
        hashMap.put("date", date);
                            /*HashMap<Object, String> hashMap = new HashMap<>();

                            hashMap.put("uid", uid);
                            hashMap.put("uname", name);
                            hashMap.put("uemail", email);
                            hashMap.put("udp", dp);
                            hashMap.put("pid", timestamp);
                            hashMap.put("ptime", timestamp);
                            hashMap.put("phone", phone);
                            hashMap.put("address", address);
                            hashMap.put("pincode", pincode);
                            hashMap.put("pimage", downloadurl);
                            hashMap.put("price", proppricee);
                            hashMap.put("date", date);*/

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.child(editpostid)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(add_property.this, "Updated........", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(add_property.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void updatewithnowimage(final String name, final String phone, final String address, final String pincode, final String date, final String proppricee, final String editpostid) {

        final String timestamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_" + timestamp;

        Bitmap bitmap = ((BitmapDrawable) img1.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uriTask.isSuccessful()) ;
                        String downloadurl = uriTask.getResult().toString();
                        if (uriTask.isSuccessful()) {

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", uid);
                            hashMap.put("uname", name);
                            hashMap.put("uemail", email);
                            hashMap.put("udp", dp);
                            hashMap.put("pid", timestamp);
                            hashMap.put("ptime", timestamp);
                            hashMap.put("phone", phone);
                            hashMap.put("address", address);
                            hashMap.put("pincode", pincode);
                            hashMap.put("pimage", downloadurl);
                            hashMap.put("price", proppricee);
                            hashMap.put("date", date);
                            /*HashMap<Object, String> hashMap = new HashMap<>();

                            hashMap.put("uid", uid);
                            hashMap.put("uname", name);
                            hashMap.put("uemail", email);
                            hashMap.put("udp", dp);
                            hashMap.put("pid", timestamp);
                            hashMap.put("ptime", timestamp);
                            hashMap.put("phone", phone);
                            hashMap.put("address", address);
                            hashMap.put("pincode", pincode);
                            hashMap.put("pimage", downloadurl);
                            hashMap.put("price", proppricee);
                            hashMap.put("date", date);*/

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                            ref.child(editpostid)
                                    .updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(add_property.this, "Updated........", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(add_property.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(add_property.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


    private void updatewaswithimage(final String name, final String phone, final String address, final String pincode, final String date, final String proppricee, final String editpostid) {

        StorageReference mpictureref = FirebaseStorage.getInstance().getReferenceFromUrl(editimg);
        mpictureref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        final String timestamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "Posts/" + "post_" + timestamp;

                        Bitmap bitmap = ((BitmapDrawable) img1.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();

                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                                        while (!uriTask.isSuccessful()) ;
                                        String downloadurl = uriTask.getResult().toString();
                                        if (uriTask.isSuccessful()) {

                                            HashMap<String, Object> hashMap = new HashMap<>();

                                            hashMap.put("uid", uid);
                                            hashMap.put("uname", name);
                                            hashMap.put("uemail", email);
                                            hashMap.put("udp", dp);
                                            hashMap.put("pid", timestamp);
                                            hashMap.put("ptime", timestamp);
                                            hashMap.put("phone", phone);
                                            hashMap.put("address", address);
                                            hashMap.put("pincode", pincode);
                                            hashMap.put("pimage", downloadurl);
                                            hashMap.put("price", proppricee);
                                            hashMap.put("date", date);

                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                            ref.child(editpostid)
                                                    .updateChildren(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            pd.dismiss();
                                                            Toast.makeText(add_property.this, "Updated........", Toast.LENGTH_SHORT).show();

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            pd.dismiss();
                                                            Toast.makeText(add_property.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                        }
                                                    });


                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(add_property.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(add_property.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void loadPostData(String editpostid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        Query fquery = reference.orderByChild("pId").equalTo(editpostid);
        fquery.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    editname = "" + ds.child("uname").getValue();
                    editaddress = "" + ds.child("address").getValue();
                    editdate = "" + ds.child("date").getValue();
                    editimg = "" + ds.child("pimage").getValue();
                    editpincode = "" + ds.child("pincode").getValue();
                    editprice = "" + ds.child("price").getValue();
                    editphnone = "" + ds.child("phone").getValue();

                    username.setText(editname);
                    phonenumber.setText(editphnone);
                    addressss.setText(editaddress);
                    pincod.setText(editpincode);
                    propprice.setText(editprice);
                    datepicker.setText(editdate);

                    if (!editimg.equals("noImage")) {
                        try {
                            Picasso.get().load(editimg).into(img1);
                        } catch (Exception e) {

                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));

    }

    private void showimagepickdialog() {

        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose image from");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                if (which == 0) {
                    if (!checkcamerapermissions()) {
                        requestcamerapermission();
                    } else {
                        pickfromcamera();
                    }

                }
                if (which == 1) {

                    if (!checkStoragepermissions()) {
                        requeststoragepermission();
                    } else {
                        pickfromcGallery();
                    }

                }
            }
        });
        builder.create().show();
    }

    private void uploadData(final String name, final String phone, final String address, final String pincode,
                            final String spinnercity, final String propprice, final String date, final String spinnerlocality,
                            final String bhkconfig, final String days, final String months, final String years, final String furnish) {

        pd.setMessage("Publishing your property ......");
        pd.show();

        final String timestamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/" + "post" + timestamp;

        if (/*!Uri.equals("noImage")*/img1.getDrawable() != null) {

            Bitmap bitmap = ((BitmapDrawable) img1.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isSuccessful()) ;

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {
                                HashMap<Object, String> hashMap = new HashMap<>();

                                hashMap.put("uid", uid);
                                hashMap.put("uname", name);
                                //hashMap.put("uemail", email);
                                hashMap.put("udp", dp);
                                hashMap.put("phonenumber",phonenum);
                                hashMap.put("pid", timestamp);
                                hashMap.put("ptime", timestamp);
                                hashMap.put("phone", phone);
                                hashMap.put("address", address);
                                hashMap.put("pincode", pincode);
                                hashMap.put("pimage", downloadUri);
                                hashMap.put("city", spinnercity);
                                hashMap.put("price", propprice);
                                hashMap.put("date", date);
                                hashMap.put("locality", spinnerlocality);
                                hashMap.put("bhkconfig", bhkconfig);
                                hashMap.put("day", days);
                                hashMap.put("month", months);
                                hashMap.put("year", years);
                                hashMap.put("furnishstatus", furnish);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                ref.child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                pd.dismiss();
                                                Toast.makeText(add_property.this, "Property submitted", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(add_property.this, main_activity.class);
                                                startActivity(intent);

                                                username.setText("");
                                                phonenumber.setText("");
                                                addressss.setText("");
                                                pincod.setText("");
                                                img1.setImageURI(null);
                                                img1uri = null;

                                                shownotification();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                pd.dismiss();
                                                Toast.makeText(add_property.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            pd.dismiss();
                            Toast.makeText(add_property.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {

            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("uid", uid);
            hashMap.put("uname", name);
            hashMap.put("uemail", email);
            hashMap.put("udp", dp);
            hashMap.put("pid", timestamp);
            hashMap.put("ptime", timestamp);
            hashMap.put("phone", phone);
            hashMap.put("address", address);
            hashMap.put("pincode", pincode);
            hashMap.put("pimage", "noImage");
            hashMap.put("city", spinnercity);
            hashMap.put("price", propprice);

            hashMap.put("locality", spinnerlocality);
            hashMap.put("date", date);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            pd.dismiss();
                            Toast.makeText(add_property.this, "Property submitted without image", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(add_property.this, main_activity.class);
                            startActivity(intent);
                            username.setText("");
                            phonenumber.setText("");
                            addressss.setText("");
                            pincod.setText("");
                            img1.setImageURI(null);
                            img1uri = null;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            pd.dismiss();
                            Toast.makeText(add_property.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void shownotification() {

        createnotificationchannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_announcement_black);
        builder.setContentTitle("Your Property is submitted successfully.");
        builder.setContentText("your properrty is submitted successfully and you willl receive a call or message from roommet team within 24hrs regarding property verification.");

        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("your properrty is submitted successfully and you willl receive a call or message from roommet team within 24hrs regarding property verification."));

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    private void createnotificationchannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "my notificxation";
            String description = "my notification description";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }


    }

    private void showImagePickDialog() {

        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose image from");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                if (which == 0) {
                    if (!checkcamerapermissions()) {
                        requestcamerapermission();
                    } else {
                        pickfromcamera();
                    }

                }
                if (which == 1) {
                    Toast.makeText(add_property.this, "gallery clicked", Toast.LENGTH_SHORT).show();

                    if (!checkStoragepermissions()) {
                        requeststoragepermission();
                    } else {
                        pickfromcGallery();
                    }

                }

            }
        });
    }

    private void pickfromcGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickfromcamera() {

        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp pic");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp description");
        img1uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, img1uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    private void checkUserStatus() {
        FirebaseUser currentuser = firebaseAuth.getCurrentUser();

        if (currentuser != null) {

            phonenum = currentuser.getPhoneNumber();
            uid = currentuser.getUid();


        } else {
            startActivity(new Intent(this, Homeclass.class));
            finish();
        }
    }


    private boolean checkStoragepermissions() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requeststoragepermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
        //requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkcamerapermissions() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestcamerapermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
        //requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case CAMERA_REQUEST_CODE: {

                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        pickfromcamera();
                    } else {
                        Toast.makeText(this, "please enable permissions", Toast.LENGTH_SHORT).show();
                    }

                } else {

                }
            }
            break;

            case STORAGE_REQUEST_CODE: {

                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        pickfromcGallery();
                    } else {
                        Toast.makeText(this, "enable permissions", Toast.LENGTH_SHORT).show();
                    }

                } else {

                }

            }
            break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                img1uri = data.getData();
                img1.setImageURI(img1uri);


            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {

                img1.setImageURI(img1uri);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        int radioidfurnish = radioGroupfurnish.getCheckedRadioButtonId();


        radioButton = findViewById(radioId);
        radioButtonfurnish = findViewById(radioidfurnish);
        /*Toast.makeText(this, "BHK config  " + radioButton.getText(),
                Toast.LENGTH_SHORT).show();*/

    }

}
