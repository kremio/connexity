package krem.io.processing.connexity;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

class Grid implements PConstants{
	
	// Calculate land occupation rule
	public static LandOccupation landOccupation;
	// Calculate land height rule
	public static LandOccupation landHeight;
	
	  int w, h, leftMargin, topMargin, mouseOverX, mouseOverY, mouseOverZ, lastDragX, lastDragY, lastDragZ;
	  float diameter;
	  Hexagone tile;
	  
	  /*
	  RGB color mapping of grid for each hexagons and contained lozenge (see resizeGrid())
	  red : hexagon x coordinate
	  green : hexagon y coordinate
	  blue : lozenge nnumber (0..2)
	  
	  e.g. Hexagon at (10,33), lozenge 1 => color(10,33,1) 
	  */
	  PGraphics colorMap;
	  
	  //All the hexagons filled on the grid
	  Hexagone[][] hexagones;
	  
	  //Used to get the coordinate of 6 hexagone neighbours of hexagone at 0,0 (works for hexagone with an even x coordinate)
	  //NOTE : indexed clockwise
	  PVector[] neighboursP = new PVector[]{
	    new PVector(0,-1),new PVector(1,-1), new PVector(1,0), new PVector(0,1),new PVector(-1,0),new PVector(-1,-1)  };
	  //Used to get the 6 hexagone neighbour of hexagone at 0,0 (works for hexagone with an odd x coordinate)
	  PVector[] neighboursI = new PVector[]{
	    new PVector(0,-1),new PVector(1,0), new PVector(1,1), new PVector(0,1),new PVector(-1,1),new PVector(-1,0)  };

	  /*
	  Used to get the 8 neighbouring lozenge (and their hexagone) of all 3 lozenges in an hexagone
	  NOTE : indexed clockwise
	  The vectors contain the following info:
	  x : index of the neighbour hexagon (0..5)
	  y : index of the first neighbour lozenge in the hexagon (0..2)
	  z : index of the seconde neighbour lozenge in the hexagon (0..2)
	  */
	  PVector[][] losangeNeighbours = new PVector[][]{
	    new PVector[]{
	      new PVector(0,1,2), new PVector(1,1,2), new PVector(2,0,2), new PVector(5,0,1)    }
	    ,
	    new PVector[]{
	      new PVector(1,1,2), new PVector(2,0,2), new PVector(3,0,2), new PVector(4,1,2)    }
	    ,
	    new PVector[]{
	      new PVector(1,1,2), new PVector(3,0,2), new PVector(4,0,1), new PVector(5,0,1)    }   
	  };


	  /*
	  width of the grid (in hexagon)
	  height of the grid (in hexagon)
	  distance to the left edge of the window (in pixel)
	  distance to the top edge of the window (in pixel)
	  diameter of the hexagons
	  */
	  public Grid(int w, int h, int leftMargin, int topMargin, float diameter, PApplet app){
	    this.w = w;
	    this.h = h;   
	    this.diameter = diameter; 
	    hexagones = new Hexagone[w][h];
	    this.leftMargin = leftMargin;
	    this.topMargin = topMargin;
	    //reset last drag
	    lastDragX = -1;
	    lastDragY = -1;
	    lastDragZ = -1;
	    //get an hexagon for tiling
	    tile = new Hexagone();
	    //calculate hexagon vertices and color map
	    resizeGrid(app);
	  }

	  //reset height data
	  public void resetHeight(PApplet noiseMaker){
	    for(int i=0;i<w;i++){
	      for(int j=0;j<h;j++){
	        if(hexagones[i][j]!=null){
	          hexagones[i][j].setHeight(0,-1);
	          hexagones[i][j].setHeight(1,-1);
	          hexagones[i][j].setHeight(2,-1);
	        }
	      }
	    }
	    landHeight = new LandOccupation( Site.site );
	    landHeight.calculateLandOccupation();
	    landHeight.createRepartitionArray(noiseMaker);
	  }

	  //reset occupation data
	  public void resetOccupation(PApplet noiseMaker){
	    for(int i=0;i<w;i++){
	      for(int j=0;j<h;j++){
	        if(hexagones[i][j]!=null){
	          hexagones[i][j].apps = new int[]{
	            -1,-1,-1          };
	        }
	      }
	    }
	    landOccupation = new LandOccupation( Site.site );
	    landOccupation.calculateLandOccupation();
	    landOccupation.createRepartitionArray(noiseMaker);
	  }

