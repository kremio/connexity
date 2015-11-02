package krem.io.processing.connexity;

public class Site {
	
	/*
	Hold information about the site configuration
	Total Superficy in Ha
	Population Density in hab./km2
	Superficy covered by one hexagon in Ha
	*/
	public static Site site;
	
	// associate colors to each heights
	public static final int[] heightColors = new int[] { 
		0xFF1B224E,//color(27, 34, 78),
		0xFF413C8E,//color(65, 60, 142),
		0xFF27A55C,//color(39, 165, 92),
		0xFF71BB86,//color(113, 187, 134),
		0xFFEFE31F,//color(239, 227, 31),
		0xFFE95A22,//color(233, 90, 34),
		0xFFE41824//color(228, 24, 36)
	};

	public static int currentHeightColor = 1;

	float superficy; // in Ha
	float popDensity; // in hab./km2
	float hexagoneArea;

	public Site(float superficy, float popDensity, float hexagoneArea) {
		this.superficy = superficy;
		this.popDensity = popDensity;
		this.hexagoneArea = hexagoneArea;
	}

	// Return the number of hexagons on the site
	public int hexagoneNumber() {
		return (int) Math.floor(superficy / hexagoneArea);
	}

}
