import functions.Functions;
import functions.basic.Log;
import functions.threads.*;

public class Main {

    public static void nonThread() {
        Task task = new Task(100);
        for (int i = 0; i < task.getTaskCount(); i++) {
            double base = 1 + Math.random() * 9;
            double left = Math.random() * 100;
            double right = 100 + Math.random() * 100;
            double dx = Math.random();

            task.setFunction(new Log(base));
            task.setLeftX(left);
            task.setRightX(right);
            task.setDx(dx);

            System.out.println("Source " + left + " " + right + " " + dx);
            double result = Functions.integral(task.getFunction(), left, right, dx);
            System.out.println("Result " + left + " " + right + " " + dx + " " + result);
        }
    }

    public static void simpleThreads() {
        Task task = new Task(100);
        Thread gen = new Thread(new SimpleGenerator(task));
        Thread integ = new Thread(new SimpleIntegrator(task));

        gen.setPriority(Thread.MIN_PRIORITY);
        integ.setPriority(Thread.MIN_PRIORITY);

        gen.start();
        integ.start();

        try {
            gen.join();
            integ.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void complicatedThreads() throws InterruptedException {
        Task task = new Task(100);

        SimpleSemaphore empty = new SimpleSemaphore(); // изначально свободен — можно писать
        SimpleSemaphore full = new SimpleSemaphore();  // изначально свободен, но сразу займём

        full.acquire();

        Generator generator = new Generator(task, empty, full);
        Integrator integrator = new Integrator(task, empty, full);

        generator.start();
        integrator.start();

        Thread.sleep(50);
        generator.interrupt();
        integrator.interrupt();

        generator.join(100);
        integrator.join(100);
    }

    public static void main(String[] args) {
        try {
            complicatedThreads();
        } catch (InterruptedException e) {
            System.err.println("Main thread was interrupted");
            Thread.currentThread().interrupt();
        }
    }
}