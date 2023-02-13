package com.example.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class descfragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    String name;
    String course;
    String email;
    String purl;
    String about;
    String pass;
    Double lati, longi;

    public descfragment(String name, String course, String email, String purl, String about, String pass, Double lati, Double longi) {
        this.name = name;
        this.course = course;
        this.email = email;
        this.purl = purl;
        this.about = about;
        this.pass = pass;
        this.lati = lati;
        this.longi = longi;
    }

    public descfragment() {

    }

    public descfragment(String name, String course, String email, String purl) {
        this.name=name;
        this.course=course;
        this.email=email;
        this.purl=purl;
    }

    public static descfragment newInstance(String param1, String param2) {
        descfragment fragment = new descfragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_descfragment, container, false);

        ImageView imageholder=view.findViewById(R.id.imagegholder);
        TextView nameholder=view.findViewById(R.id.nameholder);
        TextView courseholder=view.findViewById(R.id.courseholder);
        TextView emailholder=view.findViewById(R.id.emailholder);
        TextView aboutholder = view.findViewById(R.id.about);
        TextView latiholder = view.findViewById(R.id.latitude);
        TextView longholder = view.findViewById(R.id.longitude);
        TextView passholder = view.findViewById(R.id.password);

        nameholder.setText(name);
        courseholder.setText(course);
        emailholder.setText(email);
        aboutholder.setText(about);
        passholder.setText((CharSequence) pass);
        latiholder.setText(String.valueOf(lati));
        longholder.setText(String.valueOf(longi));
        Picasso.get().load(purl).placeholder(R.drawable.profile).into(imageholder);

        return  view;
    }

    public void onBackPressed()
    {
        AppCompatActivity activity=(AppCompatActivity)getContext();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.wrapper,new recfragment()).addToBackStack(null).commit();

    }
}