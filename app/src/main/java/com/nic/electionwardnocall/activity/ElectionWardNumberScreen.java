package com.nic.electionwardnocall.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.nic.electionwardnocall.R;
import com.nic.electionwardnocall.databinding.WardNumberBinding;
import com.nic.electionwardnocall.dialog.MyDialog;


public class ElectionWardNumberScreen extends AppCompatActivity implements MyDialog.myOnClickListener {

    private WardNumberBinding wardNumberBinding;
    Animation smalltobig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wardNumberBinding = DataBindingUtil.setContentView(this, R.layout.ward_number);
        smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        wardNumberBinding.activityMain.startAnimation(smalltobig);
        wardNumberBinding.setActivity(this);
        wardNumberBinding.selectDisTv.setTranslationX(800);
        wardNumberBinding.districtLayout.setTranslationX(800);
        wardNumberBinding.selectBlockTv.setTranslationX(800);
        wardNumberBinding.blockLayout.setTranslationX(800);
        wardNumberBinding.wardTv.setTranslationX(800);
        wardNumberBinding.card.setTranslationX(800);

        wardNumberBinding.selectDisTv.setAlpha(0);
        wardNumberBinding.districtLayout.setAlpha(0);
        wardNumberBinding.selectBlockTv.setAlpha(0);
        wardNumberBinding.blockLayout.setAlpha(0);
        wardNumberBinding.wardTv.setAlpha(0);
        wardNumberBinding.card.setAlpha(0);

        wardNumberBinding.selectDisTv.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        wardNumberBinding.districtLayout.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        wardNumberBinding.selectBlockTv.animate().translationX(0).alpha(1).setDuration(1200).setStartDelay(800).start();
        wardNumberBinding.blockLayout.animate().translationX(0).alpha(1).setDuration(1400).setStartDelay(1000).start();
        wardNumberBinding.wardTv.animate().translationX(0).alpha(1).setDuration(1500).setStartDelay(1100).start();
        wardNumberBinding.card.animate().translationX(0).alpha(1).setDuration(1600).setStartDelay(1200).start();


        wardNumberBinding.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (isPermissionGranted()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        call_action();
                    }
                }
                return false;
            }
        });

    }


    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    call_action();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void call_action() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String phoneNO = wardNumberBinding.phoneNo.getText().toString();
        callIntent.setData(Uri.parse("tel:" + phoneNO));
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, "Are you sure you want to exit ?", "Exit");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        }
    }
}
