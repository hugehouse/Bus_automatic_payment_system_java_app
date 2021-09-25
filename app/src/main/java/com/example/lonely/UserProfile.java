package com.example.lonely;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    TextInputLayout TxtUserName, TxtUserage, TxtPhoneNo;
    TextView fullNameLabel, usernameLabel, Txtbalance;
    String user_ID = "";
    String user_name = "";
    String user_age = "";
    String user_phoneNum = "";
    String user_Cash = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fullNameLabel = findViewById(R.id.fullname_field);
        usernameLabel = findViewById(R.id.username_field);
        Txtbalance = findViewById(R.id.balance);

        TxtUserName = findViewById(R.id.full_name_profile);
        TxtUserage = findViewById(R.id.userage_profile);
        TxtPhoneNo = findViewById(R.id.phone_no_profile);

        ///
        loadUserID(); // 직전의 intent에서 ID 불러오기
        loadUserProfile(); // DB에서 데이터 불러오는 작업 실행
        handler.sendEmptyMessage(0); // 핸들러 쓰레드 실행
        ///
    }

    ///
    private void loadUserProfile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("name").equalTo(user_ID);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_name = dataSnapshot.child(user_ID).child("username").getValue(String.class);
                    user_age = dataSnapshot.child(user_ID).child("userage").getValue(String.class);
                    user_phoneNum = dataSnapshot.child(user_ID).child("phoneNo").getValue(String.class);
                    user_Cash = dataSnapshot.child(user_ID).child("userCash").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            setUserProfile();
            if(!user_Cash.equals(""))
                handler.removeMessages(0); // 값이 들어왔다는 게 확인되면 핸들러 종료
            handler.sendEmptyMessageDelayed(0, 200);
        }
    };
    private void setUserProfile() {
        fullNameLabel.setText(user_ID);
        usernameLabel.setText(user_name);
        Txtbalance.setText(user_Cash + "원");

        TxtUserName.getEditText().setText(user_name);
        TxtUserage.getEditText().setText(user_age);
        TxtPhoneNo.getEditText().setText(user_phoneNum);
    }
    public void loadUserID() {
        Intent intent = getIntent();
        user_ID = intent.getStringExtra("name");
    }
    ///
}