package turtleart.flower3;

import plm.core.model.lesson.ExerciseTemplated;

import plm.core.utils.FileUtils;
import plm.universe.World;
import plm.universe.turtles.Turtle;
import plm.universe.turtles.TurtleWorld;

public class Flower3 extends ExerciseTemplated {
	
	public Flower3(FileUtils fileUtils) {
		
		super("Flower3");

		World myWorld = new TurtleWorld(fileUtils, "Flower3", 300, 300);
		Turtle t = new Turtle(myWorld, "Hawksbill", 150, 150);
		t.setHeading(-90);
		setup(myWorld);
	}
}
