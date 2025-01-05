import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {
    public static void main(String[] args) {
        // Create objects for random number generation and input reading
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        // Generate a random number between 1 and 100
        int numberToGuess = random.nextInt(100) + 1;
        int numberOfAttempts = 5;
        int userGuess = 0;

        System.out.println("Welcome to the Number Guessing Game!");
        System.out.println("I've picked a number between 1 and 100.");
        System.out.println("You have " + numberOfAttempts + " attempts to guess it.");

        // Game loop
        for (int i = 1; i <= numberOfAttempts; i++) {
            System.out.print("Attempt " + i + ": Enter your guess: ");
            userGuess = scanner.nextInt();

            // Check the user's guess
            if (userGuess == numberToGuess) {
                System.out.println("Congratulations! You guessed the number correctly.");
                break;
            } else if (userGuess < numberToGuess) {
                System.out.println("Too low! Try again.");
            } else {
                System.out.println("Too high! Try again.");
            }

            // Inform user of remaining attempts
            if (i == numberOfAttempts) {
                System.out.println("Sorry, you've run out of attempts. The number was " + numberToGuess + ".");
            }
        }

        scanner.close();
    }
}