	// draw the grid
	public void draw(PGraphics gfx){
		gfx.pushMatrix();
		gfx.pushStyle();
		gfx.noStroke();
	    //set position of the top left corner of the grid
		gfx.translate(leftMargin,topMargin);
	    //draw the tiling
	    for(float x = 0;x<w;x++){
	      for(float y = 0;y<h;y++){
	        //apply vertical offset to odd columns of hexagon
	        float offset = x%2 == 0 ? 0 : Hexagone.b;
	    	int intX = (int) Math.floor(x);
	    	int intY = (int) Math.floor(y);
	        if( hexagones[intX][intY] == null ){
	          //draw hexagon
	          tile.draw(x*(Hexagone.a+Hexagone.c),y*Hexagone.b*2+offset,gfx.color(200), gfx);
	          gfx.fill(200);
	          //if mouse is over the tile highlight the hexagon
	          if(mouseOverX == x && mouseOverY == y){
	        	  gfx.fill(0,0,255);
	          }
	          gfx.ellipse(x*(Hexagone.a + Hexagone.c ) + Hexagone.c, y * Hexagone.b * 2 + offset + Hexagone.c - 1, 5, 5);
	        }
	      }
	    }
	    
	    //draw the filled hexagon
	    for(float x = 0;x<w;x++){
	      for(float y = 0;y<h;y++){
	    	float offset = x%2 == 0 ? 0 : Hexagone.b;
	    	int intX = (int) Math.floor(x);
	    	int intY = (int) Math.floor(y);
	    	Hexagone hexagone = hexagones[intX][intY];
	    	
	        if( hexagone == null ){
	        	continue;
	        }
	        
	          if(mouseOverX == x && mouseOverY == y){
	        	  gfx.fill(0,0,255);
	        	  gfx.ellipse( x * ( Hexagone.a + Hexagone.c ) + Hexagone.c , y * Hexagone.b * 2 + offset + Hexagone.c - 1 ,8,8);
	          }
	          //draw graphics according to view
	          switch(Connexity.MODE){
	          case 3:
	            hexagone.draw3D( x * ( Hexagone.a + Hexagone.c ), y * Hexagone.b * 2 + offset, gfx);
	          case 2:
	            hexagone.drawHeights(x*(Hexagone.a+Hexagone.c),y*Hexagone.b*2+offset, gfx);
	            break;
	          case 1:
	            hexagone.drawApplications(x*(Hexagone.a+Hexagone.c),y*Hexagone.b*2+offset, gfx);
	            break;
	          case 0:
	            hexagone.draw(x*(Hexagone.a+Hexagone.c),y*Hexagone.b*2+offset, gfx);
	            break;
	          }
	        }
	    }
	    gfx.popStyle();
	    gfx.popMatrix();
	  }

	  //return the translation vector from Top Left to Bottom Right
	  public PVector getBottomRightCorner(){
	    return new PVector((this.w)*(Hexagone.a+Hexagone.c)+Hexagone.a,(this.h+0.5f)*(Hexagone.b*2));
	  }

	  //map mouse cursor position to grid coordinate
	  public void mouseOver(PApplet app){
	    int mX = app.mouseX-leftMargin;
	    if(mX < 0 || mX >= colorMap.width){
	      mouseOverX = -1;
	      mouseOverY = -1;
	      mouseOverZ = -1;            
	      return;
	    }
	    int mY = app.mouseY-topMargin;
	    if(mY < 0 || mY >= colorMap.height){
	      mouseOverX = -1;
	      mouseOverY = -1;
	      mouseOverZ = -1;      
	      return;
	    }
	    colorMap.beginDraw();
	    //get the color of the pixel at this position in the color map
	    int c = colorMap.pixels[(int) Math.floor(mX+mY*colorMap.width)];
	    colorMap.endDraw();
	    //extract coordinates
	    mouseOverX = c >> 16 & 0xFF; // hexagon x
	    mouseOverY = c >> 8 & 0xFF; //hexagon y
	    mouseOverZ = c & 0xFF; //lozenge
	    mouseOverX = mouseOverX>=w?-1:mouseOverX;
	    mouseOverY = mouseOverY>=h?-1:mouseOverY;
	    mouseOverZ = mouseOverZ>=3?-1:mouseOverZ;

	  }


