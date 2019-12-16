package papb.project.hisam.five_baru;


import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public class Event {
    private String Name, Time, FotoUrl, Desc, Kota;
    private double lon, lat;

    public Event(String name, String kota, String fotoUrl, String time, String desc, double lon, double lat) {
        this.Name = name;
        this.Kota = kota;
        this.FotoUrl = fotoUrl;
        this.Time = time;
        this.Desc = desc;
        this.lon = lon;
        this.lat = lat;
    }

    public Event(){

    }

    public String getName() {
        return Name;
    }

    public String getTime() {
        return Time;
    }

    public String getFotoUrl() {
        return FotoUrl;
    }

    public String getDesc() {
        return Desc;
    }

    public String getKota() {
        return Kota;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setFotoUrl(String fotoUrl) {
        FotoUrl = fotoUrl;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public void setKota(String kota) {
        Kota = kota;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

}

