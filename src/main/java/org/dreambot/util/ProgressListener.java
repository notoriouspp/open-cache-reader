package org.dreambot.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Robert.
 * Time :   14:50.
 */
public class ProgressListener {

    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    public final AtomicInteger currentProgress;
    public final AtomicInteger finalProgress;
    public final String info;
    private float lastPercent;


    /**
     * The progress listener utility.
     *
     * @param currentProgress - the reference to current progress state.
     * @param finalProgress   - the reference to final point aka 100%.
     * @param info            - the information text about this progress.
     */
    public ProgressListener(AtomicInteger currentProgress, AtomicInteger finalProgress, String info) {
        this.currentProgress = currentProgress;
        this.finalProgress = finalProgress;
        this.info = info;
    }

    /**
     * Calculates percent of current progress.
     *
     * @return the current percent.
     */
    public float calcPercent() {
        return ((float) currentProgress.get() / (this.finalProgress.get())) * 100f;
    }

    /**
     * Prints progress to console.
     */
    public void printToConsole(float spacing) {
        float currentPercent = calcPercent();
        if (currentPercent - lastPercent >= spacing) {
            System.out.printf(info + ": %% %.2f%% \n", lastPercent);
            lastPercent = currentPercent;
        }
    }

}
