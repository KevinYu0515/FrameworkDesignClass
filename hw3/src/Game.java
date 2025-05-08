package src;
import javax.swing.*;

import src.ChessBoard.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game {
    public void start(ChessBoardFactory factory, String mode) {
        if ("大局模式".equals(mode)) {
            System.out.println("啟動大局模式...");
        } else if ("暗棋模式".equals(mode)) {
            System.out.println("啟動暗棋模式...");
        } else {
            System.out.println("未知的遊戲模式: " + mode);
        }
        factory.createChessBoard();
    }

    public void showModeSelection() {
        JFrame frame = new JFrame("選擇遊戲模式");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("請選擇遊戲模式:");
        frame.add(label);

        JButton btnDaJu = new JButton("大局模式");
        JButton btnAnQi = new JButton("暗棋模式");

        btnDaJu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // 關閉選擇視窗
                start(new FullChessBoardFactory(), "大局模式");
            }
        });

        btnAnQi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // 關閉選擇視窗
                start(new HalfChessBoardFactory(),"暗棋模式");
            }
        });

        frame.add(btnDaJu);
        frame.add(btnAnQi);

        frame.setVisible(true);
    }
}