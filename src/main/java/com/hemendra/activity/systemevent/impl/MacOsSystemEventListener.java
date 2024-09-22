package com.hemendra.activity.systemevent.impl;

import com.hemendra.activity.systemevent.SystemEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
public class MacOsSystemEventListener implements SystemEventListener {

    /*@Override
    public void listenScreenLockOrUnlockEvent() {
        while (true) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("osascript", "-e",
                        "tell application \"System Events\" to get name of current user");
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();
                if (line == null || line.isEmpty()) {
                    log.info("Screen is locked.");
                } else {
                    log.info("Screen is unlocked.");
                }
                process.waitFor();
                // Sleep for a while before checking again
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }*/

    @Override
    public void listenScreenLockEvent() {
        log.info("Screen lock event received at {}", LocalDateTime.now().toString());
    }

    @Override
    public void listenScreenUnLockEvent() {
        log.info("Screen unlock event received at {}", LocalDateTime.now().toString());
    }

    @Override
    public void listenSystemEvent() {

    }

    @Override
    public void listenShutdownEvent() {

    }

    @Override
    public void systemSleepStartEvent() {
        log.info("System went into sleep at {}", LocalDateTime.now().toString());
    }

    @Override
    public void systemSleepEndEvent() {
        log.info("System wakeup from sleep at {}", LocalDateTime.now().toString());
    }
}
