package com.artmark.cli;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;


//wrapper pentru scanner si valideaza inputul
public class ConsoleReader {
    private final Scanner scanner = new Scanner(System.in);

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // pana cand am un raspuns
    public String readNonEmpty(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) return value;
            System.out.println("Campul nu poate fi gol.");
        }
    }

    public int readInt(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Introdu un numar intreg valid.");
            }
        }
    }

    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) return value;
            System.out.println("Alege un numar intre " + min + " si " + max + ".");
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                System.out.println("Introdu un numar valid (ex. 1500 sau 1500.50).");
            }
        }
    }

    // afiseaza o lista numerotata si lasa userul sa aleaga un element dupa numar
    // eticheta  spune cum se afiseaza fiecare element
    public <T> T alegeDinLista(String prompt, List<T> lista, Function<T, String> eticheta) {
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + eticheta.apply(lista.get(i)));
        }
        int idx = readIntInRange(prompt, 1, lista.size());
        return lista.get(idx - 1);
    }

    public void close() {
        scanner.close();
    }
}
