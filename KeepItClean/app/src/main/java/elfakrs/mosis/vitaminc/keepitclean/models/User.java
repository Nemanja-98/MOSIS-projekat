package elfakrs.mosis.vitaminc.keepitclean.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    String username;
    String password;
    String name;
    String surname;
    String phoneNumber;
    String imageUrl;
    int loggedIn;
    double lng;
    double lat;
    int points;

    public User() {
    }

    public User(String username, String password, String name, String surname, String phoneNumber, String imageUrl, int loggedIn, double lng, double lat, int points) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.loggedIn = loggedIn;
        this.lng = lng;
        this.lat = lat;
        this.points = points;
    }

    protected User(Parcel in) {
        username = in.readString();
        password = in.readString();
        name = in.readString();
        surname = in.readString();
        phoneNumber = in.readString();
        imageUrl = in.readString();
        loggedIn = in.readInt();
        lng = in.readDouble();
        lat = in.readDouble();
        points = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(phoneNumber);
        dest.writeString(imageUrl);
        dest.writeInt(loggedIn);
        dest.writeDouble(lng);
        dest.writeDouble(lat);
        dest.writeInt(points);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(int loggedIn) {
        this.loggedIn = loggedIn;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
