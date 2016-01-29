package com.humanize.android.data;

/**
 * Created by kamal on 1/28/16.
 */
public class UserDevice {

    private String deviceId;
    private String phoneType;
    private String networkOperatorName;
    private String networkType;
    private String simOperatorName;
    private String manufacturer;
    private String model;
    private String sdkVersion;
    private String gcmRegistrationToken;

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNetworkOperatorName() {
        return networkOperatorName;
    }

    public void setNetworkOperatorName(String networkOperatorName) {
        this.networkOperatorName = networkOperatorName;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getSimOperatorName() {
        return simOperatorName;
    }

    public void setSimOperatorName(String simOperatorName) {
        this.simOperatorName = simOperatorName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getGcmRegistrationToken() {
        return gcmRegistrationToken;
    }

    public void setGcmRegistrationToken(String gcmRegistrationToken) {
        this.gcmRegistrationToken = gcmRegistrationToken;
    }
}