package lessons.welcome;

import jlm.lesson.ExerciseTemplated;
import jlm.lesson.Lesson;
import universe.turtles.Turtle;
import universe.turtles.TurtleWorld;

public class Square extends ExerciseTemplated {

	public Square(Lesson lesson) {
		super(lesson);
		name = "Hello, tortues";

		/* Create initial situation */
		TurtleWorld myWorld = new TurtleWorld("WhiteBoard", 400, 400);

		new Turtle(myWorld, "Hawksbill", 200, 200);
		setup(myWorld);
	}
}
