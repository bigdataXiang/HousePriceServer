package utils.geotext;

import java.util.List;

public class Location
{
	private String matched;
	private double lng;
	private double lat;
	private String resolution;
	private Region region;
	private List<Double> box;
	private String geocode;
	private String englishname;
	private String chinesename;
	public Location()
	{
		super();
	}
	public Location(String matched, double lng, double lat, String resolution, Region region, List<Double> box, String geocode, String chinesename, String englishname )
	{
		super();			
		this.matched = matched;
		this.lng = lng;
		this.lat = lat;
		this.resolution = resolution;
		this.region = region;
		
		this.box = box;
		this.geocode = geocode;
		
		this.englishname = englishname;
		this.chinesename = chinesename;
	}
	
	public String toString() {
		String ct = "{";
		if (this.chinesename != null){
			ct += "chinesename:" + this.chinesename;
			if (this.englishname != null)
				ct += ",englishname" + this.englishname;
			ct +=", lng:" + this.lng+ ", lat:" + this.lat ;
			if (box != null && box.size() > 0)
					{
						ct += ",box:[" + box.get(0); 
						
						for (int n = 1; n < box.size(); n ++)
						{
							ct += "," + box.get(n);
						}
						ct += "]"; 
					}
					
		}else{
			ct += "matched:" + this.matched + ", lng:" + this.lng
					+ ", lat:" + this.lat + ", resolution:" + this.resolution + 
					 ", region:" + this.region;
					
					if (box != null && box.size() > 0)
					{
						ct += ",box:[" + box.get(0); 
						
						for (int n = 1; n < box.size(); n ++)
						{
							ct += "," + box.get(n);
						}
						ct += "]"; 
					}
					
					ct += ",geocode:" + geocode + "}";
			
		}
		return ct;
	}
	
	public void setMatched( String matched ) {
		this.matched = matched;
	}

	public String getMatched() {
		return this.matched;
	}
	
	public void setResolution( String resolution ) {
		this.resolution = resolution;
	}

	public String getResolution() {
		return this.resolution;
	}
	
	public void setLng( double lng ) {
		this.lng = lng;
	}

	public double getLng() {
		return this.lng;
	}
	
	public void setLat( double lat ) {
		this.lat = lat;
	}

	public double getLat() {
		return this.lat;
	}
	
	public void setRegion( Region region ) {
		this.region = region;
	}

	public Region getRegion() {
		return this.region;
	}
	
	public void setChinesename( String chinesename ) {
		this.chinesename = chinesename;
	}

	public String getChinesename() {
		return this.chinesename;
	}

	public void setEnglishname( String englishname ) {
		this.englishname = englishname;
	}

	public String getEnglishname() {
		return this.englishname;
	}

	public void setBox( List<Double> box) {
		this.box = box;
	}

	public List<Double> getBox() {
		return this.box;
	}
	public void setGeocode(String geocode ) {
		this.geocode = geocode;
	}

	public String getGeocode() {
		return this.geocode;
	}
};
