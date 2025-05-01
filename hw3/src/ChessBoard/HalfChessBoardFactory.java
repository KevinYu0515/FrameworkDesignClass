package src.ChessBoard;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;
import javax.swing.*;

import src.Rule.DarkRule;

import java.awt.image.BufferedImage;

public class HalfChessBoardFactory implements ChessBoardFactory {
    @Override
    public ChessBoard createChessBoard() {
        return new HalfChessBoard();
    }
}

class HalfChessBoard extends ChessBoard {
    private Image imgbuf;
    private Graphics gbuf;
    private JButton startBtn;

    public HalfChessBoard() {
        super.mode = 1;
        super.CELL_W = 80;
        super.OFFSET_X = 40;
        super.OFFSET_Y = 240;
        super.Mouse_RANGE_X = OFFSET_X + PIECE_R;
        super.Mouse_RANGE_Y = OFFSET_Y + PIECE_R;
        super.Mouse_RANGE_W = CELL_W * 8;
        super.Mouse_RANGE_H = CELL_W * 4;
        super.TOTAL_W = OFFSET_X + CELL_W * 8 + OFFSET_X;

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

    protected void initVar() {
        super.point = new int[8][4];
        super.rule = new DarkRule(point, piece);
        super.boardImg = createChessBoard();
        initPoint();
    }
    
    private void initButton() {
        startBtn = new JButton("開始遊戲");
        startBtn.setFont(new Font("", Font.BOLD, 18));
        startBtn.setBounds(OFFSET_X,  30, 110, 50);
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

    protected void initPoint() {
        // 隨機翻面棋子
        Random random = new Random();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, random);
        for (int i = 0; i < 32; i++) {
            point[indices.get(i) % 8][indices.get(i) / 8] = i;
        }
    }

    protected Image createChessBoard() {
        BufferedImage bimg = new BufferedImage(CELL_W * 8, CELL_W * 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) bimg.getGraphics();

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));

        // 畫表格
        for (int row = 0; row <= 4; row++) {
            g2d.drawLine(0, row * CELL_W, 9 * CELL_W, row * CELL_W);
        }
        for (int col = 0; col <= 9; col++) {
            g2d.drawLine(col * CELL_W, 0, col * CELL_W, 9 * CELL_W);
        }

        return bimg;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // 畫棋盤
        g2d.drawImage(boardImg, OFFSET_X, OFFSET_Y, null);

        // 棋盤外框
        g2d.setStroke(new BasicStroke(4.0f));
        g2d.drawRect(OFFSET_X - 5, OFFSET_Y - 5, boardImg.getWidth(this) + 10, boardImg.getHeight(this) + 10);

        int selIdx = mouseListener.getIndex();
        int idx, xpos, ypos;

        // 畫棋子
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                idx = point[i][j];
                if (idx >= 0 && idx != selIdx) {
                    xpos = OFFSET_X + CELL_W / 2 + CELL_W * i - PIECE_R;
                    ypos = OFFSET_Y + CELL_W / 2 + CELL_W * j - PIECE_R;
                    g2d.drawImage(piece[idx].getImage(), xpos, ypos, null);
                }
            }
        }
        paintTips(g2d); // 提示訊息

        // 繪製提示棋子落點
        g2d.setColor(new Color(0, 255, 0, 128)); // Semi-transparent green
        for (int[] move : mouseListener.validMoves) {
            int x = OFFSET_X + CELL_W / 2 + CELL_W * move[0];
            int y = OFFSET_Y + CELL_W / 2 + CELL_W * move[1];
            g2d.fillOval(x - PIECE_R / 2, y - PIECE_R / 2, PIECE_R, PIECE_R);
        }

        // 棋子拖動
        if (selIdx >= 0) {
            paintComponent(g2d, mouseListener.getSelx(), mouseListener.getSely());
            g2d.drawImage(piece[selIdx].getImage(), mouseListener.getPieceXpos(), mouseListener.getPieceYpos(), null);
        }
    }

    private void paintTips(Graphics2D g2d) {

        g2d.setFont(new Font("", Font.BOLD, 23));

        switch (chessState) {
            case 0:
                g2d.setColor(Color.BLACK);
                g2d.drawString("等待開局", OFFSET_X, 150);
                break;
            case 1:
                if (isRedTurn) {
                    g2d.setColor(Color.red);
                    g2d.drawString("紅棋回合", OFFSET_X, 150);
                } else {
                    g2d.setColor(Color.black);
                    g2d.drawString("黑棋回合", OFFSET_X, 150);
                }
                g2d.drawString("倒計時：" + timeCnt + " s", OFFSET_X, 200);
                break;
            case 2:
                if (isRedTurn) {
                    g2d.setColor(Color.RED);
                    g2d.drawString("遊戲結束", OFFSET_X, 150);
                    g2d.drawString("紅方獲勝！", OFFSET_X, 200);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("遊戲結束", OFFSET_X, 150);
                    g2d.drawString("黑方獲勝！", OFFSET_X, 200);
                }
                break;
            default:
                break;
        }
    }

    // 棋子原本的座標提示
    public void paintComponent(Graphics2D g2d, int i, int j) {
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setColor(Color.red);
        int x = OFFSET_X + CELL_W / 2 + CELL_W * i;
        int y = OFFSET_Y + CELL_W / 2 + CELL_W * j;
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

    void checkEnd(int st) {
        for (int i = st; i < st + 16; i++) {
            if (!piece[i].isEaten()) {
                chessState = 1;
                return;
            }
            chessState = 2;
        }
    }

    @Override
    public void update() {
        if (timeCnt > 0) {
            checkEnd(0);
            checkEnd(16);
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
