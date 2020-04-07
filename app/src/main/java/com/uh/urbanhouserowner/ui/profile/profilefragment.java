package com.uh.urbanhouserowner.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.uh.urbanhouserowner.R;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class
profilefragment extends Fragment {


    private static final int CHOOSE_IMAGE = 4;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    //EditText user_name, user_email, user_number;
    TextView business_type, user_name, user_email, user_number, veriftxt;
    ImageView profile_image;
    Uri uriprofilemage;
    ProgressBar progressbar;
    String profileImageUrl;
    Button savebutton;
    //Uri profilepic;
    FloatingActionButton fabedit;
    String[] cameraPermissions;
    String[] storagePermissions;
    Uri image_uri;
    String profilePhoto;
    FirebaseAuth auther;
    FirebaseUser currentuser;
    FirebaseDatabase profiledata;
    DatabaseReference ref;
    StorageReference storageRef;
    String storagepath = "users_profile_img/";
    ProgressDialog pd;
    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auther = FirebaseAuth.getInstance();
        currentuser = auther.getCurrentUser();
        profiledata = FirebaseDatabase.getInstance();
        ref = profiledata.getReference("Owner_Users");
        storageRef = getInstance().getReference();

        pd = new ProgressDialog(getActivity());

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        profile_image = view.findViewById(R.id.profileimage);
        user_name = view.findViewById(R.id.username);
        user_email = view.findViewById(R.id.email);
        user_number = view.findViewById(R.id.phonenumber);
        business_type = view.findViewById(R.id.businesstype);
        //progressbar = view.findViewById(R.id.progress_bar);
        //savebutton = view.findViewById(R.id.save_button);
        fabedit = view.findViewById(R.id.fab_edit);
        veriftxt = view.findViewById(R.id.verificationtext);

        /*if (currentuser.isEmailVerified()) {
            veriftxt.setText("verified");
        } else {
            veriftxt.setText("not verified(click to verify)");
            veriftxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "verification mail sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }*/

        final String mobile = currentuser.getPhoneNumber();
        //Toast.makeText(profile_act.this, "" + mobile, Toast.LENGTH_SHORT).show();

        Query query = ref.orderByChild("phonenumber").equalTo(mobile);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phonenumber").getValue();
                    String image = "" + ds.child("image").getValue();
                    String business = "" + ds.child("business").getValue();



                    user_name.setText(name);
                    user_email.setText(email);
                    user_number.setText(phone);
                    business_type.setText(business);

                    try {
                        Picasso.get().load(image).into(profile_image);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_person_black_24dp).into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*Query query = ref.orderByChild("email").equalTo(currentuser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {


                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone number").getValue();
                    String image = "" + ds.child("image").getValue();
                    String business = "" + ds.child("business").getValue();



                    try {
                        Picasso.get().load(image).into(profile_image);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_photo_camera_black_24dp).into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showImageChooser();


            }
        });


        fabedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditprofiledialog();

            }
        });

        return view;

    }

    private void showEditprofiledialog() {


        String[] options = {"edit profilefragment pic", "edit name", "edit email", "edit phone number", "edit business type"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("CHOOSE ACTION");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInter, int which) {

                if (which == 0) {

                    pd.setMessage("updating user pic");
                    profilePhoto = "image";
                    showImagepicDialog();

                } else if (which == 1) {

                    pd.setMessage("updating user name");
                    showNamePhoneEmailBusinessUpdateDialog("name");

                } else if (which == 2) {

                    pd.setMessage("updating user email");
                    showNamePhoneEmailBusinessUpdateDialog("email");

                } else if (which == 3) {

                    pd.setMessage("updating user phone number");
                    showNamePhoneEmailBusinessUpdateDialog("phone number");

                } else if (which == 4) {

                    pd.setMessage("updating user business");
                    showNamePhoneEmailBusinessUpdateDialog("business");

                }

            }
        });
        builder.create().show();
    }

    private void showNamePhoneEmailBusinessUpdateDialog(final String key) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update " + key);

       /* LinearLayout linearLayout =new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);*/
        final EditText input = new EditText(getActivity());
        input.setHint("Enter " + key);

        builder.setView(input);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String value = input.getText().toString().trim();


                if (!TextUtils.isEmpty(value)) {


                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    ref.child(currentuser.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Updated.......", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    pd.dismiss();
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });


                } else {
                    Toast.makeText(getActivity(), "please Enter " + key, Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();


    }


    private void showImagepicDialog() {

        String[] options = {"camera", "gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("pick image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInter, int which) {

                if (which == 0) {
                    if (!checkcamerapermissions()) {
                        requestcamerapermission();
                    } else {
                        pickfromcamera();
                    }
                } else if (which == 1) {
                    pd.setMessage("updating user name");
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


    private void requeststoragepermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkStoragepermissions() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }


    private void requestcamerapermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }


    private boolean checkcamerapermissions() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }


    private void pickfromcamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp description");
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);


    }


    private void pickfromcGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getActivity().setTitle("Profile");


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {


            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                uploadProfilePic(image_uri);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                // image_uri = data.getData();
                uploadProfilePic(image_uri);

            }


            //uploadProfilePic(image_uri);

        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    private void uploadProfilePic(Uri uri) {

        pd.show();

        String filePathAndName = storagepath + "" + profilePhoto + "_" + currentuser.getUid();
        StorageReference storageRef2 = storageRef.child(filePathAndName);
        storageRef2.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful()) {

                            HashMap<String, Object> results = new HashMap<>();
                            results.put(profilePhoto, downloadUri.toString());

                            ref.child(currentuser.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Image updated", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "error updating image", Toast.LENGTH_SHORT).show();


                                        }
                                    });

                        } else {

                            pd.dismiss();
                            Toast.makeText(getActivity(), "some error occured", Toast.LENGTH_SHORT).show();

                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
/* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CHOOSE_IMAGE && data != null && data.getData() != null) {

            Uri uriprofileimage = data.getData();

            try {

                Bitmap bitmappro = (Bitmap) MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uriprofileimage);
                profile_image.setImageBitmap(bitmappro);
                uploadImagetofirebasestorage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }*/

   /* private void saveUserInfo() {

        String displayName = user_name.getText().toString().trim();

        if (displayName.isEmpty()) {
            user_name.setError("name required");
            user_name.requestFocus();
            return;
        }

        FirebaseUser user = auther.getCurrentUser();

        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profilefragment = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profilefragment)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "profilefragment successssssss", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
        }


    }*/


    /*private void uploadImagetofirebasestorage() {

        final_logo StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");


        if (uriprofilemage != null) {
            progressbar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriprofilemage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressbar.setVisibility(View.GONE);
                    profileImageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                }
            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressbar.setVisibility(View.GONE);

                            Toast.makeText(getActivity(), " failure............ ", Toast.LENGTH_SHORT).show();

                        }

                    });

        }

    }*/

    private void showImageChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select profilefragment Image"), CHOOSE_IMAGE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case CAMERA_REQUEST_CODE: {

                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writestorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writestorageAccepted) {
                        pickfromcamera();
                    } else {
                        Toast.makeText(getActivity(), "enable permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE: {

                if (grantResults.length > 0) {
                    boolean writestorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (writestorageAccepted) {
                        pickfromcGallery();
                    } else {
                        Toast.makeText(getActivity(), "enable permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }
}