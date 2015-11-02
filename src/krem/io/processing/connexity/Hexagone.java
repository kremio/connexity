package krem.io.processing.connexity;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

//NOTE: diamonds are indexed clockwise
class Hexagone implements PConstants{
	
	/*
	Store coordinates of hexagon vertices 
	See Grant Muller : http://grantmuller.com/drawing-a-hexagon-in-processing-java/
	for details
	*/
	public static float c;
	public static float a;
	public static float b;
	public static PVector[] vertices;

	public static void resizeHexagon(float s){
	  c = s;
	  a = c/2;
	  b = (float) (Math.sin(PApplet.radians(60))*c);
	  vertices = new PVector[]{

	    new PVector(a,0),
	    new PVector(a+c,0),
	    new PVector(2*c,b),
	    new PVector(a+c,2*b),
	    new PVector(a,2*b),
	    new PVector(0,b)
	    };
	  }
	
	// activities per lozenge
	int[] apps = new int[] { -1, -1, -1 };

	// height per lozenge
	int[] heights = new int[3];

	// Set height to h for lozenge losange
	public void setHeight(int losange, int h) {
		heights[losange] = h;
	}

	// Return height for lozenge losange
	public int getHeight(int losange) {
		return heights[losange];
	}

	// Set activity to app for lozenge losange
	public boolean setApplication(int losange, int app) {
		if (losange < 0 || losange > 2)
			return true;
		int l1 = 0;
		int l2 = 1;
		if (losange == 0) {
			l1++;
			l2++;
		} else if (losange == 1) {
			l2++;
		}
		if ((apps[l1] != -1 && Application.applications[apps[l1]]
				.isNotANeighbour(Application.applications[app]))
				|| (apps[l2] != -1 && Application.applications[apps[l2]]
						.isNotANeighbour(Application.applications[app])))
			return false;

		apps[losange] = app;
		return true;
	}

	/*
	 * Return the activity for lozenge losange NOTE : while the activity is
	 * stored as an index in the applications array, this method returns the
	 * actual Application object To get the index, call getApplicationInd.
	 */
	public Application getApplication(int losange) {
		if (apps[losange] >= 0)
			return Application.applications[apps[losange]];
		else
			return null;
	}

	public int getApplicationInd(int losange) {
		return apps[losange];
	}

	// Draw a white empty hexagon at position (x,y)
	public void draw(float x, float y, PGraphics gfx) {
		draw(x, y, 0, gfx);
	}

	// Draw an empty hexagon with strokes of color strokeColor at position (x,y)
	public void draw( float x, float y, int strokeColor, PGraphics gfx ) {
		gfx.pushMatrix();
		gfx.pushStyle();
		gfx.noFill();
		gfx.stroke(strokeColor);

		gfx.translate(x, y);
		gfx.beginShape();
		for (int i = 0; i < vertices.length; i++){
			gfx.vertex(vertices[i].x, vertices[i].y);
		}
		gfx.endShape(CLOSE);
		gfx.popStyle();
		gfx.popMatrix();
	}

	// Draw lozenges of hexagon by filling them with the color associated to
	// their height
	public void drawHeights( float x, float y, PGraphics gfx) {
		gfx.pushMatrix();
		gfx.pushStyle();

		gfx.translate(x, y);
		int l = 0;
		for (int i = 0; i < vertices.length; i += 2) {
			if (heights[l] < 0) {
				gfx.noFill();
				gfx.stroke(0);
			} else {
				gfx.noStroke();
				gfx.fill( gfx.color( Site.heightColors[heights[l]] ) );
			}
			gfx.beginShape();
			gfx.vertex(vertices[i].x, vertices[i].y);
			gfx.vertex(vertices[i + 1].x, vertices[i + 1].y);
			gfx.vertex(vertices[(i + 2) % vertices.length].x, vertices[(i + 2)
					% vertices.length].y);
			gfx.vertex(c, c - 1);
			gfx.endShape(CLOSE);
			l++;
		}

		gfx.popStyle();
		gfx.popMatrix();
	}

	// Used to draw in the color map (see Grid)
	public void drawLosanges(float x, float y, int r, int g, PGraphics gfx) {
		gfx.pushMatrix();
		gfx.pushStyle();
		gfx.noStroke();

		gfx.translate(x, y);
		int l = 0;
		for (int i = 0; i < vertices.length; i += 2) {
			gfx.pushStyle();
			gfx.fill(gfx.color(r, g, l));
			gfx.beginShape();

			gfx.vertex(vertices[i].x, vertices[i].y);
			gfx.vertex(vertices[i + 1].x, vertices[i + 1].y);
			gfx.vertex(vertices[(i + 2) % vertices.length].x, vertices[(i + 2)
					% vertices.length].y);
			gfx.vertex(c, c - 1);
			gfx.endShape(CLOSE);
			gfx.popStyle();
			l++;
		}

		gfx.popStyle();
		gfx.popMatrix();
	}

