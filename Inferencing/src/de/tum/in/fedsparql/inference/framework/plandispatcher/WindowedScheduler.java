package de.tum.in.fedsparql.inference.framework.plandispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import de.tum.in.fedsparql.inference.framework.graph.DependencyGraph;
import de.tum.in.fedsparql.inference.io.Dispatcher;
import de.tum.in.fedsparql.inference.io.IO;
import de.tum.in.fedsparql.inference.io.Monitoring;

public abstract class WindowedScheduler extends Scheduler {

	public class Window {
		public ThreadInfo[] threads;

		public Window(ThreadInfo[] threads) {
			this.threads = threads;
		}
	}

	public WindowedScheduler(DependencyGraph collection, IO io,
			Monitoring monitoring, Dispatcher dispatcher) {
		super(collection, io, monitoring, dispatcher);
		// TODO Auto-generated constructor stub
	}

	private Queue<ThreadInfo> waitingThreads = new LinkedBlockingQueue<ThreadInfo>();
	private Timer timer = new Timer();
	private boolean timerStarted = false;

	protected int windowDelay = 1000;

	protected abstract void scheduleWindow(Window window);

	@Override
	public void schedule(ThreadInfo threadInfo) {
		synchronized (waitingThreads) {
			waitingThreads.add(threadInfo);
			if (!timerStarted) {
				timerStarted = true;
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						timerStarted = false;

						List<ThreadInfo> threadList = new ArrayList<ThreadInfo>();
						synchronized (waitingThreads) {
							while (!waitingThreads.isEmpty()) {
								ThreadInfo threadInfo = waitingThreads.poll();
								threadList.add(threadInfo);
							}
						}

						Window window = new Window(threadList.toArray(new ThreadInfo[threadList.size()]));

						scheduleWindow(window);
					}
				}, windowDelay);
			}
		}
	}

	@Override
	public void dispose() {
		timer.cancel();
	}
}
