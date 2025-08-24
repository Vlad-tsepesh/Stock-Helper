package com.example.Stock.Helper.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HelloCommand {

    @ShellMethod(key = "hello", value = "I will say hello")
    public String hello(@ShellOption(defaultValue = "/bike.jpg") String arg) {
        return arg;
    }
}
