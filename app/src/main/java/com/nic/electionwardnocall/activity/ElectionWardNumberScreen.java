package com.nic.electionwardnocall.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.electionwardnocall.DataBase.DBHelper;
import com.nic.electionwardnocall.DataBase.dbData;
import com.nic.electionwardnocall.R;
import com.nic.electionwardnocall.Session.PrefManager;
import com.nic.electionwardnocall.adapter.CommonAdapter;
import com.nic.electionwardnocall.api.Api;
import com.nic.electionwardnocall.api.ApiService;
import com.nic.electionwardnocall.api.ServerResponse;
import com.nic.electionwardnocall.constant.AppConstant;
import com.nic.electionwardnocall.databinding.WardNumberBinding;
import com.nic.electionwardnocall.dialog.MyDialog;
import com.nic.electionwardnocall.pojo.ElectionWardNoCall;
import com.nic.electionwardnocall.support.ProgressHUD;
import com.nic.electionwardnocall.utils.UrlGenerator;
import com.nic.electionwardnocall.utils.Utils;
import com.nic.electionwardnocall.windowpreferences.WindowPreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.nic.electionwardnocall.DataBase.DBHelper.RO_USER_TABLE_NAME;

public class ElectionWardNumberScreen extends AppCompatActivity implements MyDialog.myOnClickListener, Api.ServerResponseListener {

    private WardNumberBinding wardNumberBinding;
    private List<String> RuralUrbanList = new ArrayList<>();
    private List<ElectionWardNoCall> District = new ArrayList<>();
    private List<ElectionWardNoCall> Block = new ArrayList<>();
    public dbData dbData = new dbData(this);
    private SQLiteDatabase db;
    public static DBHelper dbHelper;
    Animation smalltobig;
    final Handler handler = new Handler();
    private PrefManager prefManager;
    String pref_Block, pref_district, pref_Village;
    boolean isPanchayatUnion, isMunicipality, isTownPanchayat, isCorporation;
    private ProgressHUD progressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        wardNumberBinding = DataBindingUtil.setContentView(this, R.layout.ward_number);
        wardNumberBinding.setActivity(this);
        WindowPreferencesManager windowPreferencesManager = new WindowPreferencesManager(this);
        windowPreferencesManager.applyEdgeToEdgePreference(getWindow());

        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        prefManager = new PrefManager(this);
        wardNumberBinding.homeImg.startAnimation(smalltobig);
        wardNumberBinding.selectRuralUrbanTv.setTranslationX(800);
        wardNumberBinding.ruralUrbanLayout.setTranslationX(800);
        wardNumberBinding.selectDistrictTv.setTranslationX(800);
        wardNumberBinding.districtLayout.setTranslationX(800);

        wardNumberBinding.selectVillageTv.setTranslationX(800);
        wardNumberBinding.villageLayout.setTranslationX(800);
        wardNumberBinding.call.setTranslationY(800);

        wardNumberBinding.selectRuralUrbanTv.setAlpha(0);
        wardNumberBinding.ruralUrbanLayout.setAlpha(0);
        wardNumberBinding.selectDistrictTv.setAlpha(0);
        wardNumberBinding.districtLayout.setAlpha(0);
        wardNumberBinding.selectVillageTv.setAlpha(0);
        wardNumberBinding.villageLayout.setAlpha(0);

        wardNumberBinding.selectRuralUrbanTv.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        wardNumberBinding.ruralUrbanLayout.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        wardNumberBinding.selectDistrictTv.animate().translationX(0).alpha(1).setDuration(1200).setStartDelay(800).start();
        wardNumberBinding.districtLayout.animate().translationX(0).alpha(1).setDuration(1400).setStartDelay(1000).start();
        wardNumberBinding.selectVillageTv.animate().translationX(0).alpha(1).setDuration(1500).setStartDelay(1200).start();
        wardNumberBinding.villageLayout.animate().translationX(0).alpha(1).setDuration(1600).setStartDelay(1400).start();
        wardNumberBinding.call.animate().translationY(0).alpha(1).setDuration(1800).setStartDelay(1700).start();
        loadOfflineRuralUrbanListDBValues();
        if (Utils.isOnline()) {
            fetchApi();
        } else {
            Utils.showAlert(this, getResources().getString(R.string.no_internet));
        }

