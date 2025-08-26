package com.tc.travessia;

/**
 *
 * @author John
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class Travessia extends JFrame {
    // Estado lógico (e/d)
    private String fazendeiro = "e";
    private String lobo = "e";
    private String ovelha = "e";
    private String couve = "e";

    // UI
    private final PainelEstados painelEstados;
    private final PainelAnimacao painelAnimacao;

    // Chat (log + input fixo embaixo)
    private final JTextArea chatArea;
    private JTextField campoComando;
    private final JButton btnEnviar;

    public Travessia() {
        setTitle("Travessia do Rio - Máquina de Estados");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); // IMPORTANTE: permite SOUTH fixo

        // Esquerda (estados) + Direita (animação) dentro de um JSplitPane no CENTER
        painelEstados = new PainelEstados();
        painelAnimacao = new PainelAnimacao();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelEstados, painelAnimacao);
        split.setResizeWeight(0.45);         // ~45% esquerda, 55% direita
        split.setDividerLocation(0.45);
        add(split, BorderLayout.CENTER);

        // CHAT (SOUTH) – log + input
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(0, 180));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatPanel.add(chatScroll, BorderLayout.CENTER);

        JPanel inputBar = new JPanel(new BorderLayout(8, 0));
        campoComando = new JTextField();
        btnEnviar = new JButton("Enviar");
        inputBar.add(campoComando, BorderLayout.CENTER);
        inputBar.add(btnEnviar, BorderLayout.EAST);
        chatPanel.add(inputBar, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.SOUTH);

        // Ações de envio
        Runnable enviarAcao = () -> {
            String cmd = campoComando.getText().trim().toUpperCase();
            if (!cmd.isEmpty()) {
                log("Você: " + cmd);
                aplicarMovimentoSequencial(cmd);
                campoComando.setText("");
            }
        };
        btnEnviar.addActionListener(e -> enviarAcao.run());
        campoComando.addActionListener(e -> enviarAcao.run());

        // Inicializa UI com estado atual
        painelEstados.setEstadoAtual(identificarEstado());
        painelAnimacao.syncComEstado(fazendeiro, lobo, ovelha, couve);
    }

    /* ===================== LÓGICA DE ESTADO ===================== */

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

    /* ===================== EXECUÇÃO SEQUENCIAL + ANIMAÇÃO ===================== */

    private void aplicarMovimentoSequencial(String cadeia) {
        if (cadeia.isEmpty()) return;
        char[] passos = cadeia.toCharArray();
        processarPasso(passos, 0);
    }

    private void processarPasso(char[] passos, int idx) {
        if (idx >= passos.length) return;

        char c = passos[idx];
        switch (c) {
            case 'O' -> moverComFazendeiro("ovelha", () -> {
                // Toggle lado
                if (fazendeiro.equals("e") && ovelha.equals("e")) {
                    ovelha = "d"; fazendeiro = "d";
                } else if (fazendeiro.equals("d") && ovelha.equals("d")) {
                    ovelha = "e"; fazendeiro = "e";
                } else {
                    log("Movimento inválido: O (fazendeiro e ovelha precisam estar juntos)");
                }
                aposPasso(passos, idx);
            });
            case 'L' -> moverComFazendeiro("lobo", () -> {
                if (fazendeiro.equals("e") && lobo.equals("e")) {
                    lobo = "d"; fazendeiro = "d";
                } else if (fazendeiro.equals("d") && lobo.equals("d")) {
                    lobo = "e"; fazendeiro = "e";
                } else {
                    log("Movimento inválido: L (fazendeiro e lobo precisam estar juntos)");
                }
                aposPasso(passos, idx);
            });
            case 'C' -> moverComFazendeiro("couve", () -> {
                if (fazendeiro.equals("e") && couve.equals("e")) {
                    couve = "d"; fazendeiro = "d";
                } else if (fazendeiro.equals("d") && couve.equals("d")) {
                    couve = "e"; fazendeiro = "e";
                } else {
                    log("Movimento inválido: C (fazendeiro e couve precisam estar juntos)");
                }
                aposPasso(passos, idx);
            });
            case 'F' -> moverSoFazendeiro(() -> {
                // Toggle lado do fazendeiro sozinho
                fazendeiro = fazendeiro.equals("e") ? "d" : "e";
                aposPasso(passos, idx);
            });
            default -> {
                log("Comando inválido: " + c);
                aposPasso(passos, idx);
            }
        }
    }

    private void aposPasso(char[] passos, int idx) {
        verificarPerigo();

        // Atualiza UI
        painelEstados.setEstadoAtual(identificarEstado());
        painelEstados.repaint();
        painelAnimacao.syncComEstado(fazendeiro, lobo, ovelha, couve);

        log("Estado atual: " + identificarEstado());

        // Vitória?
        if (Objects.equals(identificarEstado(), "F8")) {
            JOptionPane.showMessageDialog(this, "🎉 Parabéns! Todos atravessaram!", "Vitória", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        // Espera 1s antes do próximo passo (sem travar a UI)
        Timer delay = new Timer(1000, (ActionEvent e) -> {
            ((Timer) e.getSource()).stop();
            processarPasso(passos, idx + 1);
        });
        delay.setRepeats(false);
        delay.start();
    }

    /* ===================== ANIMAÇÕES ===================== */

    // Move fazendeiro + item juntos
    private void moverComFazendeiro(String quem, Runnable onFinish) {
        boolean paraDireita = fazendeiro.equals("e"); // se fazendeiro está na esquerda, vão para a direita; senão, voltam
        painelAnimacao.animarTravessia(quem, paraDireita, onFinish);
    }

    // Move só o fazendeiro
    private void moverSoFazendeiro(Runnable onFinish) {
        boolean paraDireita = fazendeiro.equals("e");
        painelAnimacao.animarTravessia("fazendeiro", paraDireita, onFinish);
    }

    /* ===================== UTIL ===================== */

    private void log(String msg) {
        chatArea.append(msg + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    /* ===================== PAINEL: MÁQUINA DE ESTADOS ===================== */

    private static class PainelEstados extends JPanel {
        private String estadoAtual = "F1";

        public void setEstadoAtual(String estado) { this.estadoAtual = estado; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2));
            g2.setFont(new Font("Arial", Font.BOLD, 14));

            // Coordenadas dos estados (ajuste conforme seu diagrama)
            Point[] pos = {
                    new Point(60, 100),   // F1
                    new Point(160, 100),  // F2
                    new Point(260, 100),  // F3
                    new Point(260, 200),  // F4
                    new Point(160, 200),  // F5
                    new Point(160, 300),  // F6
                    new Point(260, 300),  // F7
                    new Point(360, 300),  // F8
                    new Point(360, 100),  // F9
                    new Point(360, 200)   // F10
            };

            // Ligações (setas) — sem textos
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

            // Desenha estados (bolinhas)
            for (int i = 0; i < pos.length; i++) {
                String nome = "F" + (i + 1);
                if (nome.equals(estadoAtual)) {
                    g2.setColor(new Color(56, 176, 0));
                    g2.fillOval(pos[i].x - 20, pos[i].y - 20, 40, 40);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(pos[i].x - 20, pos[i].y - 20, 40, 40);
                }
                g2.setColor(Color.BLACK);
                g2.drawOval(pos[i].x - 20, pos[i].y - 20, 40, 40);
                g2.drawString(nome, pos[i].x - 12, pos[i].y + 5);
            }

            g2.dispose();
        }

        private void drawArrow(Graphics2D g2, Point from, Point to) {
            g2.setColor(Color.BLACK);
            g2.drawLine(from.x, from.y, to.x, to.y);
            int ARR = 7;
            double dx = to.x - from.x, dy = to.y - from.y, ang = Math.atan2(dy, dx);
            int x1 = (int) (to.x - ARR * Math.cos(ang - Math.PI / 6));
            int y1 = (int) (to.y - ARR * Math.sin(ang - Math.PI / 6));
            int x2 = (int) (to.x - ARR * Math.cos(ang + Math.PI / 6));
            int y2 = (int) (to.y - ARR * Math.sin(ang + Math.PI / 6));
            g2.drawLine(to.x, to.y, x1, y1);
            g2.drawLine(to.x, to.y, x2, y2);
        }
    }

    /* ===================== PAINEL: ANIMAÇÃO ===================== */

    private static class PainelAnimacao extends JPanel {
        // Imagens (opcionais)
        private final Image imgF, imgO, imgL, imgC;

        // Posições atuais (x) – serão ajustadas conforme lado
        private int xF = 40, xO = 40, xL = 40, xC = 40;

        // Lado atual (para sincronizar quando necessário)
        private String ladoF = "e", ladoO = "e", ladoL = "e", ladoC = "e";

        // Y fixos (linhas)
        private final int yF = 70, yO = 150, yL = 230, yC = 310;

        // Flag de animação em curso (evita sobreposição)
        private boolean animando = false;

        public PainelAnimacao() {
            setBackground(new Color(202, 235, 255));
            imgF = carregar("/img/fazendeiro.png");
            imgO = carregar("/img/ovelha.png");
            imgL = carregar("/img/lobo.png");
            imgC = carregar("/img/couve.png");
        }

        private Image carregar(String path) {
            try {
                java.net.URL url = getClass().getResource(path);
                if (url != null) return new ImageIcon(url).getImage();
            } catch (Exception ignored) {}
            return null; // fallback: desenhar formas
        }

        // Sincroniza as posições com o estado lógico (sem animação)
        public void syncComEstado(String f, String l, String o, String c) {
            ladoF = f; ladoL = l; ladoO = o; ladoC = c;
            int left = margemEsq();
            int right = margemDir();

            xF = f.equals("e") ? left : right;
            xL = l.equals("e") ? left : right;
            xO = o.equals("e") ? left : right;
            xC = c.equals("e") ? left : right;

            repaint();
        }

        // Anima uma travessia: "fazendeiro", "ovelha", "lobo" ou "couve"
        // paraDireita: true = e->d ; false = d->e
        public void animarTravessia(String quem, boolean paraDireita, Runnable onFinish) {
            if (animando) return; // simples proteção
            animando = true;

            int destino = paraDireita ? margemDir() : margemEsq();
            int passo = paraDireita ? 5 : -5;

            // Itens que se movem juntos
            boolean levaItem = !quem.equals("fazendeiro");

            Timer t = new Timer(20, null);
            t.addActionListener((ActionEvent e) -> {
                // Move fazendeiro
                xF += passo;

                // Move item, se for o caso
                switch (quem) {
                    case "ovelha" -> xO += passo;
                    case "lobo"   -> xL += passo;
                    case "couve"  -> xC += passo;
                }

                repaint();

                boolean chegou =
                        (passo > 0 && xF >= destino) ||
                        (passo < 0 && xF <= destino);

                if (chegou) {
                    ((Timer) e.getSource()).stop();
                    // Ajusta posições finais exatamente no destino
                    xF = destino;
                    if (levaItem) {
                        switch (quem) {
                            case "ovelha" -> xO = destino;
                            case "lobo"   -> xL = destino;
                            case "couve"  -> xC = destino;
                        }
                    }
                    animando = false;
                    if (onFinish != null) onFinish.run();
                }
            });
            t.start();
        }

        private int margemEsq() { return 40; }
        private int margemDir() { return Math.max(getWidth() - 120, 200); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Rio (visual simples)
            g2.setColor(new Color(160, 208, 255));
            g2.fillRect(0, 120, getWidth(), 120);

            // Margens
            g2.setColor(new Color(90, 190, 90));
            g2.fillRect(0, 0, 80, getHeight()); // margem esquerda
            g2.fillRect(getWidth() - 120, 0, 120, getHeight()); // margem direita

            // Desenha fazendeiro
            desenharEntidade(g2, imgF, xF, yF, new Color(40, 80, 200), "F");
            // Ovelha
            desenharEntidade(g2, imgO, xO, yO, Color.WHITE, "O");
            // Lobo
            desenharEntidade(g2, imgL, xL, yL, Color.GRAY, "L");
            // Couve
            desenharEntidade(g2, imgC, xC, yC, new Color(40, 160, 40), "C");

            g2.dispose();
        }

        private void desenharEntidade(Graphics2D g2, Image img, int x, int y, Color fallback, String label) {
            int w = 64, h = 64;
            if (img != null) {
                g2.drawImage(img, x, y - h/2, w, h, null);
            } else {
                g2.setColor(fallback);
                g2.fillOval(x, y - 20, 40, 40);
                g2.setColor(Color.BLACK);
                g2.drawString(label, x + 16, y - 20 + 28);
            }
        }
    }

    /* ===================== MAIN ===================== */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Travessia().setVisible(true));
    }
}
