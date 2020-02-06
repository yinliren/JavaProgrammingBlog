package Test;

import java.util.concurrent.locks.ReentrantLock;
// Using Lock to solve ---> JDK5.0 NEW
class TicketWindow implements Runnable{
	private int ticket = 30;
	//Instantiate a lock
	private ReentrantLock lock = new ReentrantLock(true);
	@Override
	public void run() {
		while (true) {
			try {
				//lock the thread
				lock.lock();
				if (ticket > 0) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName() + ": Sold Ticket, Ticket Number is: " + ticket);
					ticket--;
				}
				else {
					break;
				}
			}
			finally {
				//unlock the thread
				lock.unlock();
			}
		}
	}
	
}


public class LockTest {

	public static void main(String[] args) {
		TicketWindow w = new TicketWindow();
		Thread t1 = new Thread(w);
		Thread t2 = new Thread(w);
		Thread t3 = new Thread(w);
		
		t1.setName("Window1");
		t2.setName("Window2");
		t3.setName("Window3");
		
		t1.start();
		t2.start();
		t3.start();
	}
}