        wardNumberBinding.ruralUrbanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    isPanchayatUnion = false;
                    isMunicipality = false;
                    isTownPanchayat = false;
                    isCorporation = false;
                    loadOfflineDistrictListDBValues();
                    wardNumberBinding.roUserName.setText("");
                    wardNumberBinding.ronameTv.setVisibility(View.GONE);
                    wardNumberBinding.RONameLayout.setVisibility(View.GONE);
                    wardNumberBinding.phoneNo.setText("");
                    wardNumberBinding.wardTv.setVisibility(View.GONE);
                    wardNumberBinding.pollingStationName.setVisibility(View.GONE);
                } else if (position == 1) {
                    isPanchayatUnion = true;
                    isMunicipality = false;
                    isTownPanchayat = false;
                    isCorporation = false;
                    wardNumberBinding.selectBlockTv.setText("Local Body Name");
                    loadOfflineDistrictListDBValues();
                    wardNumberBinding.blockSpinner.setAdapter(null);
                    wardNumberBinding.roUserName.setText("");
                    wardNumberBinding.ronameTv.setVisibility(View.GONE);
                    wardNumberBinding.RONameLayout.setVisibility(View.GONE);
                    wardNumberBinding.phoneNo.setText("");
                    wardNumberBinding.wardTv.setVisibility(View.GONE);
                    wardNumberBinding.pollingStationName.setVisibility(View.GONE);
                } else if (position == 2) {
                    isPanchayatUnion = false;
                    isMunicipality = true;
                    isTownPanchayat = false;
                    isCorporation = false;
                    wardNumberBinding.selectBlockTv.setText("Municipality");
                    loadOfflineDistrictListDBValues();

                    wardNumberBinding.blockSpinner.setAdapter(null);
                    wardNumberBinding.roUserName.setText("");
                    wardNumberBinding.ronameTv.setVisibility(View.GONE);
                    wardNumberBinding.RONameLayout.setVisibility(View.GONE);
                    wardNumberBinding.phoneNo.setText("");
                    wardNumberBinding.wardTv.setVisibility(View.GONE);
                    wardNumberBinding.pollingStationName.setVisibility(View.GONE);
                } else if (position == 3) {
                    isPanchayatUnion = false;
                    isMunicipality = false;
                    isTownPanchayat = true;
                    isCorporation = false;
                    wardNumberBinding.selectBlockTv.setText("Town Panchayat");
                    loadOfflineDistrictListDBValues();

                    wardNumberBinding.blockSpinner.setAdapter(null);

                    wardNumberBinding.roUserName.setText("");
                    wardNumberBinding.ronameTv.setVisibility(View.GONE);
                    wardNumberBinding.RONameLayout.setVisibility(View.GONE);
                    wardNumberBinding.phoneNo.setText("");
                    wardNumberBinding.wardTv.setVisibility(View.GONE);
                    wardNumberBinding.pollingStationName.setVisibility(View.GONE);
                } else if (position == 4) {
                    isPanchayatUnion = false;
                    isMunicipality = false;
                    isTownPanchayat = false;
                    isCorporation = true;
                    wardNumberBinding.selectBlockTv.setText("Corporation");
                    loadOfflineDistrictListDBValues();
                    wardNumberBinding.blockSpinner.setAdapter(null);
                    wardNumberBinding.roUserName.setText("");
                    wardNumberBinding.ronameTv.setVisibility(View.GONE);
                    wardNumberBinding.RONameLayout.setVisibility(View.GONE);
                    wardNumberBinding.phoneNo.setText("");
                    wardNumberBinding.wardTv.setVisibility(View.GONE);
                    wardNumberBinding.pollingStationName.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        wardNumberBinding.districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    pref_district = District.get(position).getDistrictName();
                    prefManager.setDistrictName(pref_district);
//                    Log.d("name", "" + wardNumberBinding.ruralUrbanSpinner.getSelectedItem());
                    if (isPanchayatUnion) {
                        wardNumberBinding.blockSpinner.setAdapter(null);
                        wardNumberBinding.phoneNo.setText("");
                        blockFilterSpinner(District.get(position).getDistictCode(), "Panchayat Union");
                        prefManager.setDistrictCode(District.get(position).getDistictCode());
                        prefManager.setDistrictName(District.get(position).getDistrictName());
                        prefManager.setName("Panchayat Union");
                    } else if (isMunicipality) {
                        wardNumberBinding.blockSpinner.setAdapter(null);
                        wardNumberBinding.phoneNo.setText("");
                        blockFilterSpinner(District.get(position).getDistictCode(), "Municipality");
                        prefManager.setDistrictCode(District.get(position).getDistictCode());
                        prefManager.setDistrictName(District.get(position).getDistrictName());
                        prefManager.setName("Municipality");
                    } else if (isTownPanchayat) {
                        wardNumberBinding.blockSpinner.setAdapter(null);
                        wardNumberBinding.phoneNo.setText("");
                        blockFilterSpinner(District.get(position).getDistictCode(), "TownPanchayat");
                        prefManager.setDistrictCode(District.get(position).getDistictCode());
                        prefManager.setDistrictName(District.get(position).getDistrictName());
                        prefManager.setName("TownPanchayat");
                    } else if (isCorporation) {
                        wardNumberBinding.blockSpinner.setAdapter(null);
                        wardNumberBinding.phoneNo.setText("");
                        blockFilterSpinner(District.get(position).getDistictCode(), "Corporation");
                        prefManager.setDistrictCode(District.get(position).getDistictCode());
                        prefManager.setDistrictName(District.get(position).getDistrictName());
                        prefManager.setName("Corporation");
                    } else {
                        wardNumberBinding.blockSpinner.setAdapter(null);
                    }
                } else {
                    wardNumberBinding.selectBlockTv.setVisibility(View.GONE);
                    wardNumberBinding.blockLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        wardNumberBinding.blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String Blockquery = "SELECT * FROM " + RO_USER_TABLE_NAME + " WHERE district_code = " + prefManager.getDistrictCode() + " and localbody_type = '" + prefManager.getName() + "' order by localbody_name asc";
                    Log.d("BlockQuery", "" + Blockquery);
                    Cursor BlockList = db.rawQuery(Blockquery, null);
                    if (BlockList.getCount() > 0) {
                        wardNumberBinding.ronameTv.setVisibility(View.VISIBLE);
                        wardNumberBinding.RONameLayout.setVisibility(View.VISIBLE);
                        wardNumberBinding.wardTv.setVisibility(View.VISIBLE);
                        wardNumberBinding.pollingStationName.setVisibility(View.VISIBLE);
                        prefManager.setBlockCode(Block.get(position).getLocalBodyNo());
                        ROMobileNo(prefManager.getDistrictCode(), prefManager.getBlockCode(), prefManager.getName());
                    } else {
                        wardNumberBinding.ronameTv.setVisibility(View.GONE);
                        wardNumberBinding.RONameLayout.setVisibility(View.GONE);
                        wardNumberBinding.wardTv.setVisibility(View.GONE);
                        wardNumberBinding.pollingStationName.setVisibility(View.GONE);
                    }

                } else {
                    wardNumberBinding.ronameTv.setVisibility(View.GONE);
                    wardNumberBinding.RONameLayout.setVisibility(View.GONE);
                    wardNumberBinding.wardTv.setVisibility(View.GONE);
                    wardNumberBinding.pollingStationName.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadOfflineDistrictListDBValues();
            }
        }, 2000);


    }

    public void fetchApi() {
        getRODetailsList();
    }

    public void loadOfflineRuralUrbanListDBValues() {
        RuralUrbanList.clear();
        RuralUrbanList.add("Select Rural/Urban");
        RuralUrbanList.add("Panchayat Union");
        RuralUrbanList.add("Municipality");
        RuralUrbanList.add("Town Panchayat");
        RuralUrbanList.add("Corporation");
        ArrayAdapter<String> RuralUrbanArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, RuralUrbanList);
        RuralUrbanArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wardNumberBinding.ruralUrbanSpinner.setAdapter(RuralUrbanArray);
    }

    public void loadOfflineDistrictListDBValues() {
        String Distquery = "Select distinct district_code, district_name from " + RO_USER_TABLE_NAME + " order by district_name asc";
        Log.d("DistQuery", "" + Distquery);
        Cursor DistrictList = db.rawQuery(Distquery, null);
        District.clear();
        ElectionWardNoCall electionWardNoCall = new ElectionWardNoCall();
        electionWardNoCall.setDistrictName("Select District");
        District.add(electionWardNoCall);
        if (DistrictList.getCount() > 0) {
            if (DistrictList.moveToFirst()) {
                do {
                    ElectionWardNoCall districtList = new ElectionWardNoCall();
                    String districtCode = DistrictList.getString(DistrictList.getColumnIndexOrThrow(AppConstant.RO_DISTRICT_CODE));
                    String districtName = DistrictList.getString(DistrictList.getColumnIndexOrThrow(AppConstant.RO_DISTRICT_NAME));
                    districtList.setDistictCode(districtCode);
                    districtList.setDistrictName(districtName);
                    District.add(districtList);
                } while (DistrictList.moveToNext());
            }
        }
        wardNumberBinding.districtSpinner.setAdapter(new CommonAdapter(this, District, "DistrictList"));
    }

    public void blockFilterSpinner(String filterBlock, String localBodyType) {
        String DisBlockquery = "SELECT * FROM " + RO_USER_TABLE_NAME + " WHERE district_code = " + filterBlock + " and localbody_type = '" + localBodyType + "' order by localbody_name asc";
        Log.d("DisBloCodeQuery", "" + DisBlockquery);
        Cursor BlockList = db.rawQuery(DisBlockquery, null);

        ElectionWardNoCall blockListValue = new ElectionWardNoCall();
        if (localBodyType.equalsIgnoreCase("Panchayat Union")) {
            Block.clear();
            if (BlockList.getCount() > 0) {
                wardNumberBinding.selectBlockTv.setVisibility(View.VISIBLE);
                wardNumberBinding.blockLayout.setVisibility(View.VISIBLE);
                blockListValue.setLocalBodyName("Select Local Body Name");
            } else {
                wardNumberBinding.selectBlockTv.setVisibility(View.GONE);
                wardNumberBinding.blockLayout.setVisibility(View.GONE);
                Utils.showAlert(this, "There is no Panchayat Union in ".concat(prefManager.getDistrictName()) + " !");
            }
        } else if (localBodyType.equalsIgnoreCase("Municipality")) {
            Block.clear();
            if (BlockList.getCount() > 0) {
                wardNumberBinding.selectBlockTv.setVisibility(View.VISIBLE);
                wardNumberBinding.blockLayout.setVisibility(View.VISIBLE);
                blockListValue.setLocalBodyName("Select Municipality");
            } else {
                wardNumberBinding.selectBlockTv.setVisibility(View.GONE);
                wardNumberBinding.blockLayout.setVisibility(View.GONE);
                Utils.showAlert(this, "There is no Municipality in ".concat(prefManager.getDistrictName()) + " !");
            }
        } else if (localBodyType.equalsIgnoreCase("TownPanchayat")) {
            Block.clear();
            if (BlockList.getCount() > 0) {
                wardNumberBinding.selectBlockTv.setVisibility(View.VISIBLE);
                wardNumberBinding.blockLayout.setVisibility(View.VISIBLE);
                blockListValue.setLocalBodyName("Select Town Panchayat");
            } else {
                wardNumberBinding.selectBlockTv.setVisibility(View.GONE);
                wardNumberBinding.blockLayout.setVisibility(View.GONE);
                Utils.showAlert(this, "There is no Town Panchayat in ".concat(prefManager.getDistrictName()) + " !");
            }
        } else if (localBodyType.equalsIgnoreCase("Corporation")) {
            Block.clear();
            if (BlockList.getCount() > 0) {
                wardNumberBinding.selectBlockTv.setVisibility(View.VISIBLE);
                wardNumberBinding.blockLayout.setVisibility(View.VISIBLE);
                blockListValue.setLocalBodyName("Select Corporation");
            } else {
                wardNumberBinding.selectBlockTv.setVisibility(View.GONE);
                wardNumberBinding.blockLayout.setVisibility(View.GONE);
                Utils.showAlert(this, "There is no Corporation in ".concat(prefManager.getDistrictName()) + " !");
            }
        }
        Block.add(blockListValue);
        if (BlockList.getCount() > 0) {
            if (BlockList.moveToFirst()) {
                do {
                    ElectionWardNoCall blockList = new ElectionWardNoCall();
                    String districtCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.RO_DISTRICT_CODE));
                    String blockCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.LOCALBODY_NO));
                    String blockName = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.LOCALBODY_NAME));
                    blockList.setDistictCode(districtCode);
                    blockList.setLocalBodyNo(blockCode);
                    blockList.setLocalBodyName(blockName);
                    Block.add(blockList);
                } while (BlockList.moveToNext());
            }
        }
        if (localBodyType.equalsIgnoreCase("Panchayat Union")) {
            wardNumberBinding.blockSpinner.setAdapter(new CommonAdapter(this, Block, "BlockList"));
        } else if (localBodyType.equalsIgnoreCase("Municipality")) {
            wardNumberBinding.blockSpinner.setAdapter(new CommonAdapter(this, Block, "MunicipalityList"));
        } else if (localBodyType.equalsIgnoreCase("TownPanchayat")) {
            wardNumberBinding.blockSpinner.setAdapter(new CommonAdapter(this, Block, "TownPanchayatList"));
        } else if (localBodyType.equalsIgnoreCase("Corporation")) {
            wardNumberBinding.blockSpinner.setAdapter(new CommonAdapter(this, Block, "CorporationList"));
        }

    }

    public void ROMobileNo(String filterBlock, String localBodyNo, String localBodyType) {
        String RoUserquery = "SELECT * FROM " + RO_USER_TABLE_NAME + " WHERE district_code = " + filterBlock + " and localbody_no = " + localBodyNo + " and localbody_type = '" + localBodyType + "'";
        Log.d("RoUserquery", "" + RoUserquery);
        Cursor BlockList = db.rawQuery(RoUserquery, null);

        if (BlockList.getCount() > 0) {

            if (BlockList.moveToFirst()) {
                do {
                    ElectionWardNoCall blockList = new ElectionWardNoCall();
                    String districtCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.RO_DISTRICT_CODE));
                    String blockCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.LOCALBODY_NO));
                    String blockName = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.LOCALBODY_NAME));
                    String userName = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.RO_USER_NAME));
                    String mobileNo = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.RO_MOBILE_NO));
                    if (userName != null) {
                        wardNumberBinding.roUserName.setText(userName);
                    } else {
                        Log.d("roname", "" + userName);
                        wardNumberBinding.roUserName.setText("NA");
                    }
                    if (!mobileNo.equalsIgnoreCase("")) {
                        wardNumberBinding.phoneNo.setText(mobileNo);
                    } else {
                        wardNumberBinding.phoneNo.setText("NA");
                    }
                    blockList.setDistictCode(districtCode);
                    blockList.setLocalBodyNo(blockCode);
                    blockList.setLocalBodyName(blockName);
                    blockList.setRoMobileNo(mobileNo);
                } while (BlockList.moveToNext());
            }
        }

    }

    public void call() {
        validateDetails();
    }

    public void validateDetails() {
        if (!"Select Rural/urban".equalsIgnoreCase(RuralUrbanList.get(wardNumberBinding.ruralUrbanSpinner.getSelectedItemPosition()))) {
            if (!"Select District".equalsIgnoreCase(District.get(wardNumberBinding.districtSpinner.getSelectedItemPosition()).getDistrictName())) {
                if (isPanchayatUnion) {
                    if (!"Select Local Body Name".equalsIgnoreCase(Block.get(wardNumberBinding.blockSpinner.getSelectedItemPosition()).getLocalBodyName())) {
                        if (isPermissionGranted()) {
                            permissionGrantedMethod();
                        }
                    } else {
                        Utils.showAlert(this, "Select Local Body Name!");
                    }
                } else if (isMunicipality) {
                    if (!"Select Municipality".equalsIgnoreCase(Block.get(wardNumberBinding.blockSpinner.getSelectedItemPosition()).getLocalBodyName())) {
                        if (isPermissionGranted()) {
                            permissionGrantedMethod();
                        }
                    } else {
                        Utils.showAlert(this, "Select Municipality!");
                    }
                } else if (isTownPanchayat) {
                    if (!"Select Town Panchayat".equalsIgnoreCase(Block.get(wardNumberBinding.blockSpinner.getSelectedItemPosition()).getLocalBodyName())) {
                        if (isPermissionGranted()) {
                            permissionGrantedMethod();
                        }
                    } else {
                        Utils.showAlert(this, "Select TownPanchayat!");
                    }
                } else if (isCorporation) {
                    if (!"Select Corporation".equalsIgnoreCase(Block.get(wardNumberBinding.blockSpinner.getSelectedItemPosition()).getLocalBodyName())) {
                        if (isPermissionGranted()) {
                            permissionGrantedMethod();
                        }
                    } else {
                        Utils.showAlert(this, "Select Corporation!");
                    }
                }
            } else {
                Utils.showAlert(this, "Select District!");
            }
        } else {
            Utils.showAlert(this, "Select Rural/urban!");
        }

    }

    public void permissionGrantedMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            call_action();

        }
    }

    public boolean isPermissionGranted() {
        String phoneNO = wardNumberBinding.phoneNo.getText().toString();
        if (phoneNO.isEmpty()) {
            return false;
        }
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
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String phoneNO = wardNumberBinding.phoneNo.getText().toString();
        if (!phoneNO.equalsIgnoreCase("NA")) {
            Log.d("MobileNo", "" + phoneNO);
            if (Utils.isValidMobile(phoneNO)) {
                Log.d("MobileNo", "" + phoneNO);
                callIntent.setData(Uri.parse("tel:" + phoneNO));
                startActivity(callIntent);
            } else {
                Utils.showAlert(this, "Enter the Valid Mobile No");
            }
        } else {
            Utils.showAlert(this, "Call Not Available!");
        }


    }


    public void getRODetailsList() {
        try {
            new ApiService(this).makeJSONObjectRequest("RODetailsList", Api.Method.POST, UrlGenerator.getWorkListUrl(), roUserDetailsListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public JSONObject roUserDetailsListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.roDetailsListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("roUserDetailsList", "" + authKey);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();

            if ("RODetailsList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertROUserDetailsTask().execute(jsonObject);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadOfflineRuralUrbanListDBValues();
                        loadOfflineDistrictListDBValues();
                    }
                }, 2000);

                Log.d("RODetailsList", "" + responseDecryptedBlockKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }


    public class InsertROUserDetailsTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<ElectionWardNoCall> villagelist_count = dbData.getAll_ROUSerDetails();
            if (villagelist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ElectionWardNoCall roUserDetails = new ElectionWardNoCall();
                        try {
                            roUserDetails.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.RO_DISTRICT_CODE));
                            roUserDetails.setDistrictName(jsonArray.getJSONObject(i).getString(AppConstant.RO_DISTRICT_NAME));
                            roUserDetails.setLocalBodyNo(jsonArray.getJSONObject(i).getString(AppConstant.LOCALBODY_NO));
                            roUserDetails.setLocalBodyName(jsonArray.getJSONObject(i).getString(AppConstant.LOCALBODY_NAME));
                            roUserDetails.setRoUserName(jsonArray.getJSONObject(i).getString(AppConstant.RO_USER_NAME));
                            roUserDetails.setRoMobileNo(jsonArray.getJSONObject(i).getString(AppConstant.RO_MOBILE_NO));
                            roUserDetails.setLocalBodyType(jsonArray.getJSONObject(i).getString(AppConstant.LOCALBODY_TYPE));
                            roUserDetails.setLocalBodyAbbr(jsonArray.getJSONObject(i).getString(AppConstant.LOCALBODY_ABBR));

                            dbData.insertROUserDetails(roUserDetails);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            clearAnimations();
            if (progressHUD != null) {
                progressHUD.cancel();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(ElectionWardNumberScreen.this, "Downloading", true, false, null);
        }
    }


    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        } else {

            Intent intent = new Intent(this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    }

    public void refreshScreenCallApi() {
        if (Utils.isOnline()) {
            deleteRefreshTable();
            setAnimationView();
            fetchApi();
        } else {
            Utils.showAlert(this, getResources().getString(R.string.no_internet));
        }
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


    public void closeApplication() {
        if(!Utils.isOnline()) {
            Utils.showAlert(this,"Logging out while offline may leads to loss of data!");
        }else {
            new MyDialog(this).exitDialog(this, "Are you sure you want to Logout?", "Logout");
        }
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


    public void deleteRefreshTable() {
        dbData.open();
        dbData.deleteAllTables();
    }

    public void setAnimationView() {
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        wardNumberBinding.refresh.startAnimation(rotation);
    }

    public void clearAnimations() {
        wardNumberBinding.refresh.clearAnimation();
    }
}
