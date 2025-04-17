import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;

abstract class AbstructGame {
    protected abstract void setPlayers(Player player1, Player player2);
    abstract boolean gameOver();
    abstract boolean move(Pair<String, String> from, Pair<String, String> to);
}

class ChessGame extends AbstructGame {
    private Player player1;
    private Player player2;
    private ChessBoard board;
    private boolean isGameOver;
    private int turn = 0;

    // Constructor
    public ChessGame(Player player1, Player player2) {
        this.board = new ChessBoard();
        this.isGameOver = false;
        setPlayers(player1, player2);
        generateChess();
    }

    void start() {
        Scanner scanner = new Scanner(System.in);
        
        while (gameOver() == false) {
            try {
                showAllChess();
                System.out.println("請輸入棋盤座標（例如：A2 B2）：");
                String[] input = scanner.nextLine().split(" ");
                if (input.length != 2) {
                    System.out.println("Invalid input.");
                    continue;
                }
                move(new Pair<String, String>(input[0].substring(0, 1), input[0].substring(1)),
                        new Pair<String, String>(input[1].substring(0, 1), input[1].substring(1)));
                checkGameOver();
            } catch (Exception e) {
                break;
            }
        }

        System.out.println("===================================");
        System.out.println("Game Over!");
        if (player1.getChessCount() == 0 || player1.getChessCount() < player2.getChessCount()) {
            System.out.println("Player 2 wins!");
        } else if (player2.getChessCount() == 0 || player1.getChessCount() > player2.getChessCount()) {
            System.out.println("Player 1 wins!");
        } else {
            System.out.println("Draw!");
        }
        scanner.close();
    }

    void showAllChess() {
        System.out.println("===================================");
        System.out.format("It is %s's turn.\n", (turn == 0) ? player1.name : player2.name);
        System.out.format("[%s vs. %s: %d/%d]\n", player1.name, player2.name, player1.getChessCount(), player2.getChessCount());
        board.printBoard();
    }

    void generateChess() {
        board.setUpInitialPieces();
    }

