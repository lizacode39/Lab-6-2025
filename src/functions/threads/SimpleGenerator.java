package functions.threads;

import functions.Functions;
import functions.basic.Log;

public class SimpleGenerator implements Runnable{
    private Task task;

    public SimpleGenerator(Task task) {this.task = task;}

    @Override
    public void run() {
        for (int i = 0; i < task.getTaskCount(); i++) {
            synchronized (task){
                if (task.getFunction() != null){
                    try{
                        task.wait();
                    }
                    catch (InterruptedException e){
                        System.out.println("Thread was interrupted");
                    }
                }
                task.setFunction(new Log(1 + Math.random()*9));
                task.setLeftX(Math.random()*100);
                task.setRightX(100 + Math.random()*100);
                task.setDx(Math.random());
                System.out.println("Source " + task.getLeftX() + ' ' + task.getRightX() + ' ' + task.getDx());

                task.notify();
            }
        }
    }
}
