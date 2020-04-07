package com.uh.urbanhouserowner.ui.contactus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.uh.urbanhouserowner.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class contact_us extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contactus, container, false);


        final TextView txtvw = (TextView) view.findViewById(R.id.call_call);
        final EditText your_name = (EditText) view.findViewById(R.id.your_name);
        final EditText your_email = (EditText) view.findViewById(R.id.your_email);
        final EditText your_subject = (EditText) view.findViewById(R.id.your_subject);
        final EditText your_message = (EditText) view.findViewById(R.id.your_message);


        Button email = (Button) view.findViewById(R.id.post_message);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = your_name.getText().toString();
                String email = your_email.getText().toString();
                String subject = your_subject.getText().toString();
                String message = your_message.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    your_name.setError("Enter Your Name");
                    your_name.requestFocus();
                    return;
                }

                Boolean onError = false;
                if (!isValidEmail(email)) {
                    onError = true;
                    your_email.setError("Invalid Email");
                    return;
                }

                if (TextUtils.isEmpty(subject)) {
                    your_subject.setError("Enter Your Subject");
                    your_subject.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(message)) {
                    your_message.setError("Enter Your Message");
                    your_message.requestFocus();
                    return;
                }

                Intent sendEmail = new Intent(Intent.ACTION_SEND);

                /* Fill it with Data */
                sendEmail.setType("plain/text");
                sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"mayanklaxkar11@gmail.com"});
                sendEmail.putExtra(Intent.EXTRA_SUBJECT, subject);
                sendEmail.putExtra(Intent.EXTRA_TEXT,
                        "name:" + name + '\n' + "Email ID:" + email + '\n' + "Message:" + '\n' + message);

                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(sendEmail, "Send mail..."));


            }
        });

        txtvw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:6264204218"));
                startActivity(callIntent);

            }
        });

           /* @Override
            public void onResume() {
                super.onResume();
                //Get a Tracker (should auto-report)
            }

            @Override
            protected void onStart() {
                super.onStart();
            }

            @Override
            protected void onStop() {
                super.onStop();
            }

            // validating email id

            */

        return view;
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getActivity().setTitle("contact_us");


    }


}