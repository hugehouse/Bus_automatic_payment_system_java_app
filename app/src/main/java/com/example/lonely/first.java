package com.example.lonely;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class first extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    String user_ID = "";
    String user_cash = "";

    String lastPayment = "";
    Date nowTime;
    SimpleDateFormat formatFullTime;

    Button btnCharge;
    TextView txtBalance;

    BeaconSystem beaconSystem;
    Thread beaconThread;
    String checkedMajor = "";

    DatabaseReference reference;
    DatabaseReference busReference;
    Query checkUser;
    Query checkBus;

    ///
    MediaPlayer sound_in;
    MediaPlayer sound_transfer;
    ///
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        BeaconInit();
        loadUserID();
        userQueryInit();
        loadUserProfile(); // DB에서 데이터 불러오는 작업 실행
        TimeInit();

        ///
        setSound(); // 임시로 넣은 소리 출력 코드
        ///
    }

    private void setSound() {
        sound_in = MediaPlayer.create(this, R.raw.bus_in);
        sound_transfer = MediaPlayer.create(this, R.raw.changeride);
    }

    private void init() {
        /* Window Actionbar 위에 없애는 함수 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        btnCharge = findViewById(R.id.charge);
        txtBalance = findViewById(R.id.balance);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargeCash();
            }
        });
    }

    private void BeaconInit() {
        beaconSystem = new BeaconSystem(this, 0);

        BeaconCheck beaconCheck = new BeaconCheck(handler, beaconSystem, this);
        beaconThread = new Thread(beaconCheck, "A");

        beaconThread.start();
    }

    private void TimeInit() {
        nowTime = new Date();
        formatFullTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    }

    private boolean checkTransfer() { // !!!!!! 당일, 다음 날까지만 계산 가능하고 그 다음 달부터는 계산 X
        String[] Last = lastPayment.split("-"); // 과거 시간을 '-'를 기준으로 나눔
        String[] Now = formatFullTime.format(nowTime).split("-"); // 현재 시간을 '-'를 기준으로 나눔
        // [0] -> 년, [1] -> 월, [2] -> 일, [3] -> 시, [4] -> 분, [5] -> 초

        int intLast = (Integer.parseInt(Last[3]) * 60) * 60 + // 초 단위로 계산하기 위한 공식. 마지막 결제 시간과 현재 시간을 초 단위로 변환
                Integer.parseInt(Last[4]) * 60 +
                Integer.parseInt(Last[5]);
        int intNow = (Integer.parseInt(Now[3]) * 60) * 60 +
                Integer.parseInt(Now[4]) * 60 +
                Integer.parseInt(Now[5]);

        if (Last[0].equals(Now[0])) {
            if (Last[1].equals(Now[1])) {
                if (Last[2].equals(Now[2])) { // 년, 월, 일이 같으면 단순 시간 계산
                    if ((intNow - intLast) <= 2400 && (intNow - intLast) >= 0) // 40분이 지나지 않았다면 true(환승 성공) 반환
                        return true;
                } else if (Integer.parseInt(Last[2]) + 1 == Integer.parseInt(Now[2])) { // 다음 날일 경우 값을 보완해 계산
                    if (((intNow + 86400) - intLast) <= 2400 && ((intNow + 86400) - intLast) >= 0) // 86400(1일을 초로 나눈 수)를 더해 계산
                        return true;
                }
            }
        }
        return false;
    }

    private void userQueryInit() {
        reference = FirebaseDatabase.getInstance().getReference("users");
        checkUser = reference.orderByChild("name").equalTo(user_ID);
    }

    private void TimeToken() {

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;

            case R.id.nav_logout:
                Intent intent_logout = new Intent(first.this, Login.class);
                startActivity(intent_logout);

                finish(); // 로그아웃 버튼 클릭시 화면 종료
                break;

            case R.id.nav_profile:
                Intent intent = new Intent(first.this, UserProfile.class);
                intent.putExtra("name", user_ID);

                startActivity(intent);
                break;

            case R.id.nav_share:
                Toast.makeText(this, "미구현 기능 입니다.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_rate:
                Toast.makeText(this, "미구현 기능 입니다.", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void loadUserProfile() { // DB에 저장된 보유액과 마지막 결제 시간을 어플리케이션에 불러옴
        checkUser.addValueEventListener(new ValueEventListener() { // DB에 저장된 사용자의 정보값이 변경될 때마다 호출된다.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_cash = dataSnapshot.child(user_ID).child("userCash").getValue(String.class); // DB에 저장된 보유액을 가져옴
                    lastPayment = dataSnapshot.child(user_ID).child("lastPayment").getValue(String.class); // DB에 저장된 마지막 결제 시간을 가져옴
                    handler.sendEmptyMessage(0); // 데이터 완전히 수신될 때까지 어플 내 업데이트 반복 // 어플리케이션 화면을 갱신할 수 있게 밑에 있는 Handler 코드를 실행
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            for(int i = 0; i < 30; i++) { // 6초 지나면 종료
                setUserProfile(); // 어플리케이션 금액 부분에 DB에서 가져온 보유액을 입력
                handler.sendEmptyMessageDelayed(0, 200); // 0.2초마다 실행되며 30번 반복(6초)
            }
            handler.removeMessages(0);
        }
    };

    private void setUserProfile() {
        txtBalance.setText(user_cash);
    }

    private void loadUserID() {
        Intent intent = getIntent();
        user_ID = intent.getStringExtra("name");
    }

    private void chargeCash() { // 임시로 만들어둔 충전 기능
        loadUserProfile(); // DB에 등록된 보유액으로 갱신
        int transCash = Integer.parseInt(user_cash) +10000;
        reference.child(user_ID).child("userCash").setValue(Integer.toString(transCash));
        loadUserProfile(); // 충전된 금액을 화면에 갱신
    }

    public void payToBus() { // 실질적인 결제를 진행하는 메소드
        busReference = FirebaseDatabase.getInstance().getReference("busInfo"); // DB에 저장된 버스와 관련된 정보를 가져오기 위해 reference를 선언
        checkedMajor = Integer.toString(beaconSystem.getMajor()); // 인식된 비콘의 Major을 가져옴
        checkBus = busReference.orderByChild("uniqueNumber").equalTo(checkedMajor);
        checkBus.addListenerForSingleValueEvent(new ValueEventListener() { // 비콘에서 가져온 Major(회사 고유 번호)와 동일한 Major를 DB에서 찾아 데이터를 가져옴
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!checkTransfer()) { // 환승 여부를 따져 환승이 아니라면 일반 승차 진행
                        String price = dataSnapshot.child(checkedMajor).child("busList").child(Integer.toString(beaconSystem.getMinor())).
                                child("price").getValue(String.class); // Minor(버스 고유 번호)에 등록된 버스 요금을 가져옴
                        int transCash = Integer.parseInt(user_cash) - Integer.parseInt(price); // 현재 보유 금액에서 요금을 뺌
                        reference.child(user_ID).child("userCash").setValue(Integer.toString(transCash)); // 차감된 보유액을 DB에 갱신

                        ///
                        sound_in.start(); // 소리 재생 부분인데 이건 임시로 넣은 거
                        ///
                    }
                    else
                        sound_transfer.start();
                    TimeInit(); // 현재 시간을 측정한다.
                    reference.child(user_ID).child("lastPayment").setValue(formatFullTime.format(nowTime)); // 측정된 시간(마지막 결제 시간)을 DB에 저장
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        loadUserProfile(); // 결제를 통해 갱신된 현재 보유액을 확인해 어플리케이션 화면의 보유액 갱신
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconThread.interrupt();
        beaconSystem.SystemOff();
    }
}

class BeaconCheck implements Runnable {
    Handler handler;
    BeaconSystem beaconSystem;
    first useFirst;

    public BeaconCheck(Handler handler, BeaconSystem beaconSystem, first useFirst) {
        this.handler = handler; // 사용 X 혹시 몰라서 넣어둠
        this.beaconSystem = beaconSystem;
        this.useFirst = useFirst;
    }

    public void run() {
        while(true) {
            if(beaconSystem.PaySystem()) // BeaconSystem에 구현돼있는 PaySystem 메소드에서 결제 조건이 충족됐는지 판단
                useFirst.payToBus(); // 결제를 해야될 상황이라면 해당 메소드를 실행해 결제 진행
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
