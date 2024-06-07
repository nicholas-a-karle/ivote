package com.ivote;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;



public class SimulationDriver {

    protected static int stage;
    protected static int page;
    protected static int pageLength = 10;
    protected static Scanner sc;
    protected static VotingService vs;

    public static void main(String[] args) {

        vs = new VotingService();

        stage = 0;
        page = 0;
        sc = new Scanner(System.in);
        while (stage != -1) {
            userInterface();
        }
        sc.close();
    }

    public static void userInterface() {
        clearConsole();
        int in = 0;
        String strin = "";
        switch (stage) {
            case 0: // Welcome Message
                System.out.println("Welcome to the IVote system Demo\n\n\n\nPlease press any key to continue...");
                sc.nextLine();
                stage = 1;
                break;

            case 1: // Choose Login
                System.out.println("Please choose how you which to login:");
                System.out.println("1.\tUser Login");
                System.out.println("2.\tNew User Login");
                System.out.println("3.\tAdmin Login");
                System.out.println("4.\tExit Program");

                in = Integer.valueOf(sc.nextLine());

                switch (in) {
                    case 1: stage = 2; break;
                    case 2: stage = 3; break;
                    case 3: stage = 4; break;
                    case 4: stage = -1; break;
                    default: break;
                }
                break;

            case 2: // User Login
                System.out.println("Please choose your name and SID from the list:");

                // get said list
                int from = page * pageLength;
                int to = from + pageLength;
                int numStudents = vs.getNumStudents();
                int maxPage = numStudents / pageLength + 1;
                List<Student> students = vs.getStudents(from, to);

                if (numStudents < 1) {
                    System.out.println("There are no students in this database!\n\n\n\nPress any key to revert...");
                    sc.nextLine();
                    stage = 1;
                    break;
                }

                for (int i = 0; i < students.size(); ++i) {
                    System.out.println("" + (i+1) + "\t" + students.get(i).toFullDisplayString());
                }

                if (page < maxPage) {
                    System.out.println("" + (pageLength+1) + "Next Page");
                    if (page > 0) {
                        System.out.println("" + (pageLength+2) + "Prev Page");
                    }
                } else if (page > 0) {
                    System.out.println("" + (pageLength+1) + "Prev Page");
                }

                in = Integer.valueOf(sc.nextLine());;
                if (in == pageLength + 1) {
                    if (page < maxPage) {
                        // Next page
                        ++page;
                    } else if (page > 0) {
                        // Prev page
                        --page;
                    }
                } else if (in == pageLength + 2) {
                    if (page < maxPage && page > 0) {
                        // Prev page
                        --page;
                    }
                } else if (in > 0 && in < pageLength + 1) {
                    // Student logged in with this id

                    vs.loginAsStudent(students.get(in - 1).getId());
                    clearConsole();

                    System.out.println("Logging in as " + vs.getUserDisplayString());
                    System.out.println("Press any key to continue...");
                    sc.nextLine();

                    stage = 10;

                } // else do nothing

                break;

            case 3: // New User Login
                System.out.println("Please enter your name:");

                strin = sc.nextLine();

                vs.loginAsNewStudent(strin);

                clearConsole();

                System.out.println("Logging in as " + vs.getUserDisplayString());
                System.out.println("Press any key to continue...");
                sc.nextLine();

                stage = 10;

                break;

            case 4: // Admin Front page
                System.out.println("Welcome administrator!\nSelect an option:");

                System.out.println("1.\tDisplay system state");
                System.out.println("2.\tAdd student(s)");
                System.out.println("3.\tAdd question(s)");
                System.out.println("4.\tChange database");
                System.out.println("5.\tDisplay Voting Stats");
                System.out.println("6.\tReturn");
                System.out.println("7.\tExit Program");

                in = Integer.valueOf(sc.nextLine());;

                switch (in) {
                    case 1: stage = 5; break;
                    case 2: stage = 6; break;
                    case 3: stage = 7; break;
                    case 4: stage = 8; break;
                    case 5: stage = 9; break;
                    case 6: stage = 1; break;
                    case 7: stage = -1; break;
                    default: break;
                }
                
                break;

            case 5: // Admin state check
                System.out.println("Here is the current state of the program:");
                System.out.println(vs.getStateReport());

                System.out.println("\t\t\t\tPress any key to return...");

                sc.nextLine();

                stage = 4;

            case 6: // Add students
                
            System.out.println("Enter STOP to end.\nEnter the student's name: \n");


                strin = sc.nextLine();
                if (!strin.equals("STOP")) {
                    System.err.println(strin);
                    vs.addStudent(strin);
                } else {
                    stage = 4;
                }

                break;

            case 7: // Add questions

                System.out.println("Enter STOP to end.\nEnter the question text:\n");
                while (strin.equals("")) strin = sc.nextLine();

                if (!strin.equals("STOP")) {
                    String text = strin;
                    List<String> answers = new ArrayList<>();
                    System.out.println("Now enter the answer options, type END to end this process:");
                    while (!strin.equals("END")) {

                        answers.add(strin = sc.nextLine());
                    }
                    vs.addQuestion(text, answers);
                } else {
                    stage = 4;
                }
                break;

            case 8: // Change db
                System.out.println("Enter the new uri: ");
                String uri = sc.nextLine();
                System.out.println("Enter the new dbname: ");
                String dbname = sc.nextLine();
                vs = new VotingService(uri, dbname);

                System.out.println("New Database and Voting Service Initialized.\n\n\n\nPress any key to continue...");
                sc.nextLine();
                
                stage = 4;
                break;

            case 9: // Display statistics
                break;

            case 10: // User front page
                System.out.println("Hello " + vs.getUserName() + "!");
                System.out.println("Please select an option:");

                System.out.println("1.\tStart voting");
                System.out.println("2.\tReturn");
                System.out.println("3.\tExit Program");

                in = Integer.valueOf(sc.nextLine());;

                switch (in) {
                    case 1: stage = 11; break;
                    case 2: stage = 1; break;

                    case 3: stage = -1; break;
                    default: break;
                }
                break;

            case 11: // Voting
                System.out.println("Start of voting... Press any key to continue.");
                sc.nextLine();  
                clearConsole();

                int numQuestions = vs.getNumQuestions();

                for (int i = 0; i < numQuestions; ) {
                    System.out.println(vs.getQuestionDisplayString());
                    strin = sc.nextLine();
                    vs.interpretVoterInput(strin);
                }                

            default:
                stage = -1;
                break;
        }
            
    }

    public static void clearConsole() {
        //System.out.print("\033[H\033[2J");
        //System.out.flush();
    }
}


    