	public void click(PApplet pApp) {
		if (mouseOverX < 0 || mouseOverY < 0) // the mouse is not over the grid
			return;
		// apply click according to view
		switch (Connexity.MODE) {
		case 0: // add a filled hexagon
			if (hexagones[mouseOverX][mouseOverY] == null) {
				hexagones[mouseOverX][mouseOverY] = new Hexagone();
			} else {
				hexagones[mouseOverX][mouseOverY] = null;
			}
			break;
		case 1: // fill the site with activities starting at the mouse location
			Hexagone hx = hexagones[mouseOverX][mouseOverY];
			if (hx == null)
				return;
			resetOccupation(pApp);
			fillSite(mouseOverX, mouseOverY);
			break;
		case 2: // paint height on the curren lozenge
			hx = hexagones[mouseOverX][mouseOverY];
			if (hx == null)
				return;
			if (landHeight.heightQuantity[Site.currentHeightColor] == 0)
				return;
			if (hx.getHeight(mouseOverZ) == Site.currentHeightColor)
				return;
			System.out.println("here " + Site.currentHeightColor);

			if (hx.getHeight(mouseOverZ) != -1) {
				landHeight.repartitionArray.add(hx
						.getApplicationInd(mouseOverZ));
				hx.setHeight(mouseOverZ, -1);
			}

			landHeight.consumeActivity(landHeight.getActivityForHeight(0,
					Site.currentHeightColor));
			hx.setHeight(mouseOverZ, Site.currentHeightColor);

			Connexity.controlFrame.heightCountLabel
					.setValue(landHeight.heightQuantity[Site.currentHeightColor]
							+ " remaining");
			Connexity.controlFrame.heightCountLabel.update();
			break;
		}
	}
	  
	  //call click() on mouse drag if mouse position has changed
	  public void drag(PApplet app){
	    if(lastDragX!=mouseOverX || lastDragY!=mouseOverY || (Connexity.MODE == 2 && lastDragZ!=mouseOverZ)){
	      click(app);
	      lastDragX = mouseOverX;
	      lastDragY = mouseOverY; 
	      lastDragZ = mouseOverZ;      
	    }
	  }

