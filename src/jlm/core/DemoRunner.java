package jlm.core;

import java.util.Iterator;
import java.util.List;

import jlm.lesson.Exercise;


public class DemoRunner extends Thread {

	private Game game;
	private List<Thread> runners = null; // threads who run entities from lesson

	public DemoRunner(Game game, List<Thread> list) {
		super();
		this.game = game;
		this.runners = list;
		this.runners.add(this);
	}

	@Override
	public void run() {
		boolean stepModeWasActivated = this.game.stepModeEnabled();

		try {
			Exercise exo = this.game.getCurrentLesson().getCurrentExercise();

			game.setState(GameState.DEMO_STARTED);
			
			this.game.disableStepMode();
			
			exo.runDemo(runners);

			Iterator<Thread> it = runners.iterator();
			while (it.hasNext()) {
				Thread t = it.next();
				if (!t.equals(this)) { /* do not wait for myself */
					t.join();
					it.remove();
				}
			}
		} catch (InterruptedException e) {
			game.getOutputWriter().log(e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stepModeWasActivated) {
				this.game.enableStepMode();
			}
			game.setState(GameState.DEMO_ENDED);			
		}

		runners.remove(this);
	}

}
