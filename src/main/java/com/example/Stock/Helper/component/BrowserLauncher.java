package com.example.Stock.Helper.component;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BrowserLauncher {
    @EventListener(ApplicationReadyEvent.class)
    public void launchBrowser() {
        try { Desktop.getDesktop().browse(new URI("http://localhost:8080"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
