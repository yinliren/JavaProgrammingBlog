# Using Lock Interface to Protect Multithreading Security in Java
[Source Code](https://github.com/yinliren/JavaProgrammingBlog/blob/master/Java%20Concurrency/LockTest.java)
First of all, let's see a snippet of code to simulate a ticket selling program in multithreading.

```package Test;
class TicketWindow implements Runnable{
 private int ticket = 30;
 @Override
 public void run() {
  while (true) {
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
```
Run it and here's the result:
```Window1: Sold Ticket, Ticket Number is: 30
Window3: Sold Ticket, Ticket Number is: 29
Window2: Sold Ticket, Ticket Number is: 29
Window3: Sold Ticket, Ticket Number is: 27
Window2: Sold Ticket, Ticket Number is: 27
Window1: Sold Ticket, Ticket Number is: 27
Window1: Sold Ticket, Ticket Number is: 24
Window3: Sold Ticket, Ticket Number is: 24
Window2: Sold Ticket, Ticket Number is: 24
Window1: Sold Ticket, Ticket Number is: 21
Window3: Sold Ticket, Ticket Number is: 20
Window2: Sold Ticket, Ticket Number is: 20
Window3: Sold Ticket, Ticket Number is: 18
Window1: Sold Ticket, Ticket Number is: 18
Window2: Sold Ticket, Ticket Number is: 18
Window1: Sold Ticket, Ticket Number is: 15
Window3: Sold Ticket, Ticket Number is: 15
Window2: Sold Ticket, Ticket Number is: 13
Window1: Sold Ticket, Ticket Number is: 12
Window3: Sold Ticket, Ticket Number is: 11
Window2: Sold Ticket, Ticket Number is: 10
Window1: Sold Ticket, Ticket Number is: 9
Window2: Sold Ticket, Ticket Number is: 9
Window3: Sold Ticket, Ticket Number is: 9
Window1: Sold Ticket, Ticket Number is: 6
Window2: Sold Ticket, Ticket Number is: 5
Window3: Sold Ticket, Ticket Number is: 4
Window1: Sold Ticket, Ticket Number is: 3
Window3: Sold Ticket, Ticket Number is: 3
Window2: Sold Ticket, Ticket Number is: 3
Window1: Sold Ticket, Ticket Number is: 0
Window3: Sold Ticket, Ticket Number is: -1
```
It turns out that there're some tickets are sold by the same window, and there's an invalid ticket number in the end.
To protect the security of the multithreading program, we need to use the lock to make sure that while one thread is executing, the others will wait. Here's the code:


```package Test;
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
```
Result:

```Window1: Sold Ticket, Ticket Number is: 30
Window2: Sold Ticket, Ticket Number is: 29
Window3: Sold Ticket, Ticket Number is: 28
Window1: Sold Ticket, Ticket Number is: 27
Window2: Sold Ticket, Ticket Number is: 26
Window3: Sold Ticket, Ticket Number is: 25
Window1: Sold Ticket, Ticket Number is: 24
Window2: Sold Ticket, Ticket Number is: 23
Window3: Sold Ticket, Ticket Number is: 22
Window1: Sold Ticket, Ticket Number is: 21
Window2: Sold Ticket, Ticket Number is: 20
Window3: Sold Ticket, Ticket Number is: 19
Window1: Sold Ticket, Ticket Number is: 18
Window2: Sold Ticket, Ticket Number is: 17
Window3: Sold Ticket, Ticket Number is: 16
Window1: Sold Ticket, Ticket Number is: 15
Window2: Sold Ticket, Ticket Number is: 14
Window3: Sold Ticket, Ticket Number is: 13
Window1: Sold Ticket, Ticket Number is: 12
Window2: Sold Ticket, Ticket Number is: 11
Window3: Sold Ticket, Ticket Number is: 10
Window1: Sold Ticket, Ticket Number is: 9
Window2: Sold Ticket, Ticket Number is: 8
Window3: Sold Ticket, Ticket Number is: 7
Window1: Sold Ticket, Ticket Number is: 6
Window2: Sold Ticket, Ticket Number is: 5
Window3: Sold Ticket, Ticket Number is: 4
Window1: Sold Ticket, Ticket Number is: 3
Window2: Sold Ticket, Ticket Number is: 2
Window3: Sold Ticket, Ticket Number is: 1
```
