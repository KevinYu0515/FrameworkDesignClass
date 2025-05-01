package src;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Chess {

    public static final int size = 50;
    private final int camp; // 0-红方，1-黑方
    private final String name;
    private final int xindex, yindex;
    private Image img;
    private boolean isRevealed;

    public Chess(int camp, String name, int xindex, int yindex) {
        this.camp = camp;
        this.name = name;
        this.xindex = xindex;
        this.yindex = yindex;
        this.img = createImage();
        this.isRevealed = false;
    }

    private Image createImage() {
        BufferedImage bimg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) bimg.getGraphics();

        Color fillColor, borderColor, fontColor;
        if (camp == 0) {
            fillColor = new Color(255, 239, 188);
            borderColor = new Color(215, 173, 15);
            fontColor = Color.BLACK;
        } else {
            fillColor = new Color(255, 230, 223);
            borderColor = new Color(231, 140, 59);
            fontColor = Color.RED;
        }
        g2d.setColor(fillColor);
        g2d.fillOval(0, 0, size, size);

        g2d.setStroke(new BasicStroke(3.0f));
        g2d.setColor(borderColor);
        g2d.drawOval(1, 1, size - 2, size - 2);

        if (isRevealed) {
            g2d.setColor(fontColor);
            g2d.setFont(new Font("標楷體", Font.BOLD, (int) (38.0 * size / 50)));
            g2d.drawString(name, (int) (size / 2 - 20.0 * size / 50), (int) (size / 2 + 14.0 * size / 50));
        } else {
            g2d.setColor(Color.GRAY);
            g2d.fillOval(1, 1, size - 2, size - 2);
        }
        return bimg;
    }

    public int getXindex() {
        return xindex;
    }

    public int getYindex() {
        return yindex;
    }

    public Image getImage() {
        return img;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void reveal() {
        this.isRevealed = true;
        this.img = createImage();
    }

    public String getName() {
        return name;
    }

}