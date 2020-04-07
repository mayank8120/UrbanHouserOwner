package com.uh.urbanhouserowner.ui.faq;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.uh.urbanhouserowner.Expandablelistviewadapter;
import com.uh.urbanhouserowner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FAQFragment extends Fragment {


    ExpandableListView expandableListView;
    Expandablelistviewadapter adapter;
    List<String> lisdataheader;
    HashMap<String, List<String>> listHashMap;


    public FAQFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        expandableListView = view.findViewById(R.id.expandablelistview);
        initdat();

        adapter = new Expandablelistviewadapter(getActivity(), lisdataheader, listHashMap);
        expandableListView.setAdapter(adapter);

        return view;
    }

    public void initdat() {
        lisdataheader = new ArrayList<>();
        listHashMap = new HashMap<>();

        lisdataheader.add("Are your services genuine?");
        lisdataheader.add("Why should we trust you?");
        lisdataheader.add("What if tenant faces any issue?");
        lisdataheader.add("What if the landlord is not satisfied?");
        lisdataheader.add("What would happen if tenant creates issues regarding payment?");
        lisdataheader.add("What if the tenant does not follow the terms and conditions of the owner?");

        List<String> edmtDev = new ArrayList<>();
        edmtDev.add("We assure you that our services are 100% genuine, from our side we are trying our best to provide you ROOMMET services as best as possible,even after that if any problem arises feel free to contact us directly from contact us page.");

        List<String> androidStudio = new ArrayList<>();
        androidStudio.add("We focus on quality and not quantity,you should totally trust us as our policy is customer satisfaction rather then business.");
        /*androidStudio.add("Google Map");
        androidStudio.add("Chat Application");
        androidStudio.add("Firebase ");*/

        List<String> xamarin = new ArrayList<>();
        xamarin.add("Owner as well as the tenant is free to contact us directly.We are happy to help our customer 24*7.");
        /*xamarin.add("Xamarin Google Map");
        xamarin.add("Xamarin Chat Application");
        xamarin.add("Xamarin Firebase ");*/

        List<String> uwp = new ArrayList<>();
        uwp.add("Before using our services please make sure you read our terms & conditions properly and in case you face any dissatisfaction just make a call to us.");

        List<String> ten = new ArrayList<>();
        ten.add("The owner is free to take any actions against the tenant following our terms and conditions.ROOMMET will not be responsible for any type of disputes between owner and tenant related to rent.");

        List<String> own = new ArrayList<>();
        own.add("The first action will be a notice to the tenant and if the same is repeated then then the written notice can be sent to the tenant via ROOMMET to leave the room within specified time period.");


        listHashMap.put(lisdataheader.get(0), edmtDev);
        listHashMap.put(lisdataheader.get(1), androidStudio);
        listHashMap.put(lisdataheader.get(2), xamarin);
        listHashMap.put(lisdataheader.get(3), uwp);
        listHashMap.put(lisdataheader.get(4), ten);
        listHashMap.put(lisdataheader.get(5), own);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("FAQ's");
    }
}