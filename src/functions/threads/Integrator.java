package functions.threads;

import functions.Functions;

public class Integrator extends Thread {
    private Task task;
    private SimpleSemaphore empty; // место свободно для записи
    private SimpleSemaphore full;  // данные готовы для чтения

    public Integrator(Task task, SimpleSemaphore empty, SimpleSemaphore full) {
        this.task = task;
        this.empty = empty;
        this.full = full;
    }

    @Override
    public void run() {
        for (int i = 0; i < task.getTaskCount(); i++) {
            try {
                full.acquire(); // ждём, пока генератор запишет данные

                System.out.println("Result " + task.getLeftX() + " " + task.getRightX() + " " + task.getDx() + " "
                        + Functions.integral(task.getFunction(), task.getLeftX(), task.getRightX(), task.getDx()));

                empty.release(); // освобождаем место для следующей записи
            } catch (InterruptedException e) {
                System.out.println("Integrator was interrupted");
                return;
            }
        }
    }
}