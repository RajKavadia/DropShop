package com.DropShop;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("productId")
    @Expose
    public String productId;
    @SerializedName("customerId")
    @Expose
    public String customerId;
    @SerializedName("brandCode")
    @Expose
    public String brandCode;
    @SerializedName("brandName")
    @Expose
    public String brandName;
    @SerializedName("productCode")
    @Expose
    public Integer productCode;
    @SerializedName("productDesc")
    @Expose
    public String productDesc;
    @SerializedName("mrp")
    @Expose
    public Integer mrp;
    @SerializedName("expiry")
    @Expose
    public Integer expiry;

}