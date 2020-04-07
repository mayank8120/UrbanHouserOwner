package com.uh.urbanhouserowner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class signup_act extends AppCompatActivity {

    Button bckbutt, logbut, gtotpbut;
    EditText nameinput, phoneinput;
    FirebaseDatabase profiledata;
    DatabaseReference existuserRef;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_act);


        bckbutt = (Button) findViewById(R.id.backbutton);
        logbut = (Button) findViewById(R.id.logbutton);
        gtotpbut = (Button) findViewById(R.id.getotpbutton);
        nameinput = (EditText) findViewById(R.id.nameinp);
        phoneinput = (EditText) findViewById(R.id.phoneinp);

        profiledata = FirebaseDatabase.getInstance();
        existuserRef = profiledata.getReference("Owner_Users");


        bckbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        logbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup_act.this, login_act.class);
                startActivity(intent);
            }
        });




        gtotpbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String mobile = phoneinput.getText().toString().trim();
                final String name =nameinput.getText().toString();
                final String phon="+91"+mobile;

                if (mobile.isEmpty() || mobile.length() < 10) {
                    phoneinput.setError("Enter a valid mobile");
                    phoneinput.requestFocus();
                    return;
                }

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Owner_Users");

                userRef.orderByChild("phonenumber").equalTo(phon).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null) {

                            Toast.makeText(signup_act.this, "This number is already registered...!", Toast.LENGTH_SHORT).show();

                        } else {


                            Intent intent=new Intent(signup_act.this, otpscreensignup.class);
                            intent.putExtra("mobile", mobile);
                            intent.putExtra("name", name);
                            Toast.makeText(signup_act.this,phon,Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }
}
