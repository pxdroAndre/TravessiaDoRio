/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.tc.travessia;

/**
 *
 * @author John
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Travessia extends JFrame {
    private String fazendeiro = "e";
    private String lobo = "e";
    private String ovelha = "e";
    private String couve = "e";

    private PainelEstados painelEstados;

    public Travessia() {
        setTitle("Travessia do Rio - Máquina de Estados");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        painelEstados = new PainelEstados();
        add(painelEstados, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new GridLayout(2, 4, 5, 5));
        String[] comandos = {"OD", "OE", "LD", "LE", "CD", "CE", "FD", "FE"};
        for (String cmd : comandos) {
            JButton btn = new JButton(cmd);
            btn.addActionListener(new MovimentoListener(cmd));
            botoes.add(btn);
        }
        add(botoes, BorderLayout.SOUTH);

        painelEstados.setEstadoAtual(identificarEstado());
    }

    private String identificarEstado() {
        String estadoAtual = String.format("%s,%s,%s,%s", fazendeiro, lobo, ovelha, couve);
        return switch (estadoAtual) {
            case "e,e,e,e" -> "F1";
            case "d,e,d,e" -> "F2";
            case "e,e,d,e" -> "F3";
            case "d,e,d,d" -> "F4";
            case "e,e,e,d" -> "F5";
            case "d,d,e,d" -> "F6";
            case "e,d,e,d" -> "F7";
            case "d,d,d,d" -> "F8";
            case "d,d,d,e" -> "F9";
            case "e,d,e,e" -> "F10";
            default -> "INV";
        };
    }

    private void verificarPerigo() {
        if (fazendeiro.equals("e")) {
            if (lobo.equals("d") && ovelha.equals("d")) fimDeJogo("💀 O lobo comeu a ovelha na direita!");
            if (ovelha.equals("d") && couve.equals("d")) fimDeJogo("💀 A ovelha comeu a couve na direita!");
        } else {
            if (lobo.equals("e") && ovelha.equals("e")) fimDeJogo("💀 O lobo comeu a ovelha na esquerda!");
            if (ovelha.equals("e") && couve.equals("e")) fimDeJogo("💀 A ovelha comeu a couve na esquerda!");
        }
    }

    private void fimDeJogo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Fim de jogo", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    private void aplicarMovimento(String mov) {
        switch (mov) {
            case "OD" -> { if (fazendeiro.equals("e") && ovelha.equals("e")) { ovelha = "d"; fazendeiro = "d"; } }
            case "OE" -> { if (fazendeiro.equals("d") && ovelha.equals("d")) { ovelha = "e"; fazendeiro = "e"; } }
            case "LD" -> { if (fazendeiro.equals("e") && lobo.equals("e")) { lobo = "d"; fazendeiro = "d"; } }
            case "LE" -> { if (fazendeiro.equals("d") && lobo.equals("d")) { lobo = "e"; fazendeiro = "e"; } }
            case "CD" -> { if (fazendeiro.equals("e") && couve.equals("e")) { couve = "d"; fazendeiro = "d"; } }
            case "CE" -> { if (fazendeiro.equals("d") && couve.equals("d")) { couve = "e"; fazendeiro = "e"; } }
            case "FD" -> { if (fazendeiro.equals("e")) { fazendeiro = "d"; } }
            case "FE" -> { if (fazendeiro.equals("d")) { fazendeiro = "e"; } }
        }
        verificarPerigo();
        painelEstados.setEstadoAtual(identificarEstado());
        painelEstados.repaint();

        if (identificarEstado().equals("F8")) {
            JOptionPane.showMessageDialog(this, "🎉 Parabéns! Todos atravessaram!", "Vitória", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    private class MovimentoListener implements ActionListener {
        private final String movimento;
        public MovimentoListener(String movimento) {
            this.movimento = movimento;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            aplicarMovimento(movimento);
        }
    }

    // Painel que desenha a máquina de estados
    private static class PainelEstados extends JPanel {
        private String estadoAtual = "F1";

        public void setEstadoAtual(String estado) {
            this.estadoAtual = estado;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g2.setFont(new Font("Arial", Font.BOLD, 14));

            // Coordenadas dos estados
            Point[] pos = {
                new Point(50, 100),   // F1
                new Point(150, 100),  // F2
                new Point(250, 100),  // F3
                new Point(250, 200),  // F4
                new Point(150, 200),   // F5
                new Point(150, 300),   // F6
                new Point(250, 300),   // F7
                new Point(350, 300),  // F8
                new Point(350, 100),  // F9
                new Point(350, 200)   // F10
            };

            // Ligações (setas)
            drawArrow(g2, pos[0], pos[1]); // F1 -> F2
            drawArrow(g2, pos[1], pos[2]); // F2 -> F3
            drawArrow(g2, pos[2], pos[8]); // F3 -> F9
            drawArrow(g2, pos[8], pos[9]); // F9 -> F10
            drawArrow(g2, pos[9], pos[5]); // F10 -> F6
            drawArrow(g2, pos[5], pos[6]); // F6 -> F7
            drawArrow(g2, pos[6], pos[7]); // F7 -> F8
            drawArrow(g2, pos[2], pos[3]); // F3 -> F4
            drawArrow(g2, pos[3], pos[4]); // F4 -> F5
            drawArrow(g2, pos[4], pos[5]); // F5 -> F6

            // Desenha os estados
            for (int i = 0; i < pos.length; i++) {
                String nome = "F" + (i + 1);
                if (nome.equals(estadoAtual)) {
                    g2.setColor(Color.GREEN);
                    g2.fillOval(pos[i].x - 20, pos[i].y - 20, 40, 40);
                    g2.setColor(Color.BLACK);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(pos[i].x - 20, pos[i].y - 20, 40, 40);
                    g2.setColor(Color.BLACK);
                }
                g2.drawOval(pos[i].x - 20, pos[i].y - 20, 40, 40);
                g2.drawString(nome, pos[i].x - 10, pos[i].y + 5);
            }
        }

        private void drawArrow(Graphics2D g2, Point from, Point to) {
            int ARR_SIZE = 6;
            g2.setColor(Color.BLACK);
            g2.drawLine(from.x, from.y, to.x, to.y);

            double dx = to.x - from.x;
            double dy = to.y - from.y;
            double angle = Math.atan2(dy, dx);
            int x1 = (int) (to.x - ARR_SIZE * Math.cos(angle - Math.PI / 6));
            int y1 = (int) (to.y - ARR_SIZE * Math.sin(angle - Math.PI / 6));
            int x2 = (int) (to.x - ARR_SIZE * Math.cos(angle + Math.PI / 6));
            int y2 = (int) (to.y - ARR_SIZE * Math.sin(angle + Math.PI / 6));
            g2.drawLine(to.x, to.y, x1, y1);
            g2.drawLine(to.x, to.y, x2, y2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Travessia().setVisible(true));
    }
}