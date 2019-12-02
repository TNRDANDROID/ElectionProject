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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.nic.electionwardnocall.utils.UrlGenerator;
import com.nic.electionwardnocall.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.nic.electionwardnocall.DataBase.DBHelper.BLOCK_TABLE_NAME;
import static com.nic.electionwardnocall.DataBase.DBHelper.DISTRICT_TABLE_NAME;
import static com.nic.electionwardnocall.DataBase.DBHelper.VILLAGE_TABLE_NAME;
import static com.nic.electionwardnocall.utils.Utils.blockListJsonParams;
import static com.nic.electionwardnocall.utils.Utils.districtListJsonParams;
import static com.nic.electionwardnocall.utils.Utils.villageListJsonParams;


public class ElectionWardNumberScreen extends AppCompatActivity implements MyDialog.myOnClickListener, Api.ServerResponseListener {

    private WardNumberBinding wardNumberBinding;
    private List<String> RuralUrbanList = new ArrayList<>();
    private List<ElectionWardNoCall> District = new ArrayList<>();
    private List<ElectionWardNoCall> Block = new ArrayList<>();
    private List<ElectionWardNoCall> Village = new ArrayList<>();
    public dbData dbData = new dbData(this);
    private SQLiteDatabase db;
    public static DBHelper dbHelper;
    Animation smalltobig;
    final Handler handler = new Handler();
    private PrefManager prefManager;
    String pref_Block, pref_district, pref_Village;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wardNumberBinding = DataBindingUtil.setContentView(this, R.layout.ward_number);
        wardNumberBinding.setActivity(this);

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
        wardNumberBinding.wardTv.setTranslationX(800);
        wardNumberBinding.card.setTranslationX(800);

        wardNumberBinding.selectRuralUrbanTv.setAlpha(0);
        wardNumberBinding.ruralUrbanLayout.setAlpha(0);
        wardNumberBinding.selectDistrictTv.setAlpha(0);
        wardNumberBinding.districtLayout.setAlpha(0);
        wardNumberBinding.selectVillageTv.setAlpha(0);
        wardNumberBinding.villageLayout.setAlpha(0);
        wardNumberBinding.wardTv.setAlpha(0);
        wardNumberBinding.card.setAlpha(0);

