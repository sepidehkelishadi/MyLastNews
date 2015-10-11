package com.pishtaz.mylastnews.models;

/**
 * Created by sepideh on 5/24/2015.
 */
public class CountryTO {

    private String CountryID;
    private String Name;
    private String CountryCallingCode;
    private String InternationalCallPrefix;
    private String TrunkPrefix;


    public String getCountryID() {
        return CountryID;
    }

    public void setCountryID(String countryID) {
        CountryID = countryID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCountryCallingCode() {
        return CountryCallingCode;
    }

    public void setCountryCallingCode(String countryCallingCode) {
        CountryCallingCode = countryCallingCode;
    }

    public String getInternationalCallPrefix() {
        return InternationalCallPrefix;
    }

    public void setInternationalCallPrefix(String internationalCallPrefix) {
        InternationalCallPrefix = internationalCallPrefix;
    }

    public String getTrunkPrefix() {
        return TrunkPrefix;
    }

    public void setTrunkPrefix(String trunkPrefix) {
        TrunkPrefix = trunkPrefix;
    }
}
