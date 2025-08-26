package com.example.stockhelper.utils;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class ControlledBrowserLauncher {

    private Process browserProcess;
    private File tempProfileDir;

    private final String url = "http://localhost:8080"; // your app URL

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() throws IOException {
        // Create a temporary directory for the browser profile
        tempProfileDir = Files.createTempDirectory("chrome-temp-profile").toFile();
        tempProfileDir.deleteOnExit();

        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;

        if (os.contains("win")) {
            pb = new ProcessBuilder(
                    "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
                    "--user-data-dir=" + tempProfileDir.getAbsolutePath(),
                    "--app=" + url
            );
        } else if (os.contains("mac")) {
            pb = new ProcessBuilder(
                    "open",
                    "-a", "Google Chrome",
                    "--args",
                    "--user-data-dir=" + tempProfileDir.getAbsolutePath(),
                    "--app=" + url
            );
        } else {
            // Linux
            pb = new ProcessBuilder(
                    "/usr/bin/google-chrome",
                    "--user-data-dir=" + tempProfileDir.getAbsolutePath(),
                    "--app=" + url
            );
        }

        browserProcess = pb.start();
        System.out.println("Controlled Chrome launched in temporary profile!");
    }

    @PreDestroy
    public void onShutdown() {
        if (browserProcess != null && browserProcess.isAlive()) {
            // Kill child processes too (if any)
            browserProcess.descendants().forEach(ProcessHandle::destroy);
            browserProcess.destroy();
            System.out.println("Controlled Chrome closed on shutdown.");
        }

        // Optional: delete temp profile folder
        if (tempProfileDir != null && tempProfileDir.exists()) {
            deleteDirectory(tempProfileDir);
        }
    }

    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteDirectory(f);
                else f.delete();
            }
        }
        dir.delete();
    }
}
