package com.uh.urbanhouserowner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class cardopen extends AppCompatActivity {

    ImageButton backbutt;
    FirebaseAuth auther;
    FirebaseUser currentuser;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardopen);

        Button editbutton = findViewById(R.id.editbutt);

        //final TextView prpname = findViewById(R.id.propertyname);
        final TextView prpprice = findViewById(R.id.propprice);
        final TextView prpadd = findViewById(R.id.propaddress);
        final TextView citynam = findViewById(R.id.propcity);
        final TextView daat = findViewById(R.id.avaidate);
        final TextView owname = findViewById(R.id.ownername);
        final ImageView propertyimageee = findViewById(R.id.propertypictureextend);
        final TextView owcontact = findViewById(R.id.ownercontact);
        //tv.setText("hello"+getIntent().getStringExtra("postid"));

        backbutt=findViewById(R.id.backbutton);
        backbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final FirebaseAuth auther;
        FirebaseUser currentuser;
        FirebaseDatabase profiledata;
        DatabaseReference ref;
        StorageReference storageRef;

        auther = FirebaseAuth.getInstance();
        currentuser = auther.getCurrentUser();
        profiledata = FirebaseDatabase.getInstance();
        ref = profiledata.getReference("Posts");
        storageRef = getInstance().getReference();

        Query query = ref.orderByChild("pid").equalTo(getIntent().getStringExtra("postid"));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String pname = "" + ds.child("pid").getValue();
                    String price = "" + ds.child("price").getValue();
                    String add = "" + ds.child("address").getValue();
                    String city = "" + ds.child("city").getValue();
                    String date = "" + ds.child("date").getValue();
                    String oname = "" + ds.child("uname").getValue();
                    String ocontact = "" + ds.child("phone").getValue();
                    String pimg = "" + ds.child("pimage").getValue();

                    try {
                        Picasso.get().load(pimg).fit().centerCrop().into(propertyimageee);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_photo_camera_black_24dp).fit().centerCrop().into(propertyimageee);
                    }
                    //prpname.setText(pname);
                    prpprice.setText(price);
                    prpadd.setText(add);
                    citynam.setText(city);
                    daat.setText(date);
                    owname.setText(oname);
                    owcontact.setText(ocontact);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String timestamp = String.valueOf(System.currentTimeMillis());
                FirebaseUser currentUser = auther.getCurrentUser();
                String uid = currentUser.getUid();
                String deleterequest="edit or delete request";
                HashMap<Object, String> hashMap = new HashMap<>();

                hashMap.put("postid",getIntent().getStringExtra("postid"));
                hashMap.put("editordelete", deleterequest);
                hashMap.put("timestamp", timestamp);
                hashMap.put("userid", uid);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Deleteoreditrequest");
                ref.child(timestamp).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //pd.dismiss();
                                Toast.makeText(cardopen.this, "Edit or delete request generated. You will receive a call from a team within 24hrs.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(cardopen.this, main_activity.class);
                                startActivity(intent);


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                //pd.dismiss();
                                Toast.makeText(cardopen.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });








            }
        });
        /*editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference picref= FirebaseStorage.getInstance().getReferenceFromUrl(pimg);
                picref.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Query fquery= FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(getIntent().getStringExtra("postid"));
                                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                                            ds.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            }
        });*/


    }
}
