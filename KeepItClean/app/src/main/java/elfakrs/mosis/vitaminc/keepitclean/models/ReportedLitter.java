package elfakrs.mosis.vitaminc.keepitclean.models;

import java.util.ArrayList;

public class ReportedLitter {
    private String title;
    private String type;
    private String creator;
    private double lat;
    private double lng;
    private String description;
    private ArrayList<String> attenders;

    public ReportedLitter() {
        this.attenders = new ArrayList<String>();
    }

    public ReportedLitter(String title, String type, String creator, String description, double lat, double lng) {
        this.title = title;
        this.type = type;
        this.creator = creator;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.attenders = new ArrayList<String>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public ArrayList<String> getAttenders() {
        return attenders;
    }
}
