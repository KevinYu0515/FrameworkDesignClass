package src.Rule;
import java.util.ArrayList;
import java.util.List;
import src.Chess;

public class DarkRule extends Rule {
    private Chess[] piece;

    public DarkRule(int[][] point, Chess[] piece) {
        super(point);
        this.piece = piece;
    }
    
    @Override
    public boolean judge(int xs, int ys, int xd, int yd) {
        if (xd < 0 || yd < 0) {
            return false;
        }

        int idx = this.point[xs][ys];
        this.xs = xs;
        this.ys = ys;
        this.xd = xd;
        this.yd = yd;
        
        if (isSameFamily()) {
            return false;
        }

        if ("炮".equals(piece[idx].getName()) || "砲".equals(piece[idx].getName())) {
            if (judgePao())
                return true;
        }

        if (Math.abs(xs - xd) > 1 || Math.abs(ys - yd) > 1 || (Math.abs(ys - yd) == 1 && Math.abs(xs - xd) == 1)) {
            return false;
        }

        if (this.point[xd][yd] == -1) {
            return true;
        }

        if (!piece[this.point[xd][yd]].isRevealed()) {
            return false;
        }

        if (idx == 0 || idx == 8 || idx == 16 || idx == 24) {
            return judgeJu();
        }
        if (idx == 1 || idx == 7 || idx == 17 || idx == 23) {
            return judgeMa();
        }
        if (idx == 2 || idx == 6 || idx == 18 || idx == 22) {
            return judgeXiang();
        }
        if (idx == 3 || idx == 5 || idx == 19 || idx == 21) {
            return judgeShi();
        }
        if (idx == 4 || idx == 20) {
            return judgeWang();
        }
        if (idx == 9 || idx == 10 || idx == 25 || idx == 26) {
            return judgePao();
        }
        return checkWeight(xs, ys, xd, yd);
    }

    @Override
    public List<int[]> getValidMoves(int xs, int ys) {
        List<int[]> validMoves = new ArrayList<>();
        for (int xd = 0; xd < 8; xd++) {
            for (int yd = 0; yd < 4; yd++) {
                if (judge(xs, ys, xd, yd)) {
                    validMoves.add(new int[] { xd, yd });
                }
            }
        }
        return validMoves;
    }

    protected boolean checkWeight(int xs, int ys, int xd, int yd) {
        if ("炮".equals(piece[point[xs][ys]].getName())  || "砲".equals(piece[point[xs][ys]].getName()))
            return true;
        if (("卒".equals(piece[point[xs][ys]].getName()) || "兵".equals(piece[point[xs][ys]].getName())) &&
                ("帥".equals(piece[point[xd][yd]].getName()) || "將".equals(piece[point[xd][yd]].getName()))) {
            return true;
        }
        if (("帥".equals(piece[point[xs][ys]].getName())  || "將".equals(piece[point[xs][ys]].getName())) &&
                ("兵".equals(piece[point[xd][yd]].getName()) || "卒".equals(piece[point[xd][yd]].getName()))) {
            return false;
        }
        return piece[point[xs][ys]].weight >= piece[point[xd][yd]].weight;
    }

    protected boolean judgePao() {
        return xs == xd && checkY(xs, 1) && hasPiece(xd, yd) && piece[point[xd][yd]].isRevealed()
                || ys == yd && checkX(ys, 1) && hasPiece(xd, yd) && piece[point[xd][yd]].isRevealed();
    }
    
    protected boolean judgeJu() {
        return checkWeight(xs, ys, xd, yd);
    }

    protected boolean judgeMa() {
        return checkWeight(xs, ys, xd, yd);
    }

    protected boolean judgeXiang() {
        return checkWeight(xs, ys, xd, yd);
    }

    protected boolean judgeShi() {
        return checkWeight(xs, ys, xd, yd);
    }

    protected boolean judgeZu() {
        return checkWeight(xs, ys, xd, yd);
    }

    protected boolean judgeBing() {
        return checkWeight(xs, ys, xd, yd);
    }

    protected boolean judgeWang() {
        return checkWeight(xs, ys, xd, yd);
    }
}
