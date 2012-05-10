package com.artemis.utils;

public abstract class Timer {

	private int delay;
	private boolean repeat;
	private int acc;
	private boolean done;
	private boolean stopped;

	public Timer(int delay, boolean repeat) {
		this.delay = delay;
		this.repeat = repeat;
		this.acc = 0;
	}

	public void update(int delta) {
		if (!done && !stopped) {
			acc += delta;

			if (acc >= delay) {
				acc -= delay;

				if (repeat) {
					reset();
				} else {
					done = true;
				}

				execute();
			}
		}
	}

	public void reset() {
		stopped = false;
		done = false;
		acc = 0;
	}

	public boolean isDone() {
		return done;
	}

	public boolean isRunning() {
		return !done && acc < delay && !stopped;
	}

	public void stop() {
		stopped = true;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public abstract void execute();

	public float getPercentageRemaining() {
		if (done)
			return 100;
		else if (stopped)
			return 0;
		else
			return 1 - (float) (delay - acc) / (float) delay;
	}

	public int getDelay() {
		return delay;
	}

}
