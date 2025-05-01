package src.Rule;
import java.util.ArrayList;
import java.util.List;

public class DarkRule extends Rule {

    public DarkRule(int[][] point) {
        super(point);
    }
    
    @Override
    public boolean judge(int xs, int ys, int xd, int yd) {
        if (xd < 0 || yd < 0) {
            return false;
        }

        System.out.println("xs: " + xs + ", ys: " + ys + ", xd:" + xd + ", yd:" + yd);
        int idx = this.point[xs][ys];
        this.xs = xs;
        this.ys = ys;
        this.xd = xd;
        this.yd = yd;

        if (isSameFamily()) {
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
        if (idx >= 11 && idx < 16) {
            return judgeZu();
        }
        if (idx >= 27) {
            return judgeBing();
        }
        return false;
    }

    @Override
    public List<int[]> getValidMoves(int xs, int ys) {
        List<int[]> validMoves = new ArrayList<>();
        for (int xd = 0; xd < 8; xd++) {
            for (int yd = 0; yd < 4; yd++) {
                if (judge(xs, ys, xd, yd)) {
                    validMoves.add(new int[] { xd, yd });
                    System.out.println("Valid｜xd: " + xd + ", yd: " + yd);
                }
            }
        }
        return validMoves;
    }
}
