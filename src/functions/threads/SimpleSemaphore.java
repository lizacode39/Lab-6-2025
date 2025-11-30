package functions.threads;

public class SimpleSemaphore {
    private boolean occupied = false;

    public synchronized void acquire() throws InterruptedException {
        while (occupied) wait();
        occupied = true;
    }

    public synchronized void release() {
        occupied = false;
        notifyAll();
    }
}