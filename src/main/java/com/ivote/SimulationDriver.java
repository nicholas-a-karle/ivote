package com.ivote;

import java.util.Scanner;

public class SimulationDriver {

    protected static int stage;
    protected static Scanner sc;

    public static void main(String[] args) {
        stage = 0;
        sc = new Scanner(System.in);
        while (stage != -1) {
            userInterface();
        }
        sc.close();
    }

    public static void userInterface() {
        if (stage == 0) {
            System.out.println("Welcome to the IVote system Demo\n\nPlease press any key to continue...");
            sc.nextLine();
            stage = -1;
        }
    }


    
}