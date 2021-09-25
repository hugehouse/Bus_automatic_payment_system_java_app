package com.example.lonely;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // 인트로 화면 실행 시간 3.5초
    private static int SPLASH_SCREEN = 3500;

    Animation topAnimation, bottomAnimation;
    ImageView image;
    TextView logo, logo1, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Window Actionbar 위에 없애는 함수
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // 애니메이션
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // 이미지 및 텍스트 애니메이션 움직임 구현
        image = findViewById(R.id.imageView); // ImageView
        logo = findViewById(R.id.textView); // "NO LOOK"
        logo1 = findViewById(R.id.textView1); // "PAY"
        slogan = findViewById(R.id.textView2); // "Walk-in Bill Payment System"

        image.setAnimation(topAnimation);
        logo.setAnimation(bottomAnimation);
        logo1.setAnimation(bottomAnimation);
        slogan.setAnimation(bottomAnimation);

        // 화면 전환
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Login.class);

                // 메인 화면의 버스 이미지, DDUCK DDACK 글씨가 로그인 화면으로 전환될 때 애니메이션 효과 적용
                Pair[]  pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(image, "logo_image"); // 버스 이미지
                pairs[1] = new Pair<View, String>(logo, "logo_text"); // DDUCK DDACK
                pairs[2] = new Pair<View, String>(logo1, "logo_text"); // DDUCK DDACK

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                    // 화면이 전환될 때 애니메이션 실행
                    startActivity(intent, options.toBundle());
                }
            }
        }, SPLASH_SCREEN);
    }
}
