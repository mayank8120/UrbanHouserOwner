package com.uh.urbanhouserowner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login_act extends AppCompatActivity {


    FirebaseDatabase profiledata;
    DatabaseReference existuserRef;
    private FirebaseAuth auther;

    @Override
    protected void onStart() {
        super.onStart();
        if (auther.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, main_activity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_act);

        Button otpbutt, sigbutt, bckbut;
        final EditText phonenum;
        auther = FirebaseAuth.getInstance();

        otpbutt = (Button) findViewById(R.id.getotpbutton);
        sigbutt = (Button) findViewById(R.id.signupbutton);
        bckbut = (Button) findViewById(R.id.backbutton);
        phonenum = findViewById(R.id.phonenumber);

        otpbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String mobile = phonenum.getText().toString().trim();
                final String phon = "+91" + mobile;

                if (mobile.isEmpty() || mobile.length() < 10) {
                    phonenum.setError("Enter a valid mobile");
                    phonenum.requestFocus();
                    return;
                }


                /*Intent intent=new Intent(login_act.this,otpscreenlogin.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);*/


                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Owner_Users");

                userRef.orderByChild("phonenumber").equalTo(phon).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null) {

                            Intent intent = new Intent(login_act.this, otpscreenlogin.class);
                            intent.putExtra("mobile", mobile);
                            Toast.makeText(login_act.this, phon, Toast.LENGTH_SHORT).show();
                            startActivity(intent);

                        } else {


                            Toast.makeText(login_act.this, "This number is not registered...!", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        bckbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sigbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login_act.this, signup_act.class);
                startActivity(intent);
            }
        });
    }
}
