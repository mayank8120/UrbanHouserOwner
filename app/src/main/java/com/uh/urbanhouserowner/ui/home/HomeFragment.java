package com.uh.urbanhouserowner.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uh.urbanhouserowner.AdapterPosts;
import com.uh.urbanhouserowner.Modelpost;
import com.uh.urbanhouserowner.R;
import com.uh.urbanhouserowner.add_property;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    RecyclerView recyclerView;
    List<Modelpost> postList;
    AdapterPosts adapterPosts;


    FloatingActionButton fabopen, fab_addproperty, fab_addhostel, fab_addbusiness, fab_roommate;
    Animation FabOpen, FabClose, FabClock, FabaClock;

    RecyclerView postsrecyclerview;
    boolean[] isOpen = {false};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        recyclerView = view.findViewById(R.id.postsRecyyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        loadPosts();

        /*fab_addproperty= view.findViewById(R.id.fab_addproperty);
        fab_addhostel = view.findViewById(R.id.fab_addhostel);
        fab_addbusiness = view.findViewById(R.id.fab_addbusiness);*/
        fab_roommate = view.findViewById(R.id.fab_roompartner);
        fabopen = view.findViewById(R.id.fab);
        //postsrecyclerview = view.findViewById(R.id.recyclerview_posts);

        firebaseAuth = FirebaseAuth.getInstance();

        FabOpen = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        FabClock = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_fab);
        FabaClock = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anti_rotate_fab);

/*
        fab_addproperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonclicks();
            }
        });

        fab_addhostel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonclicks();
            }
        });

        fab_addbusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonclicks();
            }
        });*/

        fab_roommate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonclicks();
            }
        });


        fabopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fabopenbutton();
            }
        });

        return view;

    }


    private void loadPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        Query query = ref.orderByChild("uid").equalTo(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Modelpost modelpost = ds.getValue(Modelpost.class);

                    postList.add(modelpost);
                    adapterPosts = new AdapterPosts(getActivity(), postList);
                    recyclerView.setAdapter(adapterPosts);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void fabopenbutton() {
        if (isOpen[0]) {

            /*fab_addproperty.startAnimation(FabOpen);
            fab_addhostel.startAnimation(FabOpen);
            fab_addbusiness.startAnimation(FabOpen);*/
            fab_roommate.startAnimation(FabOpen);

            fabopen.startAnimation(FabClock);

            /*fab_addproperty.setClickable(true);
            fab_addhostel.setClickable(true);
            fab_addbusiness.setClickable(true);*/
            fab_roommate.setClickable(true);


            isOpen[0] = false;


        } else {


            /*fab_addproperty.startAnimation(FabClose);
            fab_addhostel.startAnimation(FabClose);
            fab_addbusiness.startAnimation(FabClose);*/
            fab_roommate.startAnimation(FabClose);

            fabopen.startAnimation(FabaClock);

            /*fab_addproperty.setClickable(false);
            fab_addhostel.setClickable(false);
            fab_addbusiness.setClickable(false);*/
            fab_roommate.setClickable(false);


            isOpen[0] = true;


        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getActivity().setTitle("Home");


    }

    private void buttonclicks() {

        Intent intent = new Intent(getActivity(), add_property.class);
        startActivity(intent);

    }
}