package krem.io.processing.connexity;

import java.awt.Frame;

import processing.core.PApplet;
import controlP5.CheckBox;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Slider;
import controlP5.Tab;
import controlP5.Textlabel;

// the ControlFrame class extends PApplet, so we 
// are creating a new processing applet inside a
// new frame with a controlP5 object loaded
@SuppressWarnings("serial")
public class ControlFrame extends PApplet {

	/*
	 * Creates control window, tabs and controllers Not very interesting, see
	 * controlP5 doc....
	 */

	public static ControlFrame addControlFrame(String theName, int theWidth,
			int theHeight, PApplet app) {
		Frame f = new Frame(theName);
		ControlFrame p = new ControlFrame(app, theWidth, theHeight);
		f.add(p);
		p.init();
		f.setTitle(theName);
		f.setSize(p.w, p.h);
		f.setLocation(100, 100);
		f.setResizable(false);
		f.setVisible(true);
		return p;
	}

	int w, h;

	public Slider heightBrush;
	public Textlabel heightCountLabel;
	public CheckBox useRule2;
	ControlP5 controlP5;

	public void setup() {
		size(w, h);
		frameRate(25);
		setupControl();
	}

	@SuppressWarnings("rawtypes")
	private void setupControl() {
		controlP5 = new ControlP5(this);

		Tab t = controlP5.addTab("Occupation");
		t.activateEvent(true);
		t.setId(-2);

		t = controlP5.getDefaultTab();
		t.setLabel("Site Layout");
		t.activateEvent(true);
		t.setId(-4);

		Controller c = controlP5.addSlider("Environment percentage", 0, 100,
				Application.environmentPercentage, 100, 40, 100, 14);
		// c.setLabelVisible(false);
		c.setColorForeground(Application.Environment.colour);
		c.setId(1);
		c.moveTo("Occupation");

		c = controlP5.addSlider("L/B ratio", 0, 10,
				Application.livingBusinessRatio, 100, 60, 100, 14);
		// c.setLabelVisible(false);
		c.setId(2);
		c.moveTo("Occupation");

		int vertOffset = 0;
		for (int i = 0; i < Application.applications.length; i++) {
			if (i == Application.ENVIRONMENT)
				continue;
			c = controlP5.addNumberbox(Application.applications[i].toString()
					+ " Quantity", Application.activityCount[i], 100,
					80 + vertOffset * 20, 100, 14);
			c.getCaptionLabel().getStyle().marginLeft = 100;
			c.getCaptionLabel().getStyle().marginTop = -14;
			c.setColorBackground(Application.applications[i].colour);
			c.moveTo("Occupation");
			c.setId(3 + i);
			vertOffset++;
		}
		useRule2 = controlP5.addCheckBox("rule 2", 100, 80 + vertOffset * 20);
		useRule2.addItem("Use rule 2", 0);
		useRule2.moveTo("Occupation");
		// c.setLabelVisible(false);
		// c.setTab(controlWindow,"Occupation");

		t = controlP5.addTab("Heights");
		t.activateEvent(true);
		t.setId(-3);

		heightBrush = controlP5.addSlider("Height Brush", 0, 6,
				Site.currentHeightColor, 10, 20, 190, 14);
		heightBrush
				.setColorBackground( color(Site.heightColors[Site.currentHeightColor]) );
		heightBrush.setNumberOfTickMarks(7);
		heightBrush.setSliderMode(Slider.FLEXIBLE);
		heightBrush.moveTo("Heights");
		heightBrush.setId(12);

		heightCountLabel = controlP5.addTextlabel("heightCountLabel",
				Grid.landHeight.heightQuantity[Site.currentHeightColor]
						+ " remaining", 300, 20);
		heightCountLabel.moveTo("Heights");

		vertOffset = 0;
		for (int i = 0; i < Application.applications.length; i++) {
			// c =
			// controlP5.addNumberbox(applications[i].toString(),activityCount[i],100,40+vertOffset*20,100,14);
			c = controlP5.addRange(Application.applications[i].toString()
					+ " Height Range", 0, 7,
					Application.applications[i].minHeight,
					Application.applications[i].maxHeight, 10,
					50 + vertOffset * 20, 190, 14);
			/*
			 * c.captionLabel().style().marginLeft = 100;
			 * c.captionLabel().style().marginTop = -14;
			 */
			c.setColorForeground(Application.applications[i].colour);
			c.moveTo("Heights");
			c.setId(13 + i);
			vertOffset++;
		}
		c = controlP5.addButton("Reset").setValue(0).setPosition( 10, 50 + vertOffset * 20 ).setSize( 80, 19 );

		c.moveTo("Heights");
		c.setId(13 + Application.applications.length);

		t = controlP5.addTab("3D");
		t.activateEvent(true);
		t.setId(-5);
			
		c = controlP5.addTextlabel("CameraInstruction", "A mouse left-drag will rotate the camera around the subject,\na right drag will zoom in and out,\nand a middle-drag (command-left-drag on mac) will pan.\n\nA double-click restores the camera to its original position.\nThe shift key constrains rotation and panning to one axis or the other.");
		c.setPosition(10, 20);
		c.setWidth(200);
		c.moveTo("3D");
	}

	/*
	 * Process events from controllers and tabs
	 */
	public void controlEvent(ControlEvent theEvent) {
		// it's an event from a controller group (check boxes, lists,...)
		if (theEvent.isGroup()) {
			for (int i = 0; i < theEvent.getArrayValue().length; i++) {
				if (i == 0) {
					Application.rule2 = !Application.rule2;
					println(Application.rule2);
					break;
				}
			}
			return;
		}

		// It's an event from a tab
		if (theEvent.isTab()) {
			println("istab");
			switch ( theEvent.getTab().getId() ) {
			case -2:
				Connexity.MODE = 1;
				break;
			case -3:
				Connexity.MODE = 2;
				break;
			case -4:
				Connexity.MODE = 0;
				break;
			case -5:
				Connexity.MODE = 3;
				break;
			}
			System.out.println("Mode: " + Connexity.MODE );
			return;
		}

		// It's an event from a controller
		switch ( theEvent.getId() ) {
		case 1:
			Application.environmentPercentage = theEvent.getValue();
			Grid.landOccupation.calculateLandOccupation();
			Grid.landOccupation.createRepartitionArray(this);
			break;
		case 2:
			Application.livingBusinessRatio = theEvent.getValue();
			Grid.landOccupation.calculateLandOccupation();
			Grid.landOccupation.createRepartitionArray(this);
			break;
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
			int app = theEvent.getId() - 3;
			Application.activityCount[app] = (int) theEvent.getValue();
			Grid.landOccupation.calculateLandOccupation();
			Grid.landOccupation.createRepartitionArray(this);
			break;
		case 12:
			System.out.println("Change height paint: "+theEvent.getValue());
			Site.currentHeightColor = (int) theEvent.getValue();
			heightBrush.getColor().setBackground( Site.heightColors[Site.currentHeightColor] );
			heightCountLabel
					.setValue(Grid.landHeight.heightQuantity[Site.currentHeightColor]
							+ " remaining");
			break;
		case 22:
			Connexity.grid.resetHeight(this);
			break;
		}
	}

	public void draw() {
		background(color(40));
	}

	public ControlFrame(Object theParent, int theWidth, int theHeight) {
		parent = theParent;
		w = theWidth;
		h = theHeight;
	}

	public ControlP5 control() {
		return controlP5;
	}

	Object parent;

}
