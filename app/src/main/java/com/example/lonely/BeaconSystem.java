package com.example.lonely;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import org.altbeacon.beacon.Beacon;
//import org.altbeacon.beacon.BeaconConsumer;
//import org.altbeacon.beacon.BeaconManager;
//import org.altbeacon.beacon.BeaconParser;
//import org.altbeacon.beacon.RangeNotifier;
//import org.altbeacon.beacon.Region;

import org.altbeacon.beacon.*;


public class BeaconSystem implements BeaconConsumer { // 비콘 연결을 담당하는 클래스

    private String System_UUID = "cccccccc-aaaa-aaaa-dddd-dddd20143401";

    public BeaconManager beaconManager;

    private List<Beacon> beaconList = new ArrayList<>();

    public BeaconSystem() {
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {  // 비콘의 신호를 감지하면 실행됨
                if (beacons.size() > 0) {
                    beaconList.clear(); // 비콘의 내용을 저장해둔 리스트를 초기화함
                    for (Beacon beacon : beacons) { // 받아온 비콘들의 정보를 모두 리스트에 저장. 각각의 비콘은 UUID, Major, Minor 값을 가지고 있음
                        beaconList.add(beacon);
                    }
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    public void requestPermission(int requestCode, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_FINE_LOCATION) { // 현재 위치 권한이 '어플리케이션을 사용 중에만 허용'일 때

            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("위치 권한 필요");
            builder.setMessage("어플리케이션의 위치 권한을 항상 허용으로 설정해 주세요.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) { // 권한 요청 작업을 실행한다.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        activity.requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_BACKGROUND_LOCATION);
                }

            });
            builder.show();

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) closeApplication();
        }
        if (requestCode == PERMISSION_REQUEST_BACKGROUND_LOCATION) {
            {
                if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) { // 현재 위치 권한이 '항상 허용'이 아닐 때
                    goPermissionSetting(); // 설정창으로 화면을 전환시킨다.
                    //closeApplication(); // 어플 종료(필요 시 사용)
                }
            }
        }
    }

    public boolean PaySystem() { // 매 순간 근처에 있는 비콘과의 거리와 고유 번호를 확인해 결제를 허용하는 코드
        for (Beacon beacon : beaconList) { // 비콘
            if (beacon.getId1().toString().equals(System_UUID)) {
                major = beacon.getId2().toInt(); //beacon major
                minor = beacon.getId3().toInt(); // beacon minor
                int checkMajor = major;
                int checkMinor = minor;
                if(!(checkMajor == beforeMajor && checkMinor == beforeMinor)) { // 결제 성공 시 저장해뒀던 Major, Minor 번호를 현재 새로 받아온 값과 비교해 동일한 버스인지 확인
                        if (Double.parseDouble(String.format("%.3f", beacon.getDistance())) < 2.0) { // 거리가 1.5m 이내일 때
                            beforeMajor = checkMajor; // 결제된 버스의 Major, Minor를 저장해 같은 버스에서 중복 결제되는 것을 방지
                            beforeMinor = checkMinor;
                            return true;
                        }
                }
            }
        }
        return false;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public void Permission_Check() { // 위치 권한이 허용되어 있는지 확인 후 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Andoird M버전 이상
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (activity.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("위치 권한 필요");
                        builder.setMessage("어플리케이션의 위치 권한을 항상 허용으로 변경해 주세요.");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @TargetApi(23)
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_BACKGROUND_LOCATION);
                            }

                        });
                        builder.show();
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("자동 결제 시스템을 사용할 수 없습니다!");
                        builder.setMessage("자동 결제 시스템을 사용하기 위해 해당 어플리케이션의 위치 권한을 항상 허용으로 설정하여 주십시오.\n '권한 -> 위치(항상 허용)'의 과정을 통해 권한을 변경하실 수 있습니다.");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_BACKGROUND_LOCATION);
                            }

                        });
                        builder.show();
                    }

                }
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("자동 결제 시스템을 사용할 수 없습니다!");
                builder.setMessage("자동 결제 시스템을 사용하기 위해 해당 어플리케이션의 위치 권한을 항상 허용으로 설정하여 주십시오.\n '권한 -> 위치(항상 허용)'의 과정을 통해 권한을 변경하실 수 있습니다.");
                builder.setPositiveButton(android.R.string.ok, null);

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        activity.requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_BACKGROUND_LOCATION);
                    }
                });

                builder.show();
            }
        }
    }

    private void goPermissionSetting() { // 설정창으로 화면을 전환시켜주는 코드
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        activity.startActivityForResult(intent, PERMISSION_ACCURATED_CHECK); // 두 번째 인수는 사용 안 함
    }

    private void closeApplication() { // 어플리케이션 종료 메소드
        SystemOff();
        activity.moveTaskToBack(true);						// 태스크를 백그라운드로 이동
        //activity.finishAndRemoveTask();						// 액티비티 종료 + 태스크 리스트에서 지우기
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void SystemOff() {
        beaconManager.unbind(this);
    }
}