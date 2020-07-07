package com.example.socialintegrity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends Activity {

    private CircleImageView circleImageView;
    private TextView txtName , txtEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        /* fetching the username from LoginActivity */
        String username = getIntent().getStringExtra("username");
        txtName = findViewById(R.id.profile_name);
        txtName.setText(username);

        String email = getIntent().getStringExtra("email");
        txtEmail = findViewById(R.id.profile_email);
        txtEmail.setText(email);

        String image_url = getIntent().getStringExtra("image_url");
        circleImageView = findViewById(R.id.profile_pic);
        Glide.with(ProfileActivity.this).load(image_url).into(circleImageView);

    }
}