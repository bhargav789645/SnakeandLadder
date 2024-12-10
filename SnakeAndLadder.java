import java.util.*;

class Snake {
    private int start;
    private int end;

    public Snake(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isValid() {
        return start > end; // A snake must have a start position greater than its end
    }
}

class Ladder {
    private int start;
    private int end;

    public Ladder(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isValid() {
        return start < end; // A ladder must have a start position less than its end
    }
}

class Player {
    private String name;
    private int position;
    private boolean hasShield;

    public Player(String name) {
        this.name = name;
        this.position = 1;  // Players start at position 1
        this.hasShield = false;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public boolean hasShield() {
        return hasShield;
    }

    public void pickShield() {
        this.hasShield = true;
    }

    public void useShield() {
        this.hasShield = false;
    }

    public void moveTo(int newPosition) {
        this.position = newPosition;
    }
}

class Dice {
    private Random random;
    private int diceType;

    public Dice() {
        random = new Random();
        this.diceType = 1;  // Default unbiased dice
    }

    public void setDiceType(int diceType) {
        this.diceType = diceType;
    }

    public int roll() {
        switch (diceType) {
            case 1: // Unbiased dice (normal dice)
                return random.nextInt(6) + 1;
            case 2: // Biased dice (more likely to roll higher numbers)
                return random.nextInt(4) + 3;  // Biased towards 3-6
            case 3: // Unfair dice (always rolls 1)
                return 1;
            default:
                return random.nextInt(6) + 1;  // Default to unbiased dice
        }
    }
}

class Board {
    private int size;
    private Map<Integer, Integer> transitions; // Maps start -> end (snakes & ladders)
    private int shieldPosition;

    public Board(int size) {
        this.size = size;
        this.transitions = new HashMap<>();
        this.shieldPosition = -1;
    }

    public void addSnake(Snake snake) {
        if (snake.isValid()) {
            transitions.put(snake.getStart(), snake.getEnd());
        }
    }

    public void addLadder(Ladder ladder) {
        if (ladder.isValid()) {
            transitions.put(ladder.getStart(), ladder.getEnd());
        }
    }

    public int getDestination(int position) {
        return transitions.getOrDefault(position, position);
    }

    public void setShieldPosition(int position) {
        this.shieldPosition = position;
    }

    public int getShieldPosition() {
        return shieldPosition;
    }
}

public class SnakeAndLadder {
    private static final Scanner scanner = new Scanner(System.in);

    private static int getValidInput(String prompt) {
        int value = -1;
        while (value <= 0) {
            System.out.print(prompt);
            try {
                value = scanner.nextInt();
                if (value <= 0) {
                    System.out.println("Please enter a positive number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next(); // Clear the invalid input
            }
        }
        return value;
    }

    private static int generateValidShieldPosition(Board board, int boardSize) {
        Random random = new Random();
        int shieldPosition;
        do {
            shieldPosition = random.nextInt(boardSize - 1) + 1;
        } while (board.getDestination(shieldPosition) != shieldPosition); // Ensure shield not on a snake or ladder
        return shieldPosition;
    }

    public static void main(String[] args) {
        // Get valid inputs
        int boardSize = getValidInput("Enter board size: ");
        int numSnakes = getValidInput("Enter number of snakes: ");
        int numLadders = getValidInput("Enter number of ladders: ");
        int numPlayers = getValidInput("Enter number of players: ");

        // Create the board
        Board board = new Board(boardSize);

        // Adding snakes using Collections
        List<Snake> snakes = new ArrayList<>();
        for (int i = 0; i < numSnakes; i++) {
            int start = getValidInput("Enter snake start position: ");
            int end = getValidInput("Enter snake end position: ");
            Snake snake = new Snake(start, end);
            if (snake.isValid()) {
                board.addSnake(snake);
                snakes.add(snake);
            } else {
                System.out.println("Invalid snake. Skipping...");
            }
        }

        // Adding ladders using Collections
        List<Ladder> ladders = new ArrayList<>();
        for (int i = 0; i < numLadders; i++) {
            int start = getValidInput("Enter ladder start position: ");
            int end = getValidInput("Enter ladder end position: ");
            Ladder ladder = new Ladder(start, end);
            if (ladder.isValid()) {
                board.addLadder(ladder);
                ladders.add(ladder);
            } else {
                System.out.println("Invalid ladder. Skipping...");
            }
        }

        // Generate shield position using Collections
        int shieldPosition = generateValidShieldPosition(board, boardSize);
        board.setShieldPosition(shieldPosition);

        // Create players using Collections
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for player " + (i + 1) + ": ");
            String playerName = scanner.next();
            players.add(new Player(playerName));
        }

        // Select Dice Type
        System.out.println("Select Dice Type:");
        System.out.println("1: Unbiased Dice");
        System.out.println("2: Biased Dice");
        System.out.println("3: Unfair Dice");
        int diceChoice = getValidInput("Enter your choice (1/2/3): ");
        Dice dice = new Dice();
        dice.setDiceType(diceChoice);

        // Start the game
        List<Player> winners = new ArrayList<>();
        while (winners.isEmpty()) {
            for (Player player : players) {
                if (winners.contains(player)) {
                    continue; // Skip already winner players
                }

                // Roll the dice
                int diceRoll = dice.roll();
                System.out.println(player.getName() + " rolled a " + diceRoll);

                int oldPosition = player.getPosition();
                int tentativePosition = oldPosition + diceRoll;

                if (tentativePosition > boardSize) {
                    tentativePosition = oldPosition; // Stay in the same position if it exceeds the board size
                    System.out.println(player.getName() + " exceeds the board size and stays at " + oldPosition);
                }

                // Check for shield pickup
                if (tentativePosition == board.getShieldPosition() && !player.hasShield()) {
                    player.pickShield();
                    System.out.println(player.getName() + " picked up a shield!");
                }

                // Check for snakes or ladders
                int finalPosition = board.getDestination(tentativePosition);

                if (finalPosition != tentativePosition) {
                    System.out.println(player.getName() + " encountered a snake or ladder!");
                }

                player.moveTo(finalPosition);
                System.out.println(player.getName() + " moved to position " + player.getPosition());

                // Check if the player reached the final position (100 or board size)
                if (player.getPosition() == boardSize) {
                    winners.add(player);
                    System.out.println(player.getName() + " has won the game!");
                }
            }
        }

        // Display winners
        System.out.println("\nGame Over! Winners:");
        for (Player winner : winners) {
            System.out.println(winner.getName() + " at position " + winner.getPosition());
        }

        // Close the scanner
        scanner.close();
    }
}
