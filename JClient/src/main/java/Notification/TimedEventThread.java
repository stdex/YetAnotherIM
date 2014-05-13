package Notification;

public class TimedEventThread extends Thread {
	
	int milis;
	TimedEventCallback theCallback;
	
	public TimedEventThread(int time, TimedEventCallback theCB) {
		milis = time;
		theCallback = theCB;
	}
	
	public void run() {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		theCallback.timeUp();
	}
}