        wardNumberBinding.selectRuralUrbanTv.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        wardNumberBinding.ruralUrbanLayout.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        wardNumberBinding.selectDistrictTv.animate().translationX(0).alpha(1).setDuration(1200).setStartDelay(800).start();
        wardNumberBinding.districtLayout.animate().translationX(0).alpha(1).setDuration(1400).setStartDelay(1000).start();
        wardNumberBinding.selectVillageTv.animate().translationX(0).alpha(1).setDuration(1500).setStartDelay(1200).start();
        wardNumberBinding.villageLayout.animate().translationX(0).alpha(1).setDuration(1600).setStartDelay(1400).start();
        wardNumberBinding.wardTv.animate().translationX(0).alpha(1).setDuration(1700).setStartDelay(1600).start();
        wardNumberBinding.card.animate().translationX(0).alpha(1).setDuration(1800).setStartDelay(1800).start();

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
                    wardNumberBinding.selectVillageTv.setText("Village Name");
                    wardNumberBinding.selectBlockTv.setVisibility(View.GONE);
                    wardNumberBinding.blockLayout.setVisibility(View.GONE);
                } else if (position == 1) {
                    wardNumberBinding.selectVillageTv.setText("Village Name");
                    wardNumberBinding.selectBlockTv.setVisibility(View.VISIBLE);
                    wardNumberBinding.blockLayout.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    wardNumberBinding.selectVillageTv.setText("Municipality");
                    loadOfflineDistrictListDBValues();
                    wardNumberBinding.villageSpinner.setAdapter(null);
                    wardNumberBinding.selectBlockTv.setVisibility(View.GONE);
                    wardNumberBinding.blockLayout.setVisibility(View.GONE);
                } else if (position == 3) {
                    wardNumberBinding.selectVillageTv.setText("Town Panchayat");
                    loadOfflineDistrictListDBValues();
                    wardNumberBinding.villageSpinner.setAdapter(null);
                    wardNumberBinding.selectBlockTv.setVisibility(View.GONE);
                    wardNumberBinding.blockLayout.setVisibility(View.GONE);
                } else if (position == 4) {
                    wardNumberBinding.selectVillageTv.setText("Corporation");
                    loadOfflineDistrictListDBValues();
                    wardNumberBinding.villageSpinner.setAdapter(null);
                    wardNumberBinding.selectBlockTv.setVisibility(View.GONE);
                    wardNumberBinding.blockLayout.setVisibility(View.GONE);
                } else {
                    wardNumberBinding.selectBlockTv.setVisibility(View.GONE);
                    wardNumberBinding.blockLayout.setVisibility(View.GONE);
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

                }
                pref_district = District.get(position).getDistrictName();
                prefManager.setDistrictName(pref_district);

                blockFilterSpinner(District.get(position).getDistictCode());
                prefManager.setDistrictCode(District.get(position).getDistictCode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        wardNumberBinding.blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                }
                pref_Block = Block.get(position).getBlockName();
                prefManager.setBlockName(pref_Block);
                prefManager.setKeySpinnerSelectedBlockcode(Block.get(position).getBlockCode());

                villageFilterSpinner(Block.get(position).getBlockCode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        wardNumberBinding.villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

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
        getDistrictList();
        getBlockList();
        getVillageList();
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
        Cursor DistrictList = db.rawQuery("Select * from " + DISTRICT_TABLE_NAME + " WHERE dcode != 29 order by dname asc", null);
        District.clear();
        ElectionWardNoCall electionWardNoCall = new ElectionWardNoCall();
        electionWardNoCall.setDistrictName("Select District");
        District.add(electionWardNoCall);
        if (DistrictList.getCount() > 0) {
            if (DistrictList.moveToFirst()) {
                do {
                    ElectionWardNoCall districtList = new ElectionWardNoCall();
                    String districtCode = DistrictList.getString(DistrictList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String districtName = DistrictList.getString(DistrictList.getColumnIndexOrThrow(AppConstant.DISTRICT_NAME));
                    districtList.setDistictCode(districtCode);
                    districtList.setDistrictName(districtName);
                    District.add(districtList);
                } while (DistrictList.moveToNext());
            }
        }
        wardNumberBinding.districtSpinner.setAdapter(new CommonAdapter(this, District, "DistrictList"));
    }

    public void blockFilterSpinner(String filterBlock) {

        Cursor BlockList = db.rawQuery("SELECT * FROM " + BLOCK_TABLE_NAME + " WHERE dcode = " + filterBlock + " order by bname asc", null);
        Block.clear();
        ElectionWardNoCall blockListValue = new ElectionWardNoCall();
        blockListValue.setBlockName("Select Block");
        Block.add(blockListValue);
        if (BlockList.getCount() > 0) {
            if (BlockList.moveToFirst()) {
                do {
                    ElectionWardNoCall blockList = new ElectionWardNoCall();
                    String districtCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String blockName = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.BLOCK_NAME));
                    blockList.setDistictCode(districtCode);
                    blockList.setBlockCode(blockCode);
                    blockList.setBlockName(blockName);
                    Block.add(blockList);
                } while (BlockList.moveToNext());
            }
        }
        wardNumberBinding.blockSpinner.setAdapter(new CommonAdapter(this, Block, "BlockList"));
    }

    public void villageFilterSpinner(String filterVillage) {
        String Query = "SELECT * FROM " + VILLAGE_TABLE_NAME + " WHERE dcode = " + prefManager.getDistrictCode() + " and bcode = " + filterVillage + " order by pvname asc";
        Log.d("villageQuery", "" + Query);
        Cursor VillageList = db.rawQuery(Query, null);
        Village.clear();
        ElectionWardNoCall villageListValue = new ElectionWardNoCall();
        villageListValue.setPvName("Select Village");
        Village.add(villageListValue);
        if (VillageList.getCount() > 0) {
            if (VillageList.moveToFirst()) {
                do {
                    ElectionWardNoCall villageList = new ElectionWardNoCall();
                    String districtCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String pvname = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_NAME));

                    villageList.setDistictCode(districtCode);
                    villageList.setBlockCode(blockCode);
                    villageList.setPvCode(pvCode);
                    villageList.setPvName(pvname);

                    Village.add(villageList);
                    Log.d("spinnersize", "" + Village.size());
                } while (VillageList.moveToNext());
            }
        }
        wardNumberBinding.villageSpinner.setAdapter(new CommonAdapter(this, Village, "VillageList"));
    }

    public void call() {
        if (isPermissionGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                call_action();
            }
        }
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


    public void getDistrictList() {
        try {
            new ApiService(this).makeJSONObjectRequest("DistrictList", Api.Method.POST, UrlGenerator.getOpenUrl(), districtListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getBlockList() {
        try {
            new ApiService(this).makeJSONObjectRequest("BlockList", Api.Method.POST, UrlGenerator.getOpenUrl(), blockListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getVillageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("VillageList", Api.Method.POST, UrlGenerator.getOpenUrl(), villageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status = responseObj.getString(AppConstant.KEY_STATUS);
            String response = responseObj.getString(AppConstant.KEY_RESPONSE);

            if ("DistrictList".equals(urlType) && responseObj != null) {
                if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("OK")) {
                    new InsertDistrictTask().execute(responseObj.getJSONArray(AppConstant.JSON_DATA));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadOfflineDistrictListDBValues();
                        }
                    }, 2000);

                } else if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("NO_RECORD")) {
                    Log.d("Record", responseObj.getString(AppConstant.KEY_MESSAGE));
                }
                Log.d("DistrictList", "" + responseObj.getJSONArray(AppConstant.JSON_DATA));
            }
            if ("BlockList".equals(urlType) && responseObj != null) {
                if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("OK")) {
                    new InsertBlockTask().execute(responseObj.getJSONArray(AppConstant.JSON_DATA));
                } else if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("NO_RECORD")) {
                    Log.d("Record", responseObj.getString(AppConstant.KEY_MESSAGE));
                }
                Log.d("BlockList", "" + responseObj.getJSONArray(AppConstant.JSON_DATA));
            }

            if ("VillageList".equals(urlType) && responseObj != null) {
                if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("OK")) {
                    new InsertVillageTask().execute(responseObj.getJSONArray(AppConstant.JSON_DATA));
                } else if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("NO_RECORD")) {
                    Log.d("Record", responseObj.getString(AppConstant.KEY_MESSAGE));
                }
                Log.d("VillageList", "" + responseObj.getJSONArray(AppConstant.JSON_DATA));
                String authKey = responseObj.getJSONArray(AppConstant.JSON_DATA).toString();
                int maxLogSize = 20000;
                for (int i = 0; i <= authKey.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i + 1) * maxLogSize;
                    end = end > authKey.length() ? authKey.length() : end;
                    Log.v("to_send", authKey.substring(start, end));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class InsertDistrictTask extends AsyncTask<JSONArray, Void, Void> {

        @Override
        protected Void doInBackground(JSONArray... params) {
            dbData.open();
            ArrayList<ElectionWardNoCall> districtlist_count = dbData.getAll_District();
            if (districtlist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray = params[0];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ElectionWardNoCall districtListValue = new ElectionWardNoCall();
                        try {
                            districtListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            districtListValue.setDistrictName(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_NAME));

                            dbData.insertDistrict(districtListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }

    public class InsertBlockTask extends AsyncTask<JSONArray, Void, Void> {

        @Override
        protected Void doInBackground(JSONArray... params) {
            dbData.open();
            ArrayList<ElectionWardNoCall> blocklist_count = dbData.getAll_Block();
            if (blocklist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray = params[0];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ElectionWardNoCall blocktListValue = new ElectionWardNoCall();
                        try {
                            blocktListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            blocktListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            blocktListValue.setBlockName(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_NAME));

                            dbData.insertBlock(blocktListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }

    public class InsertVillageTask extends AsyncTask<JSONArray, Void, Void> {

        @Override
        protected Void doInBackground(JSONArray... params) {
            dbData.open();
            ArrayList<ElectionWardNoCall> villagelist_count = dbData.getAll_Village();
            if (villagelist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray = params[0];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ElectionWardNoCall villageListValue = new ElectionWardNoCall();
                        try {
                            villageListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            villageListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            villageListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            villageListValue.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));

                            dbData.insertVillage(villageListValue);
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
            if (Utils.isOnline()) {
                deleteRefreshTable();
            }
            onBackPressed();
        }
    }

    public void refreshScreenCallApi() {
        if (Utils.isOnline()) {
            setAnimationView();
            deleteRefreshTable();
            fetchApi();
        } else {
            Utils.showAlert(this, getResources().getString(R.string.no_internet));
        }
    }

    public void deleteRefreshTable() {
        dbData.open();
        dbData.deleteAllTables();
        prefManager.clearSession();
        Utils.clearApplicationData(this);
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
