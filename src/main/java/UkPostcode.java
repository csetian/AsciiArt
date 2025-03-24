import java.util.List;

public class UkPostcode {
    private Integer id;
    private String postCode;
    private Double latitude;
    private Double longitude;

    public UkPostcode(final String id, final String postCode, final String latitude, final String longitude) {
        this.id = Integer.valueOf(id);
        this.postCode = postCode;
        this.latitude = Double.valueOf(latitude);
        this.longitude = Double.valueOf(longitude);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }



}