	// Save grid to text file
	public void saveToFile(PApplet app) {
		String hexaCoords = "";
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int intX = (int) Math.floor(x);
				int intY = (int) Math.floor(y);
				Hexagone hexagone = hexagones[intX][intY];

				if (hexagone != null) {
					hexaCoords += x + ":" + y + ":" + hexagone.toString() + ";";
				}
			}
		}
		app.saveStrings("site.txt", new String[] { hexaCoords });
	}

	  //Load grid from text file
	  public void loadFromFile(PApplet pApp){
	    String hexaCoordStr = pApp.loadStrings("site.txt")[0];
	    hexagones = new Hexagone[w][h];
	    String[] hexaCoords = hexaCoordStr.split(";");
	    System.out.println(hexaCoords.length);
	    for(int i=0;i<hexaCoords.length;i++){
	      int x = Integer.parseInt(hexaCoords[i].split(":")[0]);
	      int y = Integer.parseInt(hexaCoords[i].split(":")[1]);
	      hexagones[x][y] = new Hexagone();
	      System.out.println(hexaCoords.length);
	      if(hexaCoords[i].split(":").length==3){
	        String info = hexaCoords[i].split(":")[2];
	        String[] losanges = info.split("\\)");
	        int app = Integer.parseInt(losanges[0].substring(1).split(",")[0]);
	        int h = Integer.parseInt(losanges[0].substring(1).split(",")[1]);
	        hexagones[x][y].setHeight(0,h);
	        hexagones[x][y].setApplication(0,app);

	        app = Integer.parseInt(losanges[1].substring(1).split(",")[0]);
	        h = Integer.parseInt(losanges[1].substring(1).split(",")[1]);
	        hexagones[x][y].setHeight(1,h);
	        hexagones[x][y].setApplication(1,app);

	        app = Integer.parseInt(losanges[2].substring(1).split(",")[0]);
	        h = Integer.parseInt(losanges[2].substring(1).split(",")[1]);
	        hexagones[x][y].setHeight(2,h);
	        hexagones[x][y].setApplication(2,app);
	      }
	    }
	    System.out.println(hexaCoords.length+" hexagones loaded");
	  }

	  //return true if the hexagon at (c,r) is null OR all its lozenges have an activity, false otherwise
	  public boolean isFilled(int c, int r){
	    Hexagone h = hexagones[c][r];
	    return (h == null) || (h.getApplication(0)!=null &&  h.getApplication(1)!=null && h.getApplication(2)!=null); 
	  }

	// Fill the lozenges of an hexagon using occupation rules
	private void fillHexagone(int c, int r) {
		if (isFilled(c, r)) // hexagon already filled
			return;

		Hexagone h = hexagones[c][r];
		Hexagone[] n = getNeighboursHexa(c, r); // the neighbour of the hexagon
		for (int l = 0; l < 3; l++) { // for each of the lozenge of the hexagon
			boolean match = false; // true when the lozenge has been filled
			int randomApp = -1; // index of an activity in the applications
								// array
			// int randomAppInd = 0; //index of an activity in the
			// repartitionArray
			int i = -1; // used to move in the repartitionArray
			/*
			 * 1 : Pick up a position in the repartitionArray if rule2 is
			 * activated, or randomly otherwise 2 : Check that the activity
			 * match the rules regarding the neighbours lozenges in neighbours
			 * hexagons ELSE GOTO 1 3 : Check that the activity match the rules
			 * regarding the neighbours lozenges in the current hexagons ELSE
			 * GOTO 1 4 : Set the lozenge activity to randomApp
			 */
			while (!match) {
				i++;
				match = true;
				if (Application.rule2) {
					randomApp = landOccupation.repartitionArray.get(i);
				} else {
					randomApp = (int) Math.floor(Math.random()
							* Application.applications.length);
				}

				for (int ln = 0; ln < losangeNeighbours[l].length; ln++) {
					int lX = (int) losangeNeighbours[l][ln].x;
					int lY = (int) losangeNeighbours[l][ln].y;
					int lZ = (int) losangeNeighbours[l][ln].z;
					if (n[lX] == null) {
						continue;
					}
					if ((n[lX].getApplication(lY) != null && n[lX]
							.getApplication(lY).isNotANeighbour(
									Application.applications[randomApp]))
							|| (n[lX].getApplication(lZ) != null && n[lX]
									.getApplication(lZ)
									.isNotANeighbour(
											Application.applications[randomApp]))) {
						match = false;
						break;
					}
				}
				if (match) {
					match = h.setApplication(l, randomApp);
					if (match) {
						// println("OK");
						if (Application.rule2) {
							landOccupation.consumeActivity(i);
						}
					}
				}
			}

		}
	}

	  //fill the entire site using occupation rules by recursive call to this method
	  public void fillSite(int c, int r){
	    fillHexagone(c, r);
	    PVector[] neighbours = c%2==0?neighboursP:neighboursI;
	    for(int i=0;i<neighbours.length;i++){
	      if(c+neighbours[i].x >=0 && r+neighbours[i].y >=0 && c+neighbours[i].x < w && r+neighbours[i].y < h){
	        if(!isFilled((int)(c+neighbours[i].x), (int)(r+neighbours[i].y)))
	          fillSite((int)(c+neighbours[i].x), (int)(r+neighbours[i].y));
	      }
	    }
	  }

	  //Return an array of the neighbours hexagones
	  public Hexagone[] getNeighboursHexa(int c, int r){
	    Hexagone[] n = new Hexagone[6];
	    PVector[] neighbours = c%2==0?neighboursP:neighboursI;
	    for(int i=0;i<neighbours.length;i++){ 
	      if(c+neighbours[i].x >=0 && r+neighbours[i].y >=0 && c+neighbours[i].x < w && r+neighbours[i].y < h){
	        n[i] = hexagones[(int)(c+neighbours[i].x)][(int)(r+neighbours[i].y)];
	      }
	      else{
	        n[i] = null;
	      }
	    }
	    return n;
	  }

	 
	  public void increaseDiameter(PApplet app){
	    diameter+=0.1;
	    resizeGrid(app);
	  }

	  public void decreaseDiameter(PApplet app){
	    diameter-=0.1;
	    resizeGrid(app);
	  }

	  //Resize the hexagons and the color map
	  public void resizeGrid(PApplet app){
	    Hexagone.resizeHexagon(diameter*0.5f);
	    if(colorMap!=null)
	      colorMap.dispose();
	    colorMap = app.createGraphics( (int)getBottomRightCorner().x, (int)getBottomRightCorner().y, P2D);
	    colorMap.beginDraw();
	    // colorMap.smooth();
	    colorMap.background(255);

	    //create the colorMap
	    for(int x=0;x<w;x++){
	      for(int y=0;y<h;y++){
	        float offset = x%2==0?0:Hexagone.b;
	        tile.drawLosanges(x*(Hexagone.a+Hexagone.c),y*Hexagone.b*2+offset, x, y, colorMap);
	      }
	    }
	    colorMap.endDraw();
	  }
	}

