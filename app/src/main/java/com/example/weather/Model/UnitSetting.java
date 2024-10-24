package com.example.weather.Model;

public class UnitSetting {
    String temperatureUnit;

    public UnitSetting(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }
}
