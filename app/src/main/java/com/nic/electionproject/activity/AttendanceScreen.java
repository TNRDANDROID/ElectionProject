package com.nic.electionproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.nic.electionproject.R;
import com.nic.electionproject.adapter.AttendanceNameListAdapter;
import com.nic.electionproject.api.Api;
import com.nic.electionproject.api.ServerResponse;
import com.nic.electionproject.databinding.AttendanceBinding;
import com.nic.electionproject.windowpreferences.WindowPreferencesManager;

public class AttendanceScreen extends AppCompatActivity implements Api.ServerResponseListener {
public AttendanceBinding attendanceBinding;
private AttendanceNameListAdapter attendanceNameListAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attendanceBinding = DataBindingUtil.setContentView(this, R.layout.attendance);
        attendanceBinding.setActivity(this);
        WindowPreferencesManager windowPreferencesManager = new WindowPreferencesManager(this);
        windowPreferencesManager.applyEdgeToEdgePreference(getWindow());
        attendanceNameListAdapter = new AttendanceNameListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        attendanceBinding.staffList.setLayoutManager(mLayoutManager);
        attendanceBinding.staffList.setItemAnimator(new DefaultItemAnimator());
        attendanceBinding.staffList.setHasFixedSize(true);
        attendanceBinding.staffList.setNestedScrollingEnabled(false);
        attendanceBinding.staffList.setFocusable(false);
        attendanceBinding.staffList.setAdapter(attendanceNameListAdapter);
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void setHeaderCount(int count){
        attendanceBinding.questionsCheckedHeaderTv.setText((String.valueOf(count)).concat(" of " + "20"+ " Employees"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void homePage() {
        Intent intent = new Intent(this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
