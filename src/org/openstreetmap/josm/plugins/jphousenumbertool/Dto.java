package org.openstreetmap.josm.plugins.jphousenumbertool;

import java.io.Serializable;

/**
 * @author Tom-Konda <https://github.com/tom-konda>
 * 
 * Below author is author for original HouseNumberTool plugin.
 * @author Oliver Raupach 18.01.2012 <http://www.oliver-raupach.de>
 */
public class Dto implements Serializable {

    private static final long serialVersionUID = 5226513739078369787L;

    private boolean saveBuilding = true;
    private boolean saveCountry = true;
    private boolean saveProvince = true;
    private boolean saveCounty = false;
    private boolean saveCity = true;
    private boolean savePostcode = true;
    private boolean saveSuburb = false;
    private boolean saveQuarter = false;
    private boolean saveNeighbourhood = true;
    private boolean saveBlockNumber = true;
    private boolean saveHousenumber = true;

   
    private String building = "yes";
    private String country;
    private String province;
    private String county;
    private String city;
    private String suburb;
    private String quarter;
    private String neighbourhood;
    private String block_number;
    private String postcode;
    private String housenumber;
    private int housenumberChangeValue = 0;

    public boolean isSaveBuilding() {
        return saveBuilding;
    }

    public void setSaveBuilding(boolean saveBuilding) {
        this.saveBuilding = saveBuilding;
    }

    public boolean isSaveCountry() {
        return saveCountry;
    }

    public void setSaveCountry(boolean saveCountry) {
        this.saveCountry = saveCountry;
    }

    public boolean isSaveCounty() {
        return saveCounty;
    }

    public void setSaveProvince(boolean saveProvince) {
        this.saveProvince = saveProvince;
    }

    public boolean isSaveProvince() {
        return saveProvince;
    }

    public void setSaveCounty(boolean saveCounty) {
        this.saveCounty = saveCounty;
    }

    public boolean isSaveCity() {
        return saveCity;
    }

    public void setSaveCity(boolean saveCity) {
        this.saveCity = saveCity;
    }

    public boolean isSaveSuburb() {
        return saveSuburb;
    }

    public void setSaveSuburb(boolean saveSuburb) {
        this.saveSuburb = saveSuburb;
    }

    public boolean isSaveQuarter() {
        return saveQuarter;
    }

    public void setSaveQuarter(boolean saveQuarter) {
        this.saveQuarter = saveQuarter;
    }

    public boolean isSaveNeighbourhood() {
        return saveNeighbourhood;
    }

    public void setSaveNeighbourhood(boolean saveNeighbourhood) {
        this.saveNeighbourhood = saveNeighbourhood;
    }

    public boolean isSaveBlockNumber() {
        return saveBlockNumber;
    }

    public void setSaveBlockNumber(boolean saveBlock_Number) {
        this.saveBlockNumber = saveBlock_Number;
    }

    public boolean isSavePostcode() {
        return savePostcode;
    }

    public void setSavePostcode(boolean savePostcode) {
        this.savePostcode = savePostcode;
    }

    public boolean isSaveHousenumber() {
        return saveHousenumber;
    }

    public void setSaveHousenumber(boolean saveHousenumber) {
        this.saveHousenumber = saveHousenumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

	public String getQuarter() {
	    return quarter;
	}
	
	public void setQuarter(String quarter) {
	    this.quarter = quarter;
	}

	public String getNeighbourhood() {
	    return neighbourhood;
	}
	
	public void setNeighbourhood(String neighbourhood) {
	    this.neighbourhood = neighbourhood;
	}

	public String getBlockNumber() {
	    return block_number;
	}
	
	public void setBlockNumber(String block_number) {
	    this.block_number = block_number;
	}

    public String getHousenumber() {
        return housenumber;
    }

    public void setHousenumber(String housenumber) {
        this.housenumber = housenumber;
    }

    public int getHousenumberChangeValue() {
        return housenumberChangeValue;
    }

    public void setHousenumberChangeValue(int housenumberChangeValue) {
        this.housenumberChangeValue = housenumberChangeValue;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }
}