    protected void setPlayers(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    boolean gameOver() {
        return this.isGameOver;
    }

    boolean move(Pair<String, String> from, Pair<String, String> to) {
        System.out.println(from.first + from.second + " -> " + to.first + to.second);
        if (board.isValidMove(from, to)) {
            try {
                board.makeMove(turn, from, to);
                if (turn == 0) player2.costChessCount();
                else player1.costChessCount();
                turn = (turn + 1) % 2;
                return true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        turn = (turn + 1) % 2;
        return false;
    }

    private void checkGameOver() {
        if (player1.getChessCount() == 0 || player2.getChessCount() == 0)
            isGameOver = true;
    }
    
    public Chess[][] getBoard() {
        return board.getBoardArray(); // 你要在 ChessBoard 加這個方法
    }

    public String getCurrentPlayerName() {
        return (turn == 0) ? player1.name : player2.name;
    }
}

class ChessBoard {
    private Chess[][] _board = new Chess[4][8];
    private String[][] _refboard = new String[4][8];

    ChessBoard() {
        System.out.println("Creating a chessboard...");
        for (int i = 0; i < _refboard.length; i++) {
            Arrays.fill(_board[i], null);
        }
    }

    public Chess[][] getBoardArray() {
        return _board;
    }

    void printBoard() {
        System.out.print("  ");
        for (int i = 1; i <= 8; i++)
            System.out.print(String.format("%2d ", i));
        System.out.println();
        for (int i = 0; i < 4; i++) {
            System.out.print((char) (i + 65) + " ");
            for (int j = 0; j < 8; j++) {
                if (_board[i][j] == null)
                    System.out.print(String.format("%2s ", "_"));
                else if (_board[i][j].name == "X")
                    System.out.print(String.format("%2s ", _board[i][j].name));
                else
                    System.out.print(String.format("%s ", _board[i][j].name));
            }
            System.out.println();
        }
    }
    
    void printRefBoard() {
        System.out.println("Printing the chessboard...");
        System.out.print("  ");
        for (int i = 1; i <= 8; i++)
            System.out.print(String.format("%2d ", i));
        System.out.println();
        for (int i = 0; i < 4; i++) {
            System.out.print((char) (i + 65) + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(String.format("%s ", _refboard[i][j]));
            }
            System.out.println();
        }
    }

    void setUpInitialPieces() {
        System.out.println("Setting up the pieces...");
        String[] chessType = { "車", "馬", "象", "士", "將", "士", "象", "馬", "車", "包", "包", "卒", "卒", "卒", "卒", "卒" };
        String[] chessType2 = { "硨", "傌", "像", "仕", "帥", "仕", "像", "傌", "硨", "砲", "砲", "兵", "兵", "兵", "兵", "兵" };
        double[] chessWeight = { 4, 3, 5, 6, 7, 6, 5, 3, 4, 2, 2, 1, 1, 1, 1, 1 };
        int randNum1 = ThreadLocalRandom.current().nextInt(1, 5);
        int randNum2 = ThreadLocalRandom.current().nextInt(1, 9);
        for (int i = 0; i < 16; i++) {
            while (_board[randNum1 - 1][randNum2 - 1] != null) {
                randNum1 = ThreadLocalRandom.current().nextInt(1, 5);
                randNum2 = ThreadLocalRandom.current().nextInt(1, 9);
            }
            _refboard[randNum1 - 1][randNum2 - 1] = chessType[i];
            _board[randNum1 - 1][randNum2 - 1] = new Chess("X", chessWeight[i], 0, new Pair<String, String>(
                    String.valueOf((char) (randNum1 + 64)), String.valueOf(randNum2)));
        }
        for (int i = 0; i < 16; i++) {
            while (_board[randNum1 - 1][randNum2 - 1] != null) {
                randNum1 = ThreadLocalRandom.current().nextInt(1, 5);
                randNum2 = ThreadLocalRandom.current().nextInt(1, 9);
            }
            _refboard[randNum1 - 1][randNum2 - 1] = chessType2[i];
            _board[randNum1 - 1][randNum2 - 1] = new Chess("X", chessWeight[i], 1, new Pair<String, String>(
                    String.valueOf((char) (randNum1 + 64)), String.valueOf(randNum2)));
        }
    }

    boolean isValidMove(Pair<String, String> from, Pair<String, String> to) {
        if (from.x < 0 || from.x >= 4 || from.y < 0 || from.y >= 8) {
            return false;
        }
        if (to.x < 0 || to.x >= 4 || to.y < 0 || to.y >= 8) {
            return false;
        }
        return true;
    }

    void makeMove(int turn, Pair<String, String> from, Pair<String, String> to) throws InvalidChessMoveException {
        if (_board[from.x][from.y] == null)
            throw new InvalidChessMoveException("The location is null.");

        if (_board[from.x][from.y].name == "X") {
            _board[from.x][from.y].name = _refboard[from.x][from.y];
        }

        if (_board[from.x][from.y].side != turn)
            throw new InvalidChessMoveException("Invalid move: cannot move the other player's piece.");

        if (_board[from.x][from.y].weight == 2) {
            if (from.x == to.x || from.y == to.y) {
                if (_board[to.x][to.y] == null) {
                    if ((Math.abs(from.x - to.x) == 1 && from.y == to.y) || (from.x == to.x && Math.abs(from.y - to.y) == 1)) {
                        System.out.format("Making move at location: %s -> %s\n", from.first + from.second,
                                to.first + to.second);
                        _board[to.x][to.y] = _board[from.x][from.y];
                        _board[from.x][from.y] = null;
                        return;
                    }
                    else 
                        throw new InvalidChessMoveException("Invalid move: cannot move more than one step.");
                }
                int cnt = 0;
                if (from.x == to.x) {
                    for (int i = Math.min(from.y, to.y) + 1; i < Math.max(from.y, to.y); i++) {
                        if (_board[from.x][i] != null)
                            cnt++;
                    }
                } else {
                    for (int i = Math.min(from.x, to.x) + 1; i < Math.max(from.x, to.x); i++) {
                        if (_board[i][from.y] != null)
                            cnt++;
                    }
                }
                if (cnt != 1)
                    throw new InvalidChessMoveException("Invalid move: there is a piece in the way.");
                System.out.format("Making move at location: %s -> %s\n", from.first + from.second, to.first + to.second);
                _board[to.x][to.y] = _board[from.x][from.y];
                _board[from.x][from.y] = null;
                return;
            } else {
                throw new InvalidChessMoveException("Invalid move: cannot move diagonally.");   
            }
        }

        if(!((Math.abs(from.x - to.x) == 1 && from.y == to.y) || (from.x == to.x && Math.abs(from.y - to.y) == 1)))
            throw new InvalidChessMoveException("Invalid move: cannot move more than one step.");
        
        if (_board[to.x][to.y] == null) {
            System.out.format("Making move at location: %s -> %s\n", from.first + from.second, to.first + to.second);
            _board[to.x][to.y] = _board[from.x][from.y];
            _board[from.x][from.y] = null;
            return;
        }

        if (_board[to.x][to.y].name == "X") {
            _board[to.x][to.y].name = _refboard[to.x][to.y];
        }

        if (_board[from.x][from.y].side == _board[to.x][to.y].side)
            throw new InvalidChessMoveException("Invalid move: cannot eat your own piece.");

        else if (_board[from.x][from.y].weight == 1 && _board[to.x][to.y].weight == 7) {
            System.out.format("Making move at location: %s -> %s\n", from.first + from.second, to.first + to.second);
            _board[to.x][to.y] = _board[from.x][from.y];
            _board[from.x][from.y] = null;
        }

        else if (_board[from.x][from.y].weight == 7 && _board[to.x][to.y].weight == 1) {
            throw new InvalidChessMoveException("Invalid move: weight 7 cannot eat weight 1.");
        }

        else if (_board[from.x][from.y].weight < _board[to.x][to.y].weight)
            throw new InvalidChessMoveException("Invalid move: this piece cannot eat the other piece.");
        
        else {
            System.out.format("Making move at location: %s -> %s\n", from.first + from.second, to.first + to.second);
            _board[to.x][to.y] = _board[from.x][from.y];
            _board[from.x][from.y] = null;
            
        }
            
    }
}

class InvalidChessMoveException extends Exception {
    public InvalidChessMoveException(String message) {
        super(message);
    }
}