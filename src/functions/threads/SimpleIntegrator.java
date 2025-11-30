package functions.threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable{
    private Task task;

    public SimpleIntegrator(Task task) {this.task = task;}

    @Override
    public void run() {
        for (int i = 0; i < task.getTaskCount(); i++) {
            synchronized (task) {
                if (task.getFunction() == null) {
                    try {
                        task.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Thread was interrupted");
                    }
                }
                System.out.println("Result " + task.getLeftX() + ' ' + task.getRightX() + ' ' + task.getDx() + ' '
                        + Functions.integral(task.getFunction(), task.getLeftX(), task.getRightX(), task.getDx()));
                task.setFunction(null);

                task.notify();
            }
        }
    }
}
