package src.ChessBoard;
import javax.swing.JPanel;

import src.Chess;
import src.Rule.Rule;

import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

interface ChessBoardInterface {
    void initVar();
    void initPoint();
    Image createChessBoard();
    void paint(Graphics g);
    void update();
}

abstract class ChessBoard extends JPanel {
    protected final Chess[] piece = new Chess[32];
    protected int[][] point; // 落棋點
    protected Rule rule;
    protected BoardMouseListener mouseListener = new BoardMouseListener();
    protected int mode; // 模式：0 為全局 1 為暗棋
    protected int chessState; // 對局狀態 0：未開始 1：對局中 2：結束
    protected boolean isRedTurn; // 回合判斷
    protected final String[] blackChessName = { "車", "馬", "象", "士", "將", "炮", "卒" };
    protected final String[] redChessName = { "俥", "傌", "相", "仕", "帥", "砲", "兵" };
    private Timer timer;
    protected int timeCnt;

    protected int CELL_W = 60;
    protected int OFFSET_X = 40, OFFSET_Y = 40;
    protected final int PIECE_R = Chess.size / 2;
    protected int TOTAL_W = OFFSET_X + CELL_W * 8 + 280;
    protected int TOTAL_H = OFFSET_Y + CELL_W * 9 + OFFSET_Y + 80;
    protected int Mouse_RANGE_X = OFFSET_X - PIECE_R;
    protected int Mouse_RANGE_Y = OFFSET_Y - PIECE_R;
    protected int Mouse_RANGE_W = OFFSET_X + CELL_W * 8 + 2 * PIECE_R;
    protected int Mouse_RANGE_H = OFFSET_Y + CELL_W * 9 + 2 * PIECE_R;
    protected Image boardImg; // 棋盤圖片

    protected void initVar() {}
    protected void initPoint() {}

    protected Image createChessBoard() {
        return null;
    }

    public void paint(Graphics g) {
    }

    protected void update() {
    }

    protected ChessBoard() {
        initPiece();
    }

    private int getWeight(String type) {
        int weight = 0;
        switch (type) {
            case "車", "俥":
                weight = 4;
                break;
            case "象", "相":
                weight = 5;
                break;
            case "馬", "傌":
                weight = 3;
                break;
            case "卒", "兵":
                weight = 1;
                break;
            case "士", "仕":
                weight = 6;
                break;
            case "將", "帥":
                weight = 7;
                break;
            case "砲", "炮":
                weight = 2;
                break;
        }
        return weight;
    }

    protected void initPiece() {
        for (int i = 0; i < 9; i++) {
            int nameIdx = (i < 5) ? i : (8 - i);
            piece[i] = new Chess(0, blackChessName[nameIdx], i, 0, getWeight(blackChessName[nameIdx]));
            piece[i + 16] = new Chess(1, redChessName[nameIdx], i, 9, getWeight(redChessName[nameIdx]));
        }
        piece[9] = new Chess(0, blackChessName[5], 1, 2, getWeight(blackChessName[5]));
        piece[10] = new Chess(0, blackChessName[5], 7, 2, getWeight(blackChessName[5]));
        piece[25] = new Chess(1, redChessName[5], 1, 7, getWeight(redChessName[5]));
        piece[26] = new Chess(1, redChessName[5], 7, 7, getWeight(redChessName[5]));
        for (int i = 0; i < 5; i++) {
            piece[11 + i] = new Chess(0, blackChessName[6], 2 * i, 3, getWeight(blackChessName[6]));
            piece[27 + i] = new Chess(1, redChessName[6], 2 * i, 6, getWeight(redChessName[6]));
        }
    }
    
    protected void resetTimer() {
        cancelTimer();
        timer = new Timer();
        // 每秒執行一次
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                timeCnt--;
                if (timeCnt == 0) {
                    isRedTurn = !isRedTurn;
                    chessState = 2;
                }
                update();
            }
        }, 1000, 1000);
        timeCnt = 10;
    }

    protected void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    class BoardMouseListener implements MouseListener, MouseMotionListener {

        private int pieceIdx = -1; // 選中的棋子的索引
        private int selx, sely; // 選中的棋子座標
        private int xpos, ypos; // 鼠標座標
        protected java.util.List<int[]> validMoves = new java.util.ArrayList<>();

        @Override
        public void mousePressed(MouseEvent me) {
            pieceIdx = -1;
            if (boardImg == null) {
                throw new IllegalStateException("Board image not initialized");
            }
            
            selx = posToIndex(me.getX(), Mouse_RANGE_X, Mouse_RANGE_X + Mouse_RANGE_W);
            sely = posToIndex(me.getY(), Mouse_RANGE_Y, Mouse_RANGE_Y + Mouse_RANGE_H);
            if (selx == -1 || sely == -1) {
                return;
            }
            int idx = point[selx][sely];
            if (idx < 0 || chessState != 1) {
                return;
            }

            if (!piece[idx].isRevealed()) {
                piece[idx].reveal();
                isRedTurn = !isRedTurn;
                resetTimer();
            } else {
                if (isRedTurn ? idx < 16 : idx >= 16) {
                    return;
                }
                pieceIdx = idx;

                validMoves.clear();
                validMoves = rule.getValidMoves(selx, sely);
            }
            update();
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (pieceIdx < 0) {
                return;
            }
            xpos = me.getX();
            ypos = me.getY();
            update();
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (pieceIdx < 0) {
                return;
            }

            if (boardImg == null) {
                throw new IllegalStateException("Board image not initialized");
            }

            pieceIdx = -1;
            int xd = posToIndex(me.getX(), Mouse_RANGE_X, Mouse_RANGE_X + Mouse_RANGE_W);
            int yd = posToIndex(me.getY(), Mouse_RANGE_Y, Mouse_RANGE_Y + Mouse_RANGE_H);
            if (rule.judge(selx, sely, xd, yd)) {
                int dstIdx = point[xd][yd];
                point[xd][yd] = point[selx][sely];
                point[selx][sely] = -1;
                isRedTurn = !isRedTurn;
                resetTimer();
                if (dstIdx >= 0) {
                    piece[dstIdx].die();
                }
            }
            validMoves.clear();
            update();
        }

        private int posToIndex(int pos, int ref, int range) {
            if (pos < ref || pos > ref + range) {
                return -1;
            }
            int m = (pos - ref) / CELL_W;
            int r = (pos - ref) % CELL_W;
            if (r < 2 * PIECE_R) {
                return m;
            }
            return -1;
        }

        public int getSelx() {
            return selx;
        }

        public int getSely() {
            return sely;
        }

        public int getIndex() {
            return pieceIdx;
        }

        public int getPieceXpos() {
            return xpos - PIECE_R;
        }

        public int getPieceYpos() {
            return ypos - PIECE_R;
        }

        @Override
        public void mouseClicked(MouseEvent me) {
        }

        @Override
        public void mouseEntered(MouseEvent me) {}

        @Override
        public void mouseExited(MouseEvent me) {}

        @Override
        public void mouseMoved(MouseEvent me) {}
    }
}