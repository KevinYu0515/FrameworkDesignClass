import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class chessGameGUI extends JFrame {
    private JLabel currentPlayerLabel;
    private Map<String, ImageIcon> pieceImages = new HashMap<>();
    private ChessGame game;
    private JPanel boardPanel;
    private JButton[][] buttons;
    private Pair<String, String> selectedFrom = null;

    public chessGameGUI() {

        setTitle("象棋遊戲");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Player player1 = new Player("Player 1", 0);
        Player player2 = new Player("Player 2", 1);
        game = new ChessGame(player1, player2);

        boardPanel = new JPanel(new GridLayout(4, 8));
        buttons = new JButton[4][8];

        initializeBoardUI();

        add(boardPanel, BorderLayout.CENTER);
        setVisible(true);
        loadPieceImages();

        currentPlayerLabel = new JLabel();
        currentPlayerLabel.setFont(new Font("Serif", Font.BOLD, 18));
        currentPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(currentPlayerLabel, BorderLayout.NORTH);
        updateCurrentPlayer();

    }
    
    private void loadPieceImages() {
        String[] names = { "car", "horse", "elephant", "guard", "king", "cannon", "pawn" };
        for (String name : names) {
            pieceImages.put(name + "_black", new ImageIcon("resources/images/" + name + "_black.png"));
            pieceImages.put(name + "_red", new ImageIcon("resources/images/" + name + "_red.png"));
        }
    }
    
    private void updateCurrentPlayer() {
        String name = game.getCurrentPlayerName();
        currentPlayerLabel.setText("目前輪到: " + name);
    }

    private void initializeBoardUI() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                JButton button = new JButton();
                button.setFont(new Font("Serif", Font.PLAIN, 18));
                int row = i;
                int col = j;

                button.addActionListener(e -> handleClick(row, col));

                buttons[i][j] = button;
                boardPanel.add(button);
            }
        }

        updateBoard();
    }

    private ImageIcon getScaledIcon(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private String mapChineseNameToKey(String name) {
        switch (name) {
            case "車":
            case "硨":
                return "car";
            case "馬":
            case "傌":
                return "horse";
            case "象":
            case "像":
                return "elephant";
            case "士":
            case "仕":
                return "guard";
            case "將":
            case "帥":
                return "king";
            case "包":
            case "砲":
                return "cannon";
            case "卒":
            case "兵":
                return "pawn";
            default:
                return "pawn"; // fallback
        }
    }

    private void handleClick(int row, int col) {
        String rowChar = String.valueOf((char) (row + 'A'));
        String colStr = String.valueOf(col + 1);
        Pair<String, String> clicked = new Pair<>(rowChar, colStr);
        Chess[][] boardArray = game.getBoard();
        String[][] refBoard = game.getRefBoard();

        // 翻開棋子就換人
        if (boardArray[row][col] != null && boardArray[row][col].name.equals("X")) {
            String actualName = refBoard[row][col];
            boardArray[row][col].name = actualName;
            updateBoard();

            int pieceSide = boardArray[row][col].side;
            int currentTurn = game.getTurn();
            if (pieceSide != currentTurn) {
                game.nextTurn();
                updateCurrentPlayer();
                updateBoard();
                return;
            }
        }


        // 沒有選中的格子，就記下來
        if (selectedFrom == null) {
            selectedFrom = clicked;
            updateBoard();
        } else {
            // 如果是同一格，再次點擊就取消
            System.out.println(selectedFrom);
            if (selectedFrom.first.equals(clicked.first) && selectedFrom.second.equals(clicked.second)) {
                selectedFrom = null;
                updateBoard();
                return;
            }

            boolean moved = game.move(selectedFrom, clicked);
            selectedFrom = null;
            if (moved) {
                if (game.gameOver()) {
                    String winner = game.getWinnerName();
                    JOptionPane.showMessageDialog(this, "Game Over! " + winner + " 獲勝！");
                }
            }
            game.nextTurn();
            updateCurrentPlayer();
            updateBoard();
        }
    }

    private void updateBoard() {
        Chess[][] board = game.getBoard();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                JButton button = buttons[i][j];
                button.setBackground(null);

                if (board[i][j] != null) {
                    if (board[i][j].name.equals("X")) {
                        button.setIcon(null);
                        button.setText("X");
                    } else {
                        String pieceName = board[i][j].name;
                        int side = board[i][j].side;
                        String key = mapChineseNameToKey(pieceName) + (side == 0 ? "_black" : "_red");

                        ImageIcon icon = pieceImages.get(key);
                        if (icon != null) {
                            icon = getScaledIcon(icon, 50, 50);
                            button.setIcon(icon);
                            button.setText("");
                        }
                    }
                } else {
                    button.setIcon(null);
                    button.setText("");
                }

                // 高亮選中的格子
                String rowChar = String.valueOf((char) (i + 'A'));
                String colStr = String.valueOf(j + 1);
                if (selectedFrom != null && selectedFrom.first.equals(rowChar) && selectedFrom.second.equals(colStr)) {
                    button.setBackground(Color.YELLOW);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(chessGameGUI::new);
    }
}
