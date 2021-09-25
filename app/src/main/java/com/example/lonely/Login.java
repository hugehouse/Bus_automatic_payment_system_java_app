package com.example.lonely;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    Button callSignUp, login_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout name, password;

    ///
    BeaconSystem beaconSystem;
    ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        BeaconInit();
    }

    private void init() {
        /* Window Actionbar 위에 없애는 함수 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //Hooks
        callSignUp = findViewById(R.id.signup_screen);

        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        password = findViewById(R.id.reg_password);
        name = findViewById(R.id.reg_name);
        login_btn = findViewById(R.id.login_btn);

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);

                // 회원가입 버튼 클릭시 애니메이션 동작
                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logoText, "logo_text");
                pairs[2] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[3] = new Pair<View, String>(name, "username_tran");
                pairs[4] = new Pair<View, String>(password, "password_tran");
                pairs[5] = new Pair<View, String>(login_btn, "button_tran");
                pairs[6] = new Pair<View, String>(callSignUp, "login_signup_tran");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            }
        });
    }

    private void BeaconInit() {
        beaconSystem = new BeaconSystem(this);
    }

    private Boolean validateName() {
        String val = name.getEditText().getText().toString();

        if (val.isEmpty()) {
            name.setError("아이디 및 이메일을 한글자 이상 입력 해주세요.");
            return false;
        } else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();
        if (val.isEmpty()) {
            password.setError("비밀번호를 한글자 이상 입력 해주세요.");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view) {
        if (!validateName() | !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {
        final String userEnteredName = name.getEditText().getText().toString().trim(); // 입력된 ID를 가져온다
        final String userEnteredPassword = password.getEditText().getText().toString().trim(); // 입력된 Password를 가져온다

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("name").equalTo(userEnteredName);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name.setError(null);
                    name.setErrorEnabled(false);
                    String passwordFromDB = dataSnapshot.child(userEnteredName).child("password").getValue(String.class); // DB에 입력된 ID의 Password를 가져온다.
                    if (passwordFromDB.equals(userEnteredPassword)) { // 가져온 Password와 입력된 값을 비교해 같다면 로그인 성공으로 판단하고 이하 코드가 실행된다.

                        name.setError(null);
                        name.setErrorEnabled(false);

                        String nameFromDB = dataSnapshot.child(userEnteredName).child("name").getValue(String.class); // 사실상 없어도 되는 코드
                        Intent intent = new Intent(getApplicationContext(), first.class);
                        intent.putExtra("name", nameFromDB); // 유저의 정보를 어플의 다른 화면에서도 매번 불러오기 위해 전송한다.
                        startActivity(intent); // 메인 화면을 띄운다.
                    } else {
                        password.setError("비밀번호에 문제가 있습니다.");
                        name.requestFocus();
                    }
                } else {
                    name.setError("존재하지 않는 아이디 입니다.");
                    name.requestFocus();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        beaconSystem.requestPermission(requestCode, grantResults); // 권한 요청에 대한 결과를 판단 후 설정창을 열거나 어플 종료
    }
}