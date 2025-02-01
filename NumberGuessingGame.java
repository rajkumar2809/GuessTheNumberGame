import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {
    static final String GREEN = "\u001B[32m";
    static final String RED = "\u001B[31m";
    static final String BLUE = "\u001B[34m";
    static final String YELLOW = "\u001B[33m";
    static final String CYAN = "\u001B[36m";
    static final String RESET = "\u001B[0m";
    static final String HIGHSCORE_FILE = "highscores.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        boolean playAgain;

        System.out.println(YELLOW + "\nWelcome to the Ultimate Number Guessing Game!" + RESET);

        // Ask for user name with validation
        String userName = getUserName(scanner);
        System.out.println("\nHello, " + GREEN + userName + RESET + "! Let's begin the game.");

        do {
            int attempts = chooseDifficulty(scanner);
            int numberToGuess = random.nextInt(100) + 1;
            int score = 100;
            boolean guessedCorrectly = false;
            List<Integer> previousGuesses = new ArrayList<>();

            System.out.println("\nI've picked a number between 1 and 100.");
            System.out.println("You have " + attempts + " attempts to guess it.");

            // Trigger random event once, after the number is picked
            triggerInitialRandomEvent(numberToGuess);

            long startTime = System.currentTimeMillis(); // Track start time
            for (int i = 1; i <= attempts; i++) {
                int userGuess = getUserGuess(scanner, previousGuesses);
                previousGuesses.add(userGuess);

                if (userGuess == numberToGuess) {
                    long endTime = System.currentTimeMillis(); // Track end time
                    double timeTakenInSeconds = (endTime - startTime) / 1000.0;
                    System.out.println(
                            GREEN + "\nCongratulations! You guessed the number in " + i + " attempts!" + RESET);
                    System.out.println(
                            "Time taken: " + CYAN + String.format("%.2f", timeTakenInSeconds) + " seconds" + RESET);
                    guessedCorrectly = true;
                    saveHighScore(userName, score, timeTakenInSeconds);
                    break;
                } else {
                    if (userGuess < numberToGuess) {
                        System.out.println(RED + "Too low! Try again." + RESET);
                    } else {
                        System.out.println(RED + "Too high! Try again." + RESET);
                    }

                    // Dynamic feedback based on closeness to the number
                    if (Math.abs(userGuess - numberToGuess) <= 5) {
                        System.out.println(CYAN + "You're very close! Keep it up!" + RESET);
                    } else if (Math.abs(userGuess - numberToGuess) <= 10) {
                        System.out.println(CYAN + "You're getting closer! Try again!" + RESET);
                    }
                }

                score = Math.max(0, score - 10); // Ensure score does not go below zero
            }

            if (!guessedCorrectly) {
                System.out.println(
                        RED + "\nSorry, you've run out of attempts. The number was " + numberToGuess + "." + RESET);
            }

            displayHighScores();
            displayGameStatistics();
            System.out.println("\nDo you want to play again? (yes/no): ");
            playAgain = scanner.next().equalsIgnoreCase("yes");

        } while (playAgain);

        System.out.println("\nThank you for playing! Goodbye!");
        scanner.close();
    }

    private static String getUserName(Scanner scanner) {
        String userName;
        while (true) {
            System.out.print("\nEnter your name: ");
            userName = scanner.nextLine().trim();

            // Validate that the name contains only letters
            if (userName.matches("[a-zA-Z]+")) {
                break;
            } else {
                System.out.println(RED + "Invalid name! Please enter only alphabetic characters." + RESET);
            }
        }
        return userName;
    }

    private static int chooseDifficulty(Scanner scanner) {
        System.out.println(BLUE + "\nChoose difficulty level: " + RESET);
        System.out.println("1. Easy (10 attempts)\n2. Medium (7 attempts)\n3. Hard (5 attempts)");
        int choice;
        while (true) {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice >= 1 && choice <= 3)
                    break;
            } else {
                scanner.next(); // Consume invalid input
            }
            System.out.println("Invalid choice! Please enter 1, 2, or 3.");
        }
        return (choice == 1) ? 10 : (choice == 2) ? 7 : 5;
    }

    private static int getUserGuess(Scanner scanner, List<Integer> previousGuesses) {
        int guess;
        while (true) {
            System.out.print("\nEnter your guess: ");
            if (scanner.hasNextInt()) {
                guess = scanner.nextInt();
                if (guess >= 1 && guess <= 100 && !previousGuesses.contains(guess))
                    return guess;
                System.out.println(RED + "You've already guessed this number! Try a different one." + RESET);
            } else {
                scanner.next(); // Consume invalid input
            }
            System.out.println("Invalid input! Please enter a number between 1 and 100.");
        }
    }

    // Trigger special event once after number is picked
    private static void triggerInitialRandomEvent(int numberToGuess) {
        Random random = new Random();
        int eventType = random.nextInt(6); // Now we have 6 possibilities
        switch (eventType) {
            case 0:
                System.out.println(CYAN + "\nSpecial Event: You gain an extra attempt! 🎉" + RESET);
                break;
            case 1:
                if (numberToGuess % 5 == 0) {
                    System.out.println(YELLOW + "\nSurprise Event: The number is a multiple of 5!" + RESET);
                }
                break;
            case 2:
                System.out.println(RED + "\nPenalty: You lose 10 points for a wrong guess!" + RESET);
                break;
            case 3:
                System.out.println(
                        GREEN + "\nHint: The number is " + (numberToGuess % 2 == 0 ? "even" : "odd") + "." + RESET);
                break;
            case 4:
                System.out
                        .println(RED + "\nYou accidentally guessed the wrong number! You'll lose some points!" + RESET);
                break;
            case 5:
                System.out.println(CYAN + "\nSpecial Hint: The number is between " + (numberToGuess - 10) + " and "
                        + (numberToGuess + 10) + "." + RESET);
                break;
            default:
                break;
        }
    }

    private static void saveHighScore(String userName, int score, double timeTakenInSeconds) {
        try {
            List<String> scores = new ArrayList<>();
            File file = new File(HIGHSCORE_FILE);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        scores.add(line);
                    }
                }
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            // Save player name, score, time taken, and datetime with desired formatting
            scores.add(userName + " | " + score + " | " + String.format("%.2f", timeTakenInSeconds) + " seconds | "
                    + dateFormat.format(date));

            // Sort scores by time and take top 5 (or fewer if there are not enough entries)
            scores.sort(Collections.reverseOrder());
            try (FileWriter writer = new FileWriter(HIGHSCORE_FILE)) {
                for (int i = 0; i < Math.min(5, scores.size()); i++) {
                    writer.write(scores.get(i) + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving score.");
        }
    }

    private static void displayHighScores() {
        System.out.println(YELLOW + "\n--- High Scores ---" + RESET);
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(GREEN + line + RESET);
            }
        } catch (IOException e) {
            System.out.println("No high scores recorded yet.");
        }
    }

    private static void displayGameStatistics() {
        List<String> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(line);
            }
        } catch (IOException e) {
            System.out.println("No game statistics recorded yet.");
        }

        if (!scores.isEmpty()) {
            System.out.println(YELLOW + "\n--- Game Statistics ---" + RESET);
            System.out.println("Top High Scores:");
            scores.stream().limit(5).forEach(score -> System.out.println(GREEN + score + RESET));
        } else {
            System.out.println(RED + "\nNo scores available for statistics." + RESET);
        }
    }
}
