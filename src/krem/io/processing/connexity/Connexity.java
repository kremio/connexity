package krem.io.processing.connexity;


import processing.core.*;
//Handles the 3D camera
import peasy.*;

@SuppressWarnings("serial")
public class Connexity extends PApplet {

	public static void main(String[] args) {
		PApplet.main(new String[] { "krem.io.processing.connexity.Connexity" });
	}

	//Hexagon shape and division in 3 lozenges
	Hexagone hexa;
	int gridX = 22;

	// Hexagonal tiling
	static Grid grid;

	/*
	Switch between views
	0: site layout edit, 1: activities edit, 2: height edit, 3: 3D view
	*/
	public static int MODE = 0;


	public static ControlFrame controlFrame;

	PeasyCam cam;

	public void setup(){
	 size(800,900,P3D);
	 
	 cam = new PeasyCam(this, width/2.0, height/2.0, 0,(height/2.0f) / tan(PI*60.0f / 360.0f));
	 cam.setMinimumDistance(10);
	 cam.setMaximumDistance(1000);
	 cam.setSuppressRollRotationMode();
	 cam.setActive(false);
	  
	 //new ControlP5(this);
	 smooth();
	 //Define a site of 100 Ha, Population density of 2858 hab./km2, one hexagone covers 0.5 Ha
	 Site.site = new Site(100, 2858, 0.5f);

	 //Create hexagonal tiling
	 hexa = new Hexagone();
	 grid = new Grid(22,30,183,155,24, this);
	 
	 /*
	 Configure activities giving:
	 Name
	 Color
	 Minimum height
	 Maximum height
	 */
	 Application.Entertainment = new Application("Entertainment",color(169,38,134),0,2);
	 Application.Transport = new Application("Transport",color(55,74,151),0,1);
	 Application.Education = new Application("Education",color(240,136,21),0,4);
	 Application.Housing = new Application("Housing",color(240,228,32),2,6);
	 Application.Community = new Application("Community",color(42,187,234),0,3);
	 Application.Environment = new Application("Environment",color(12,113,43),0,0);
	 Application.Health = new Application("Health",color(231,114,166),1,4);
	 Application.Food = new Application("Food",color(150,190,14),0,2);
	 Application.Employement = new Application("Employement", color(228,25,31),1,5);
	 
	 /*
	 Define neighbouring rules
	 */
	 Application.Entertainment.notANeighbour( Application.Housing );
	 Application.Entertainment.notANeighbour( Application.Health );
	 Application.Entertainment.notANeighbour( Application.Education );  
	 Application.Community.notANeighbour( Application.Health );
	 
	 // Aggregate all applications in one Array
	 Application.applications = new Application[]{
			 Application.Entertainment,
			 Application.Transport,
			 Application.Education,
			 Application.Housing,
			 Application.Community,
			 Application.Environment,
			 Application.Health,
			 Application.Food,
			 Application.Employement
	 };
	 
	  //Configure land occupation for the site
	 Grid.landOccupation = new LandOccupation(Site.site);
	  //Configure land height for the site
	 Grid.landHeight = new LandOccupation(Site.site);
	 //Calculate rules
	 Grid.landOccupation.calculateLandOccupation();
	 Grid.landOccupation.createRepartitionArray(this);
	 Grid.landHeight.calculateLandOccupation();
	 Grid.landHeight.createRepartitionArray(this);
	 //load grid setup from file
	 grid.loadFromFile(this);
	 controlFrame = ControlFrame.addControlFrame("Controls", 400, 300, this);
	}
	
	void setCamera() {
		if (MODE == 3) { // if in 3D view, apply transformation to camera
			background(0);
			// 'global luminosity' light
			ambientLight(200, 200, 200);
			// shadow casting light
			directionalLight(55, 55, 55, 0, -1, 0);
			
			//Activate interactive 3D camera
			cam.setActive(true);

		} else { // else set to 2D view
			cam.setActive(false);
			camera(width / 2.0f, height / 2.0f, (height / 2.0f)
					/ tan(PI * 60.0f / 360.0f), width / 2.0f, height / 2.0f, 0,
					0, 1f, 0);
			background(255);
		}
	}

	public void draw(){
	  
	  //set camera if in 3D view, reset to default otherwise
	  setCamera();
	  //draw grid bounding box
	  PVector bR = grid.getBottomRightCorner();
	  rect(grid.leftMargin,grid.topMargin,bR.x,bR.y);
	 //ask grid to transform mouse cursor position into a position in the hexagonal tiling 
	 grid.mouseOver(this);
	 //draw grid
	 grid.draw(this.g);

	 // set origin of coordinate system to the center of the window
	 translate(width/2.0f, height/2.0f, 0);
	 fill(255);
	}

	public void mousePressed(){
	  //transfer click event to grid
	  grid.click(this);
	}

	public void mouseDragged(){
	  //transfer drag event to grid
	  grid.drag(this);
	}

	public void keyPressed(){
	  /*
	  Keyboard input:
	  ---------------
	  UP Arrow    : translate grid one pixel north
	  DOWN Arrow  : translate grid one pixel south
	  LEFT Arrow  : translate grid one pixel west
	  RIGHT Arrow : translate grid one pixel east
	  s           : save current grid data to file
	  l           : load grid data from file
	  +           : increase radius of hexagons
	  -           : decrease radius of hexagons
	  */
	  switch(keyCode){
	    case UP:
	    grid.topMargin-=1;
	    break;
	    
	    case DOWN:
	    println("down");
	     grid.topMargin+=1;
	    break;
	    
	    case LEFT:
	    grid.leftMargin-=1;
	    break;
	    
	    case RIGHT:
	    grid.leftMargin+=1;
	    break;
	  }
	  
	  switch(key){
	    case 's' : grid.saveToFile(this);
	    break;
	    case 'l' : grid.loadFromFile(this);
	    break;
	    case '+' : grid.increaseDiameter(this);
	    break;
	    case '-' : grid.decreaseDiameter(this);
	    break;
	  }
	  
	}
	
	
}
