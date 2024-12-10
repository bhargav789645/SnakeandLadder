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

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
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
}

class Player {
    private String name;
    private int position;
    private boolean isWinner;

    public Player(String name) {
        this.name = name;
        this.position = 0; // Players start at position 0
        this.isWinner = false;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void moveTo(int newPosition) {
        this.position = newPosition;
    }

    public void declareWinner() {
        this.isWinner = true;
    }
}

class Dice {
    private final Random random;

    public Dice() {
        this.random = new Random();
    }

    public int roll() {
        return random.nextInt(6) + 1; // Rolls a number between 1 and 6
    }
}

class Board {
    private final int size;
    private final Map<Integer, Integer> transitions; // Maps start -> end for snakes/ladders

    public Board(int size) {
        this.size = size;
        this.transitions = new HashMap<>();
    }

    public int getSize() {
        return size;
    }

    public void addSnake(Snake snake) {
        if (snake.getStart() > snake.getEnd()) { // Validating snake position
            transitions.put(snake.getStart(), snake.getEnd());
        }
    }

    public void addLadder(Ladder ladder) {
        if (ladder.getStart() < ladder.getEnd()) { // Validating ladder position
            transitions.put(ladder.getStart(), ladder.getEnd());
        }
    }

    public int getDestination(int position) {
        return transitions.getOrDefault(position, position); // Returns destination if a transition exists
    }
}

class Game {
    private final Board board;
    private final Dice dice;
    private final List<Player> players;
    private final List<Player> winners;

    public Game(Board board, Dice dice, List<Player> players) {
        this.board = board;
        this.dice = dice;
        this.players = players;
        this.winners = new ArrayList<>();
    }

    public void startGame() {
        System.out.println("Game Started!");

        int finalPosition = board.getSize(); // Winning position

        while (winners.size() < players.size()) {
            for (Player player : players) {
                if (player.isWinner()) continue; // Skip if already a winner

                int diceRoll = dice.roll();
                System.out.println(player.getName() + " rolled a " + diceRoll);

                int currentPosition = player.getPosition();
                int tentativePosition = currentPosition + diceRoll;

                if (tentativePosition > finalPosition) {
                    System.out.println(player.getName() + " exceeded the final position. Needs " + (finalPosition - currentPosition) + " to win.");
                    continue;
                }

                int destination = board.getDestination(tentativePosition);
                if (destination < tentativePosition) {
                    System.out.println(player.getName() + " hit a snake! Sliding down to " + destination);
                } else if (destination > tentativePosition) {
                    System.out.println(player.getName() + " climbed a ladder! Moving up to " + destination);
                }

                player.moveTo(destination);
                System.out.println(player.getName() + " moved to position " + player.getPosition());

                if (player.getPosition() == finalPosition) {
                    player.declareWinner();
                    winners.add(player);
                    System.out.println(player.getName() + " has reached the final position and is a winner!");
                }
            }
        }

        System.out.println("Game Over! Winners:");
        for (int i = 0; i < winners.size(); i++) {
            System.out.println((i + 1) + ". " + winners.get(i).getName());
        }
    }
}

public class SnakeAndLadder {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter board size: ");
        int boardSize = scanner.nextInt();
        if (boardSize <= 1) {
            System.out.println("Board size must be greater than 1.");
            return;
        }

        System.out.print("Enter number of snakes: ");
        int numSnakes = scanner.nextInt();

        System.out.print("Enter number of ladders: ");
        int numLadders = scanner.nextInt();

        Board board = new Board(boardSize);

        System.out.println("Enter snake positions (start end): ");
        for (int i = 0; i < numSnakes; i++) {
            int start = scanner.nextInt();
            int end = scanner.nextInt();
            board.addSnake(new Snake(start, end));
        }

        System.out.println("Enter ladder positions (start end): ");
        for (int i = 0; i < numLadders; i++) {
            int start = scanner.nextInt();
            int end = scanner.nextInt();
            board.addLadder(new Ladder(start, end));
        }

        System.out.print("Enter number of players: ");
        int numPlayers = scanner.nextInt();

        System.out.println("Enter player names: ");
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            String name = scanner.next();
            players.add(new Player(name));
        }

        Dice dice = new Dice();
        Game game = new Game(board, dice, players);
        game.startGame();

        scanner.close();
    }
}