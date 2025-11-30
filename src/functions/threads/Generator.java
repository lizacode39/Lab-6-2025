package functions.threads;

import functions.basic.Log;

public class Generator extends Thread {
    private Task task;
    private SimpleSemaphore empty; // место свободно для записи
    private SimpleSemaphore full;  // данные готовы для чтения

    public Generator(Task task, SimpleSemaphore empty, SimpleSemaphore full) {
        this.task = task;
        this.empty = empty;
        this.full = full;
    }

    @Override
    public void run() {
        for (int i = 0; i < task.getTaskCount(); i++) {
            try {
                empty.acquire(); // ждём, пока интегратор освободит место

                task.setFunction(new Log(1 + Math.random() * 9));
                task.setLeftX(Math.random() * 100);
                task.setRightX(100 + Math.random() * 100);
                task.setDx(Math.random());
                System.out.println("Source " + task.getLeftX() + " " + task.getRightX() + " " + task.getDx());

                full.release(); // данные готовы
            } catch (InterruptedException e) {
                System.out.println("Generator was interrupted");
                return;
            }
        }
    }
}