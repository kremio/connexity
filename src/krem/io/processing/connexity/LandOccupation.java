package krem.io.processing.connexity;

import java.util.ArrayList;

import processing.core.PApplet;

class LandOccupation{

	  Site site;
	  // Total number of activities in the site
	  int totalActivities;
	  // percentage of activity on site
	  float activityPercent;
	  // percentage of office on site
	  float officePercent;
	  //percentage of dwelling on site
	  float dwellingPercent;
	  
	  //Diamonds remaining for each heights
	  int[] heightQuantity;
	  
	  /*
	  This array contains index of activities in the applications array, one for each lozenge. Thus it contains as much activities as they are lozenge.
	  The quantity of each activities is set according to the percentages calculated by a call to calculateLandOccupation()
	  and filles by a call to createRepartitionArray()
	  IMPORTANT : This means that  calls to calculateLandOccupation() followed by createRepartitionArray() MUST hapen before reading activities from this array.
	  */
	  ArrayList<Integer> repartitionArray;
	  
	  public LandOccupation(Site site){
	    this.site = site;  
	  }
	  
	  //Apply land occupation rules
	  public void calculateLandOccupation(){
	    totalActivities = 0;
	    for(int i=0;i< Application.activityCount.length;i++)
	      totalActivities += Application.activityCount[i];
	    activityPercent = 100*(100-Application.environmentPercentage)/totalActivities;
	    officePercent = activityPercent/(1.0f+Application.livingBusinessRatio);
	    dwellingPercent = (100-Application.environmentPercentage)-activityPercent-officePercent;
	  }
	  
	  //return the number of lozenges for a given percentage
	  public int getNumberOfLosangeForPercentage(float percent){
	    return Math.round( site.hexagoneNumber() * 3 * (percent/100) );
	  }
	  
	  //return the number of lozenges 
	  public int getNumberOfLosangeForActivity(float l, float aN){ //losange restant, qte pour activité
	    return Math.round( aN/totalActivities * l );
	  }
	  
	  //populate repartitionArray and heightQuantity
	  public void createRepartitionArray(PApplet noiseMaker){
	    //initialize heightQuantity to be the same size as heightColors
	    heightQuantity = new int[ Site.heightColors.length ];
	    //Total number of lozenges
	    int tL = site.hexagoneNumber()*3;
	    System.out.println(tL+" losanges");
	    repartitionArray = new ArrayList<Integer>();
	    
	    //Starts by filling the array with parcs
	    for(int i=0;i<tL;i++){
	      repartitionArray.add(Application.ENVIRONMENT);
	      Application.applications[Application.ENVIRONMENT].addHeight(heightQuantity);
	    }
	    
	    //number of lozenge occupied by housing
	    int dL = getNumberOfLosangeForPercentage(dwellingPercent);
	   
	    for(int i=0;i<dL;i++){
	      /*
	      Put housing at random position
	      1 : Pick up a random position in the array
	      2 : IF position is occupied by something else than ENVIRONMENT GOTO 1
	      3 : ELSE set value of array at position to HOUSING
	      */
	      int n = (int) Math.floor( Math.random() * repartitionArray.size() ); //1
	      while( repartitionArray.get(n) != Application.ENVIRONMENT ){ //2
	        n = (int) Math.floor( Math.random() * repartitionArray.size() ); //1
	      }
	      repartitionArray.set(n, Application.HOUSING); //3
	      Application.applications[ Application.HOUSING ].addHeight(heightQuantity);
	    }
	    
	    
	    //number of lozenges occupied by business
	    int biznessL = getNumberOfLosangeForPercentage(officePercent);
	    //number of lozenges remaining
	    int remainingL = tL-getNumberOfLosangeForPercentage( Application.environmentPercentage ) - dL-biznessL;
	    //used to move in the Perlin noise function
	    float nM = 0;
	    //Go through each activities and populate the array
	    for(int i=0;i< Application.activityCount.length;i++){
	      //number of lozenge for this activity
	      int nL = getNumberOfLosangeForActivity(remainingL,  Application.activityCount[i]);
	      //if the activity is employement use biznessL instead
	      if(i ==  Application.EMPLOYEMENT){
	        nL = biznessL;
	      }
	      
	      //reset
	      nM = 0;
	      /*
	      WHILE they are remaining lozenges for current activity
	       1 : Pick up a random position in the array (using Perlin noise, more 'organic')
	       2 : IF position is occupied by something else than ENVIRONMENT GOTO 1
	       3 : ELSE set value of array at position to current activity
	       4 : decrement remaining lozenge by 1
	      */
	      while(nL > 0){
	        int n = (int) Math.floor( noiseMaker.noise(i+nM) * repartitionArray.size()); //1
	        while( repartitionArray.get(n) != Application.ENVIRONMENT){ //2
	          nM += 0.1;
	          n = (int) Math.floor(noiseMaker.noise(i+nM) * repartitionArray.size()); //1
	        }
	        repartitionArray.set(n,i); //3
	        System.out.println(n + ": " + i);
	        Application.applications[i].addHeight(heightQuantity);
	        nL--; //4
	      }
	    }
	  }
	  
	  /*
	  Return an activity that match the given height
	  */
	public int getActivityForHeight(int start, int h) {
		int bestMatch = 9;
		int bestMatchInd = start;
		for (int i = start; i < repartitionArray.size(); i++) {
			int ind = repartitionArray.get(i);
			System.out.println(i + " :" + ind);
			if ( /*ind >= 0 &&*/ Application.applications[ind].fitTheHeight(h) ) {
				if (bestMatch > Math.abs(Application.applications[ind].maxHeight
						- Application.applications[ind].minHeight)) {
					bestMatch = Math.abs(Application.applications[ind].maxHeight
							- Application.applications[ind].minHeight);
					bestMatchInd = i;
				}
			}
		}
	    Application.applications[ repartitionArray.get(bestMatchInd)].fitTheHeight(h);
	    return bestMatchInd;
	  }
	  
	  //remove activity at position i from array so it won't be used again
	  //return the consummed activity
	  public int consumeActivity(int i){ 
	   Application.applications[ repartitionArray.get(i) ].subHeight(heightQuantity);
	   return repartitionArray.remove(i);
	  }
	  
	  
	}