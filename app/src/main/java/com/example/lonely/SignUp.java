package com.example.lonely;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    TextInputLayout regName, regUsername, regPassword, regUserage, regPhoneNo;
    Button regBtn, regToLoginBtn;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Window Actionbar 위에 없애는 함수 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        // 회원가입 부분 텍스트창
        regName = findViewById(R.id.reg_name);
        regUsername = findViewById(R.id.reg_username);
        regPassword = findViewById(R.id.reg_password);
        regUserage = findViewById(R.id.reg_userage);
        regPhoneNo = findViewById(R.id.reg_phoneNo);

        regBtn = findViewById(R.id.reg_btn);
        regToLoginBtn = findViewById(R.id.reg_login_btn);

        // 회원가입 버튼 눌렀을 때 인스턴스 값 데이터베이스에 저장
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(v);

                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("users"); // DB에 저장하기 위해 reference를 가져온다.

                // 입력된 값 가져오기
                String name = regName.getEditText().getText().toString(); // 유저 ID
                String username = regUsername.getEditText().getText().toString();
                String userage = regUserage.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();
                String phoneNo = regPhoneNo.getEditText().getText().toString();
                String userCash = "0"; // 보유액 항목
                String lastPayment = "9999-99-99-99-99-99"; // 마지막 결제 시간
                UserHelperClass helperClass = new UserHelperClass(name, username, userage, password, phoneNo, userCash, lastPayment); // DB에 저장하기 위해 미리 만들어둔 틀에 값 전달

                // DB에 입력된 값을 전달해 유저를 등록한다.
                reference.child(name).setValue(helperClass);
            }
        });
    }

    // 아이디 및 이메일 주소 텍스트 필드 제어
    private Boolean validateName() {
        String val = regName.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            regName.setError("아이디를 한글자 이상 입력 해주세요.");
            return false;
        } else if (val.length() >= 20) {
            regName.setError("아이디가 너무 길어요.");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            regName.setError("띄어쓰기를 사용할 수 없습니다.");
            return false;
        } else {
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }

    // 사용자 이름 텍스트 필드 제어
    private Boolean validateUsername() {
        String val = regUsername.getEditText().getText().toString();

        if (val.isEmpty()) {
            regUsername.setError("이름을 입력 해주세요.");
            return false;
        } else {
            regUsername.setError(null);
            regUsername.setErrorEnabled(false);
            return true;
        }
    }

    // 사용자 이름 텍스트 필드 제어
    private Boolean validateUserage() {
        String val = regUserage.getEditText().getText().toString();

        if (val.isEmpty()) {
            regUserage.setError("나이를 입력 해주세요.");
            return false;
        } else {
            regUserage.setError(null);
            regUserage.setErrorEnabled(false);
            return true;
        }
    }

    // 사용자 전화번호 텍스트 필드 제어
    private Boolean validatePhoneNo() {
        String val = regPhoneNo.getEditText().getText().toString();

        if (val.isEmpty()) {
            regPhoneNo.setError("번호를 입력 해주세요");
            return false;
        } else {
            regPhoneNo.setError(null);
            regPhoneNo.setErrorEnabled(false);
            return true;
        }
    }

    // 사용자 비밀번호 텍스트 필드 제어
    private Boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            regPassword.setError("비밀번호를 입력 해주세요");
            return false;

        } else if (!val.matches(passwordVal)) {
            regPassword.setError("좀 더 강력한 비밀번호를 입력 해주세요.");
            return false;

        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }

    public void registerUser(View view) {
        if (!validateName() | !validatePassword() | !validatePhoneNo() | !validateUserage() | !validateUsername()) {
            return;
        }
    }
}