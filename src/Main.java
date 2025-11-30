import functions.Functions;
import functions.basic.Log;
import functions.threads.*;

public class Main {

    // Задание 2: последовательная версия (без потоков)
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

    // Задание 3: простая многопоточная версия (с wait/notify)
    public static void simpleThreads() {
        Task task = new Task(100);
        Thread generator = new Thread(new SimpleGenerator(task));
        Thread integrator = new Thread(new SimpleIntegrator(task));

        generator.start();
        integrator.start();

        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Задание 4: сложная многопоточная версия (с собственным семафором)
    public static void complicatedThreads() throws InterruptedException {
        Task task = new Task(100);

        SimpleSemaphore empty = new SimpleSemaphore(); // можно писать
        SimpleSemaphore full = new SimpleSemaphore();  // можно читать

        // Делаем full занятым, чтобы интегратор ждал
        full.acquire();

        Generator generator = new Generator(task, empty, full);
        Integrator integrator = new Integrator(task, empty, full);

        generator.start();
        integrator.start();

        // Требование задания: ждать 50 мс, затем прервать
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

        // nonThread();          // ← последовательный режим

       //  simpleThreads();      // ← многопоточный с wait/notify
    }
}