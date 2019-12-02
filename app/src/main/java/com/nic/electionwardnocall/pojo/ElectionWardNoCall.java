package com.nic.electionwardnocall.pojo;

/**
 * Created by AchanthiSundar on 01-11-2017.
 */

public class ElectionWardNoCall {

    private String distictCode;
    private String districtName;

    private String blockCode;

    private String SchemeName;

    private String selectedBlockCode;

    private String FinancialYear;

    private String PvCode;
    private String PvName;

    private String blockName;


    private Integer WorkId;
    private String typeOfWork;
    private String imageRemark;
    private String dateTime;
    private String imageAvailable;
    private String createdDate;
    private String workTypeCode;


    public String getPvName() {
        return PvName;
    }

    public void setPvName(String pvName) {
        PvName = pvName;
    }


    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }


    public String getDistictCode() {
        return distictCode;
    }

    public void setDistictCode(String distictCode) {
        this.distictCode = distictCode;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getSelectedBlockCode() {
        return selectedBlockCode;
    }

    public void setSelectedBlockCode(String selectedBlockCode) {
        this.selectedBlockCode = selectedBlockCode;
    }
    public String getPvCode() {
        return PvCode;
    }

    public void setPvCode(String pvCode) {
        this.PvCode = pvCode;
    }


}