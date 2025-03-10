package org.max.home.accuweather.location;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ParentCity {
    @JsonProperty("Key")
    public String key;
    @JsonProperty("LocalizedName")
    public String localizedName;
    @JsonProperty("EnglishName")
    public String englishName;
}