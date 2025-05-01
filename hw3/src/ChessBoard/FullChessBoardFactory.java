package src.ChessBoard;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import src.Rule.Rule;

public class FullChessBoardFactory implements ChessBoardFactory {
    @Override
    public ChessBoard createChessBoard() {
        return new FullChessBoard();
    }
}

class FullChessBoard extends ChessBoard {

    private Image imgbuf;
    private Graphics gbuf;
    private JButton startBtn;

    public FullChessBoard() {
        super.mode = 0;
        JFrame f = new JFrame("象棋");
        f.setBounds(400, 30, TOTAL_W, TOTAL_H);

        initVar();
        initButton();

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);

        setLayout(null);
        setBackground(new Color(206, 230, 214));
        f.getContentPane().add(this, BorderLayout.CENTER);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initButton() {
        startBtn = new JButton("開始遊戲");
        startBtn.setFont(new Font("", Font.BOLD, 18));
        startBtn.setBounds(TOTAL_W - 170, 220, 110, 50);
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(chessState);
                if (chessState != 1) {
                    chessState = 1;
                    isRedTurn = true;
                    resetTimer();
                    initPoint();
                    initPiece();
                    startBtn.setText("結束遊戲");
                } else {
                    chessState = 0;
                    cancelTimer();
                    initPoint();
                    startBtn.setText("開始遊戲");
                }
                update();
            }
        });
        add(startBtn);
    }

    protected void initVar() {
        point = new int[9][10];
        rule = new Rule(point);
        boardImg = createChessBoard();
        initPoint();
    }

    protected void initPoint() {

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                point[i][j] = -1;
            }
        }

        // 更新座標
        for (int i = 0; i < 32; i++) {
            point[piece[i].getXindex()][piece[i].getYindex()] = i;
        }
    }

    // 繪製棋盤
    protected Image createChessBoard() {
        BufferedImage bimg = new BufferedImage(CELL_W * 8, CELL_W * 9, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) bimg.getGraphics();

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));
        
        // 畫橫線
        for (int j = 1; j < 9; j++) {
            g2d.drawLine(0, CELL_W * j, CELL_W * 8, CELL_W * j);
        }
        // 畫豎線
        for (int i = 1; i < 8; i++) {
            g2d.drawLine(CELL_W * i, 0, CELL_W * i, CELL_W * 4);
            g2d.drawLine(CELL_W * i, CELL_W * 5, CELL_W * i, CELL_W * 9);
        }

        // 畫邊框
        g2d.drawRect(1, 1, CELL_W * 8 - 2, CELL_W * 9 - 2);
        
        // 畫九宮格
        g2d.drawLine(CELL_W * 3, 0, CELL_W * 5, CELL_W * 2);
        g2d.drawLine(CELL_W * 3, CELL_W * 2, CELL_W * 5, 0);
        g2d.drawLine(CELL_W * 3, CELL_W * 7, CELL_W * 5, CELL_W * 9);
        g2d.drawLine(CELL_W * 3, CELL_W * 9, CELL_W * 5, CELL_W * 7);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("隶书", Font.BOLD, (int) (45.0 * CELL_W / 60)));
        g2d.drawString("楚" + " 河", (int) (CELL_W * 4 - 180.0 * CELL_W / 60), (int) (CELL_W * 4.5 + 15.0 * CELL_W / 60));
        g2d.drawString("漢" + " 界", (int) (CELL_W * 4 + 60.0 * CELL_W / 60), (int) (CELL_W * 4.5 + 15.0 * CELL_W / 60));
        return bimg;
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        // 畫棋盤
        g2d.drawImage(boardImg, OFFSET_X, OFFSET_Y, null);
        
        // 棋盤外框
        g2d.setStroke(new BasicStroke(4.0f));
        g2d.drawRect(OFFSET_X - 5, OFFSET_Y - 5, boardImg.getWidth(this) + 10, boardImg.getHeight(this) + 10);

        int selIdx = mouseListener.getIndex();
        int idx, xpos, ypos;
        
        // 畫棋子
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                idx = point[i][j];
                if (idx >= 0 && idx != selIdx) {
                    xpos = OFFSET_X + CELL_W * i - PIECE_R;
                    ypos = OFFSET_Y + CELL_W * j - PIECE_R;
                    piece[idx].reveal(); // 顯示棋子
                    g2d.drawImage(piece[idx].getImage(), xpos, ypos, null);
                }
            }
        }
        paintTips(g2d); // 提示訊息

        // 繪製提示棋子落點
        g2d.setColor(new Color(0, 255, 0, 128)); // Semi-transparent green
        for (int[] move :mouseListener.validMoves) {
            int x = OFFSET_X + CELL_W * move[0];
            int y = OFFSET_Y + CELL_W * move[1];
            g2d.fillOval(x - PIECE_R / 2, y - PIECE_R / 2, PIECE_R, PIECE_R);
        }

        // 棋子拖動
        if (selIdx >= 0) {
            paintComponent(g2d,mouseListener.getSelx(),mouseListener.getSely());
            g2d.drawImage(piece[selIdx].getImage(),mouseListener.getPieceXpos(),mouseListener.getPieceYpos(), null);
        }
    }

    private void paintTips(Graphics2D g2d) {

        g2d.setFont(new Font("", Font.BOLD, 23));

        switch (chessState) {
            case 0:
                g2d.setColor(Color.BLACK);
                g2d.drawString("等待開局", TOTAL_W - 165, 100);
                break;
            case 1:
                if (isRedTurn) {
                    g2d.setColor(Color.red);
                    g2d.drawString("紅棋回合", TOTAL_W - 165, 100);
                } else {
                    g2d.setColor(Color.black);
                    g2d.drawString("黑棋回合", TOTAL_W - 165, 100);
                }
                g2d.drawString("倒計時：" + timeCnt + " s", TOTAL_W - 185, 140);
                break;
            case 2:
                if (isRedTurn) {
                    g2d.setColor(Color.RED);
                    g2d.drawString("遊戲結束", TOTAL_W - 165, 100);
                    g2d.drawString("紅方獲勝！", TOTAL_W - 180, 140);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("遊戲結束", TOTAL_W - 165, 100);
                    g2d.drawString("黑方獲勝！", TOTAL_W - 180, 140);
                }
                break;
            default:
                break;
        }
    }

    // 座標提示
    public void paintComponent(Graphics2D g2d, int i, int j) {
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setColor(Color.red);
        int x = OFFSET_X + CELL_W * i, y = OFFSET_Y + CELL_W * j;
        final int d1 = 15, d2 = 5;
        g2d.drawLine(x - d1, y - d2, x - d2, y - d2);
        g2d.drawLine(x - d1, y + d2, x - d2, y + d2);
        g2d.drawLine(x + d1, y - d2, x + d2, y - d2);
        g2d.drawLine(x + d1, y + d2, x + d2, y + d2);
        g2d.drawLine(x - d2, y - d2, x - d2, y - d1);
        g2d.drawLine(x + d2, y - d2, x + d2, y - d1);
        g2d.drawLine(x - d2, y + d2, x - d2, y + d1);
        g2d.drawLine(x + d2, y + d2, x + d2, y + d1);
    }

    public void update() {
        if (piece[4].isEaten() || piece[20].isEaten()) {
            chessState = 2;
        }
        if (imgbuf == null) {
            imgbuf = createImage(this.getSize().width, this.getSize().height);
            gbuf = imgbuf.getGraphics();
        }
        gbuf.setColor(getBackground());
        gbuf.fillRect(0, 0, this.getSize().width, this.getSize().height);
        if (chessState == 2) {
            isRedTurn = !isRedTurn;
            this.cancelTimer();
            startBtn.setText("重新開始");
            if (isRedTurn) {
                JOptionPane.showMessageDialog(null, "紅方獲勝！", "遊戲結束", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "黑方獲勝！", "遊戲結束", JOptionPane.PLAIN_MESSAGE);
            }
        }
        paint(gbuf);
        this.getGraphics().drawImage(imgbuf, 0, 0, this);
    }
}