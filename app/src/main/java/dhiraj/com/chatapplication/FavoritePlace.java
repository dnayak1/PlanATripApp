package dhiraj.com.chatapplication;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by dhira on 30-04-2017.
 */

public class FavoritePlace implements Serializable {
    private String placeName;
    private String placeId;
    private double latitude,longitude;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }



    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
