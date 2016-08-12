package utils.geotext;

public class Within {
	private String name;
	private double center_lng;
	private double center_lat;
	public Within()
	{
		super();
	}
	public Within(String name, double center_lng, double center_lat)
	{
		super();			
		this.name = name;
		this.center_lat = center_lat;
		this.center_lng = center_lng;
	}
	
	public String toString() {
		return "within:{name:" + this.name + ",center_lng:" + this.center_lng
		+ ", center_lat:" + this.center_lat + "}";
	}
	
	public void setName( String name ) {
		this.name = name;
	}

	public void setCenter_lng( double center_lng ) {
		this.center_lng = center_lng;
	}

	public void setCenter_lat( double center_lat ) {
		this.center_lat = center_lat;
	}
	public String getName() {
		return this.name;
	}

	public double getCenter_lng( ) {
		return this.center_lng;
	}

	public double getCenter_lat( ) {
		return  this.center_lat;
	}
	
};
