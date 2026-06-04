package com.artmark;

import com.artmark.cli.ConsoleApp;

//pornirea CLI-ului, toate datele sunt salvate in db si logica in servicii
public class Main {
    public static void main(String[] args) {
        new ConsoleApp().run();
    }
}