	// Draw lozenges of hexagon by filling them with the color associated to
	// their activity
	public void drawApplications(float x, float y, PGraphics gfx) {
		gfx.pushMatrix();
		gfx.pushStyle();
		gfx.noFill();
		gfx.stroke(0);

		gfx.translate(x, y);
		int l = 0;
		for (int i = 0; i < vertices.length; i += 2) {
			gfx.pushStyle();
			if (apps[l] >= 0){
				gfx.fill(Application.applications[apps[l]].colour);
			}
			gfx.beginShape();
			gfx.vertex(vertices[i].x, vertices[i].y);
			gfx.vertex(vertices[i + 1].x, vertices[i + 1].y);
			gfx.vertex(vertices[(i + 2) % vertices.length].x, vertices[(i + 2)
					% vertices.length].y);
			gfx.vertex(c, c - 1);
			gfx.endShape(CLOSE);
			gfx.popStyle();
			l++;
		}

		gfx.popStyle();
		gfx.popMatrix();
	}

	// Draw lozenges of hexagon in 3D as parallelogram prism using their height
	// data for the distance between base, and activity colour to fill each
	// faces
	public void draw3D(float x, float y, PGraphics gfx) {
		gfx.pushMatrix();
		gfx.pushStyle();
		gfx.noStroke();

		gfx.translate(x, y);
		int l = 0;
		for (int i = 0; i < vertices.length; i += 2) {
			float h = getHeight(l) * 10;
			h = h >= 0 ? h + 1 : 1;
			
			
			Application app = getApplication(l);
			if (app == null){
				gfx.fill( 255 );
			}else{
				gfx.fill(app.colour);
			}
			// bottom base
			gfx.beginShape();
			gfx.vertex(vertices[i].x, vertices[i].y, 0);
			gfx.vertex(vertices[i + 1].x, vertices[i + 1].y, 0);
			gfx.vertex(vertices[(i + 2) % vertices.length].x, vertices[(i + 2)
					% vertices.length].y, 0);
			gfx.vertex(c, c - 10, 0);
			gfx.endShape(CLOSE);

			// top base
			gfx.beginShape();
			gfx.vertex(vertices[i].x, vertices[i].y, h);
			gfx.vertex(vertices[i + 1].x, vertices[i + 1].y, h);
			gfx.vertex(vertices[(i + 2) % vertices.length].x, vertices[(i + 2)
					% vertices.length].y, h);
			gfx.vertex(c, c - 1, h);
			gfx.endShape(CLOSE);

			// 1st face
			gfx.beginShape();
			gfx.vertex(vertices[i].x, vertices[i].y, h);
			gfx.vertex(vertices[i + 1].x, vertices[i + 1].y, h);
			gfx.vertex(vertices[i + 1].x, vertices[i + 1].y, 0);
			gfx.vertex(vertices[i].x, vertices[i].y, 0);
			gfx.endShape(CLOSE);

			// 2nd face
			gfx.beginShape();
			gfx.vertex(vertices[i + 1].x, vertices[i + 1].y, h);
			gfx.vertex(vertices[(i + 2) % vertices.length].x, vertices[(i + 2)
					% vertices.length].y, h);
			gfx.vertex(vertices[(i + 2) % vertices.length].x, vertices[(i + 2)
					% vertices.length].y, 0);
			gfx.vertex(vertices[i + 1].x, vertices[i + 1].y, 0);
			gfx.endShape(CLOSE);

			// 3rd face
			gfx.beginShape();
			gfx.vertex(vertices[(i + 2) % vertices.length].x, vertices[(i + 2)
					% vertices.length].y, h);
			gfx.vertex(c, c - 1, h);
			gfx.vertex(c, c - 1, 0);
			gfx.vertex(vertices[(i + 2) % vertices.length].x, vertices[(i + 2)
					% vertices.length].y, 0);
			gfx.endShape(CLOSE);

			// 4th face
			gfx.beginShape();
			gfx.vertex(c, c - 1, h);
			gfx.vertex(vertices[i].x, vertices[i].y, h);
			gfx.vertex(vertices[i].x, vertices[i].y, 0);
			gfx.vertex(c, c - 1, 0);
			gfx.endShape(CLOSE);

			l++;
		}

		gfx.popStyle();
		gfx.popMatrix();
	}

	// String representation of an hexagon
	public String toString() {
		return "(" + apps[0] + "," + heights[0] + ")(" + apps[1] + ","
				+ heights[1] + ")(" + apps[2] + "," + heights[2] + ")";
	}
}