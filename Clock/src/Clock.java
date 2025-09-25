import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *  This class describes a clock that shows the current date and time.
 *  It does this by using Java Threads, thread priorities and
 *  real-time date/time display
 */
public class Clock {
    private volatile String currentDateTime; // Shared resource between threads
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss MM-dd-yyyy");

    public Clock() {
        updateDateTime();
    }


    /**
     * Updates the current date and time string.
     */
    public void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        currentDateTime = now.format(formatter);
    }

    /**
     * Returns the last updated date and time.
     */
    public String getCurrentDateTime() {
        return currentDateTime;
    }

    /**
     * Starts the clock simulation with two threads:
     * - one for updating
     * - one for displaying
     */
    public void start() {
        Thread updater = new Thread(new TimeUpdaterThread(), "UpdaterThread");
        Thread display = new Thread(new TimeDisplayThread(), "DisplayThread");

        // set thread priorities (1 = lowest, 10 = highest)
        updater.setPriority(Thread.MIN_PRIORITY); // background updater
        display.setPriority(Thread.MAX_PRIORITY); // display must be responsive

        updater.start();
        display.start();
    }

    /**
     * Background thread: updates the time every second.
     */
    private class TimeUpdaterThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                updateDateTime();
                try {
                    Thread.sleep(1000); // update every second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * Foreground thread: prints the time every second on the same line.
     */
    private class TimeDisplayThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                System.out.print("\r" + "Current Time: " + getCurrentDateTime());
                try {
                    Thread.sleep(1000); // print every second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

}
