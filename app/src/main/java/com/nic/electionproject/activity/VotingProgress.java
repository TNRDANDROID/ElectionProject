package com.nic.electionproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.electionproject.R;
import com.nic.electionproject.Session.PrefManager;
import com.nic.electionproject.api.Api;
import com.nic.electionproject.api.ServerResponse;
import com.nic.electionproject.databinding.VotingProgressBinding;
import com.nic.electionproject.utils.Utils;
import com.nic.electionproject.windowpreferences.WindowPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class VotingProgress extends AppCompatActivity implements Api.ServerResponseListener {
    public VotingProgressBinding votingProgressBinding;
    public PrefManager prefManager;
    private List<String> DatePhaseList = new ArrayList<>();
    private List<String> TimePhaseList = new ArrayList<>();
    boolean isPollCommenced7Am;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowPreferencesManager windowPreferencesManager = new WindowPreferencesManager(this);
        windowPreferencesManager.applyEdgeToEdgePreference(getWindow());
        votingProgressBinding = DataBindingUtil.setContentView(this, R.layout.voting_progress);
        votingProgressBinding.setActivity(this);
        prefManager = new PrefManager(this);
        votingProgressBinding.districtUserLayout.setTranslationX(800);
        votingProgressBinding.blockUserLayout.setTranslationX(800);

        votingProgressBinding.selectRuralUrbanTv.setTranslationX(800);
        votingProgressBinding.ruralUrbanLayout.setTranslationX(800);

        votingProgressBinding.selectDistrictTv.setTranslationX(800);
        votingProgressBinding.districtLayout.setTranslationX(800);

        votingProgressBinding.boothTitle.setTranslationX(800);
        votingProgressBinding.boothLayout.setTranslationX(800);

        votingProgressBinding.polledTitle.setTranslationX(800);
        votingProgressBinding.polledLayout.setTranslationX(800);

        votingProgressBinding.save.setTranslationX(800);


        votingProgressBinding.districtUserLayout.setAlpha(0);
        votingProgressBinding.blockUserLayout.setAlpha(0);

        votingProgressBinding.selectRuralUrbanTv.setAlpha(0);
        votingProgressBinding.ruralUrbanLayout.setAlpha(0);

        votingProgressBinding.selectDistrictTv.setAlpha(0);
        votingProgressBinding.districtLayout.setAlpha(0);

        votingProgressBinding.boothTitle.setAlpha(0);
        votingProgressBinding.boothLayout.setAlpha(0);

        votingProgressBinding.polledTitle.setAlpha(0);
        votingProgressBinding.polledLayout.setAlpha(0);

        votingProgressBinding.save.setAlpha(0);


        votingProgressBinding.districtUserLayout.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        votingProgressBinding.blockUserLayout.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();

        votingProgressBinding.selectRuralUrbanTv.animate().translationX(0).alpha(1).setDuration(1200).setStartDelay(800).start();
        votingProgressBinding.ruralUrbanLayout.animate().translationX(0).alpha(1).setDuration(1400).setStartDelay(1000).start();

        votingProgressBinding.selectDistrictTv.animate().translationX(0).alpha(1).setDuration(1600).setStartDelay(1200).start();
        votingProgressBinding.districtLayout.animate().translationX(0).alpha(1).setDuration(1800).setStartDelay(1400).start();

        votingProgressBinding.boothTitle.animate().translationX(0).alpha(1).setDuration(2000).setStartDelay(1600).start();
        votingProgressBinding.boothLayout.animate().translationX(0).alpha(1).setDuration(2200).setStartDelay(1800).start();

        votingProgressBinding.polledTitle.animate().translationX(0).alpha(1).setDuration(2400).setStartDelay(2000).start();
        votingProgressBinding.polledLayout.animate().translationX(0).alpha(1).setDuration(2600).setStartDelay(2200).start();

        votingProgressBinding.save.animate().translationX(0).alpha(1).setDuration(2800).setStartDelay(2400).start();

        votingProgressBinding.timePhaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position <= 1) {
                    isPollCommenced7Am = true;
                    votingProgressBinding.NoOfMalesInBooth.setEnabled(true);
                    votingProgressBinding.NoOfFemalesInBooth.setEnabled(true);
                    votingProgressBinding.NoOfOthersInBooth.setEnabled(true);

                } else {
                    isPollCommenced7Am = false;
                    votingProgressBinding.NoOfMalesInBooth.getText().clear();
                    votingProgressBinding.NoOfFemalesInBooth.getText().clear();
                    votingProgressBinding.NoOfOthersInBooth.getText().clear();
                    votingProgressBinding.NoOfMalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfFemalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfOthersInBooth.setEnabled(false);

                } /*else if (position == 2) {
                    isPollCommenced7Am = false;
                    votingProgressBinding.NoOfMalesInBooth.getText().clear();
                    votingProgressBinding.NoOfFemalesInBooth.getText().clear();
                    votingProgressBinding.NoOfOthersInBooth.getText().clear();
                    votingProgressBinding.NoOfMalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfFemalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfOthersInBooth.setEnabled(false);

                } else if (position == 3) {
                    isPollCommenced7Am = false;
                    votingProgressBinding.NoOfMalesInBooth.getText().clear();
                    votingProgressBinding.NoOfFemalesInBooth.getText().clear();
                    votingProgressBinding.NoOfOthersInBooth.getText().clear();
                    votingProgressBinding.NoOfMalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfFemalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfOthersInBooth.setEnabled(false);

                } else if (position == 4) {
                    isPollCommenced7Am = false;
                    votingProgressBinding.NoOfMalesInBooth.getText().clear();
                    votingProgressBinding.NoOfFemalesInBooth.getText().clear();
                    votingProgressBinding.NoOfOthersInBooth.getText().clear();
                    votingProgressBinding.NoOfMalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfFemalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfOthersInBooth.setEnabled(false);

                } else if (position == 5) {
                    isPollCommenced7Am = false;
                    votingProgressBinding.NoOfMalesInBooth.getText().clear();
                    votingProgressBinding.NoOfFemalesInBooth.getText().clear();
                    votingProgressBinding.NoOfOthersInBooth.getText().clear();
                    votingProgressBinding.NoOfMalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfFemalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfOthersInBooth.setEnabled(false);
                }
                else if (position == 6) {
                    isPollCommenced7Am = false;
                    votingProgressBinding.NoOfMalesInBooth.getText().clear();
                    votingProgressBinding.NoOfFemalesInBooth.getText().clear();
                    votingProgressBinding.NoOfOthersInBooth.getText().clear();
                    votingProgressBinding.NoOfMalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfFemalesInBooth.setEnabled(false);
                    votingProgressBinding.NoOfOthersInBooth.setEnabled(false);
                }*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loadDatePhaseListValues();
        loadTimeListValues();

        votingProgressBinding.NoOfMalesInBooth.addTextChangedListener(votesInBooth);
        votingProgressBinding.NoOfFemalesInBooth.addTextChangedListener(votesInBooth);
        votingProgressBinding.NoOfOthersInBooth.addTextChangedListener(votesInBooth);

        votingProgressBinding.NoOfMalesInBoothPolled.addTextChangedListener(votesInBoothPolled);
        votingProgressBinding.NoOfFemalesInBoothPolled.addTextChangedListener(votesInBoothPolled);
        votingProgressBinding.NoOfOthersInBoothPolled.addTextChangedListener(votesInBoothPolled);

    }

    TextWatcher votesInBooth = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!TextUtils.isEmpty(votingProgressBinding.NoOfMalesInBooth.getText().toString().trim())
                    || !TextUtils.isEmpty(votingProgressBinding.NoOfFemalesInBooth.getText().toString().trim())
                    || !TextUtils.isEmpty(votingProgressBinding.NoOfOthersInBooth.getText().toString().trim())) {

                int firtValue = TextUtils.isEmpty(votingProgressBinding.NoOfMalesInBooth.getText().toString().trim()) ? 0 : Integer.parseInt(votingProgressBinding.NoOfMalesInBooth.getText().toString().trim());
                int secondValue = TextUtils.isEmpty(votingProgressBinding.NoOfFemalesInBooth.getText().toString().trim()) ? 0 : Integer.parseInt(votingProgressBinding.NoOfFemalesInBooth.getText().toString().trim());
                int thirdValue = TextUtils.isEmpty(votingProgressBinding.NoOfOthersInBooth.getText().toString().trim()) ? 0 : Integer.parseInt(votingProgressBinding.NoOfOthersInBooth.getText().toString().trim());


                int answer = firtValue + secondValue + thirdValue;

                Log.e("RESULT", String.valueOf(answer));
                votingProgressBinding.NoOfTotalInBooth.setText(String.valueOf(answer));
            } else {
                votingProgressBinding.NoOfTotalInBooth.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    TextWatcher votesInBoothPolled = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!TextUtils.isEmpty(votingProgressBinding.NoOfMalesInBoothPolled.getText().toString().trim())
                    || !TextUtils.isEmpty(votingProgressBinding.NoOfFemalesInBoothPolled.getText().toString().trim())
                    || !TextUtils.isEmpty(votingProgressBinding.NoOfOthersInBoothPolled.getText().toString().trim())) {

                int firtValue = TextUtils.isEmpty(votingProgressBinding.NoOfMalesInBoothPolled.getText().toString().trim()) ? 0 : Integer.parseInt(votingProgressBinding.NoOfMalesInBoothPolled.getText().toString().trim());
                int secondValue = TextUtils.isEmpty(votingProgressBinding.NoOfFemalesInBoothPolled.getText().toString().trim()) ? 0 : Integer.parseInt(votingProgressBinding.NoOfFemalesInBoothPolled.getText().toString().trim());
                int thirdValue = TextUtils.isEmpty(votingProgressBinding.NoOfOthersInBoothPolled.getText().toString().trim()) ? 0 : Integer.parseInt(votingProgressBinding.NoOfOthersInBoothPolled.getText().toString().trim());


                int answer = firtValue + secondValue + thirdValue;

                Log.e("RESULT", String.valueOf(answer));
                votingProgressBinding.NoOfTotalInBoothPolled.setText(String.valueOf(answer));
            } else {
                votingProgressBinding.NoOfTotalInBoothPolled.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void loadDatePhaseListValues() {
        DatePhaseList.clear();
        DatePhaseList.add("Select Date");
        DatePhaseList.add("23-01-2020(Phase I)");
        DatePhaseList.add("30-02-2020(Phase II)");
        ArrayAdapter<String> RuralUrbanArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatePhaseList);
        RuralUrbanArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        votingProgressBinding.datePhaseSpinner.setAdapter(RuralUrbanArray);
    }

    public void loadTimeListValues() {
        TimePhaseList.clear();
        TimePhaseList.add("Select Time");
        TimePhaseList.add("Poll Commenced (07:00 AM)");
        TimePhaseList.add("Upto (09:00 AM)");
        TimePhaseList.add("Upto (11:00 AM)");
        TimePhaseList.add("Upto (01:00 PM)");
        TimePhaseList.add("Upto (03:00 PM)");
        TimePhaseList.add("End of Poll");
        ArrayAdapter<String> RuralUrbanArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TimePhaseList);
        RuralUrbanArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        votingProgressBinding.timePhaseSpinner.setAdapter(RuralUrbanArray);
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void validateVotingProgress() {
        if (!"Select Date".equalsIgnoreCase(DatePhaseList.get(votingProgressBinding.datePhaseSpinner.getSelectedItemPosition()))) {
            if (!"Select Time".equalsIgnoreCase(TimePhaseList.get(votingProgressBinding.timePhaseSpinner.getSelectedItemPosition()))) {
                if (isPollCommenced7Am) {
                    if (!votingProgressBinding.NoOfMalesInBooth.getText().toString().isEmpty()) {
                        if (!votingProgressBinding.NoOfFemalesInBooth.getText().toString().isEmpty()) {
                            if (!votingProgressBinding.NoOfOthersInBooth.getText().toString().isEmpty()) {
                                noOfVotesPolledValidation();
                            } else {
                                Utils.showAlert(this, "Enter the no of Other voters in contested election office!");
                            }
                        } else {
                            Utils.showAlert(this, "Enter the no of Female voters in contested election office!");
                        }
                    } else {
                        Utils.showAlert(this, "Enter the no of Male voters in contested election office!");
                    }
                } else {
                    noOfVotesPolledValidation();
                }

            } else {
                Utils.showAlert(this, "Select Time!");
            }
        } else {
            Utils.showAlert(this, "Select Date!");
        }

    }

    public void noOfVotesPolledValidation() {
        if (!votingProgressBinding.NoOfMalesInBoothPolled.getText().toString().isEmpty()) {
            if (!votingProgressBinding.NoOfFemalesInBoothPolled.getText().toString().isEmpty()) {
                if (!votingProgressBinding.NoOfOthersInBoothPolled.getText().toString().isEmpty()) {

                } else {
                    Utils.showAlert(this, "Enter the no of Other voters polled!");
                }
            } else {
                Utils.showAlert(this, "Enter the no of Female voters polled!");
            }
        } else {
            Utils.showAlert(this, "Enter the no of Male voters polled!");
        }
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
