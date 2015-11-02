package krem.io.processing.connexity;

import java.util.ArrayList;


  
/*
Hold data and rules about an application
*/
class Application{
	
	/*
	* Configure and define rules
	*/

	//Application objects
	public static Application Entertainment, Transport, Education, Housing, Community, Environment, Health, Food, Employement;

	//Applications objects indexes in applications array
	public static final int ENTERTAINMENT = 0;
	public static final int TRANSPORT = 1;
	public static final int EDUCATION = 2;
	public static final int HOUSING = 3;
	public static final int COMMUNITY = 4;
	public static final int ENVIRONMENT = 5;
	public static final int HEALTH = 6;
	public static final int FOOD = 7;
	public static final int EMPLOYEMENT = 8;
	
	//part of the environment in percentage of total area of the site
	public static float environmentPercentage = 65f;
	//ratio between dwelling and business
	public static float livingBusinessRatio = 2.5f;

	//applications array
	public static Application[] applications;

	//quantity for each application
	public static final int[] activityCount = new int[]{
	  66, //ENTERTAINMENT
	  18, //TRANSPORT 
	  37, //EDUCATION 
	  18, //HOUSING 
	  13, //COMMUNITY 
	  0, //environment is calculated automaticaly
	  24, //HEALTH 
	  194, //FOOD 
	  29 //EMPLOYEMENT 
	};

	//toggle rule2
	public static boolean rule2 = false;
	
  ArrayList<Application> notNeigbours;
  String label;
  int colour;
  int minHeight;
  int maxHeight;
  
  /*
  label     : The application name
  colour    : The application colour
  minHeight : Minimum height for this application
  maxHeight : Maximum height for this application 
  */
  public Application(String label, int colour, int minHeight, int maxHeight){
    this.label = label;
    this.colour = colour;
    notNeigbours = new ArrayList<Application>();
    this.minHeight = minHeight;
    this.maxHeight = maxHeight;
  }
  
  //Flag application given as parameter as NOT being a possible neighbour 
  public void notANeighbour(Application app){
    if(isNotANeighbour(app))
      return;
    notNeigbours.add(app);
    app.notANeighbour(this);
  }
  
  //Return true if application given as parameter is NOT being a possible neighbour, false otherwise
  public boolean isNotANeighbour(Application app){
    return notNeigbours.contains(app);
  }
  
  //Return true if minHeight <= h <= maxHeight
  public boolean fitTheHeight(int h){
    return h>=minHeight && h<=maxHeight;
  }
  
  //Return the name of the application
  public String toString(){
    return label;
  }
  
  public String heightRangetoString(){
    return minHeight+"<"+maxHeight;
  }
  
  //increment given array by 1 for every height in the range of this application
  public void addHeight(int[] heights){
    for(int i=minHeight;i<=maxHeight;i++)
      heights[i]++;
  }
  
 //decrement given array by 1 for every height in the range of this application
  public void subHeight(int[] heights){
    System.out.println(heightRangetoString());
    for(int i=minHeight;i<=maxHeight;i++){
      heights[i]--;
    }
  }
}


  



