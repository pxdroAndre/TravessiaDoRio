import java.util.Scanner;

/**
 * Solução em Java para o problema da Travessia do Rio (Fazendeiro, Lobo, Ovelha e Couve).
 * O código é uma tradução direta do pseudocódigo fornecido.
 */
public class TravessiaDoRio {

    // Variáveis para armazenar a posição de cada elemento ('e' para esquerda, 'd' para direita)
    private static String fazendeiro;
    private static String lobo;
    private static String ovelha;
    private static String couve;

    /**
     * Função principal que executa o programa.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- Leitura do estado inicial ---
        System.out.println("--- CONFIGURAÇÃO INICIAL ---");
        System.out.print("Digite a posição inicial do Fazendeiro (e/d): ");
        fazendeiro = scanner.nextLine().toLowerCase();

        System.out.print("Digite a posição inicial do Lobo (e/d): ");
        lobo = scanner.nextLine().toLowerCase();

        System.out.print("Digite a posição inicial da Ovelha (e/d): ");
        ovelha = scanner.nextLine().toLowerCase();

        System.out.print("Digite a posição inicial da Couve (e/d): ");
        couve = scanner.nextLine().toLowerCase();

        // Identifica e mostra o estado inicial
        String estado = identificarEstado(fazendeiro, lobo, ovelha, couve);
        System.out.println("\nEstado inicial: " + estado);
        System.out.println("---------------------------\n");


        // Verifica se a configuração inicial já representa um perigo
        verificarPerigo(fazendeiro, lobo, ovelha, couve);

        // --- Loop de movimentos ---
        while (true) {
            System.out.println("Posições atuais -> Fazendeiro: " + fazendeiro + ", Lobo: " + lobo + ", Ovelha: " + ovelha + ", Couve: " + couve);
            System.out.print("Digite o movimento (OD, OE, LD, LE, CD, CE, FD, FE ou 'fim' para sair): ");
            String movimento = scanner.nextLine().toUpperCase(); // Converte para maiúsculas para facilitar a comparação

            if (movimento.equals("FIM")) {
                System.out.println("Fim do programa.");
                break; // Sai do loop
            }

            // A lógica de movimento é tratada em um método separado para organização
            aplicarMovimento(movimento);

            // Verifica o perigo após o movimento
            verificarPerigo(fazendeiro, lobo, ovelha, couve);

            // Mostra o novo estado
            estado = identificarEstado(fazendeiro, lobo, ovelha, couve);
            System.out.println(">>> Novo estado: " + estado + "\n");

            // Verifica se o jogador venceu
            if (estado.equals("F8")) {
                System.out.println("🎉 PARABÉNS! Você conseguiu atravessar todos com segurança! 🎉");
                break;
            }
        }

        scanner.close(); // Boa prática fechar o scanner ao final
    }

    /**
     * Mapeia a configuração das posições para um nome de estado.
     * Corresponde à função identificarEstado do pseudocódigo.
     */
    public static String identificarEstado(String faz, String lob, String ove, String cou) {
        // Usa String.format para criar a chave de estado de forma mais limpa
        String estadoAtual = String.format("%s,%s,%s,%s", faz, lob, ove, cou);

        switch (estadoAtual) {
            case "e,e,e,e" -> {
                return "F1 (Início)";
            }
            case "d,e,d,e" -> {
                return "F2";
            }
            case "e,e,d,e" -> {
                return "F3";
            }
            case "d,e,e,d" -> {
                return "F4";
            }
            case "e,e,e,d" -> {
                return "F5";
            }
            case "d,d,e,e" -> {
                return "F6";
            }
            case "e,d,e,e" -> {
                return "F7";
            }
            case "d,d,d,d" -> {
                return "F8 (Vitória)";
            }
            // F9 é idêntico a F2 no seu pseudocódigo
            // case "d,e,d,e": return "F9";
            case "e,d,e,d" -> {
                return "F10";
            }
            case "d,d,e,d" -> {
                return "F11";
            }
            case "e,d,d,e" -> {
                return "F12";
            }
            default -> {
                return "Configuração inválida";
            }
        }
    }

    /**
     * Verifica se uma condição de derrota foi atingida.
     * Corresponde à função verificarPerigo do pseudocódigo.
     * Se uma condição de perigo é encontrada, o programa é encerrado.
     */
    public static void verificarPerigo(String faz, String lob, String ove, String cou) {
        // Se o fazendeiro está na esquerda, o perigo está na margem direita
        if (faz.equals("e")) {
            if (lob.equals("d") && ove.equals("d")) {
                System.out.println("\n💀 PERDEU! O lobo comeu a ovelha na margem direita!");
                System.exit(0); // Encerra o programa
            }
            if (ove.equals("d") && cou.equals("d")) {
                System.out.println("\n💀 PERDEU! A ovelha comeu a couve na margem direita!");
                System.exit(0); // Encerra o programa
            }
        }
        // Se o fazendeiro está na direita, o perigo está na margem esquerda
        else if (faz.equals("d")) {
            if (lob.equals("e") && ove.equals("e")) {
                System.out.println("\n💀 PERDEU! O lobo comeu a ovelha na margem esquerda!");
                System.exit(0); // Encerra o programa
            }
            if (ove.equals("e") && cou.equals("e")) {
                System.out.println("\n💀 PERDEU! A ovelha comeu a couve na margem esquerda!");
                System.exit(0); // Encerra o programa
            }
        }
    }

    /**
     * Processa a lógica de movimento baseada na entrada do usuário.
     */
    private static void aplicarMovimento(String mov) {
        switch (mov) {
            // Levar a Ovelha para a Direita
            case "OD":
                if (fazendeiro.equals("e") && ovelha.equals("e")) {
                    ovelha = "d"; fazendeiro = "d";
                } else {
                    System.out.println("Movimento inválido! O fazendeiro e a ovelha devem estar na esquerda.");
                }
                break;
            // Levar a Ovelha para a Esquerda
            case "OE":
                if (fazendeiro.equals("d") && ovelha.equals("d")) {
                    ovelha = "e"; fazendeiro = "e";
                } else {
                    System.out.println("Movimento inválido! O fazendeiro e a ovelha devem estar na direita.");
                }
                break;
            // Levar o Lobo para a Direita
            case "LD":
                if (fazendeiro.equals("e") && lobo.equals("e")) {
                    lobo = "d"; fazendeiro = "d";
                } else {
                    System.out.println("Movimento inválido! O fazendeiro e o lobo devem estar na esquerda.");
                }
                break;
            // Levar o Lobo para a Esquerda
            case "LE":
                if (fazendeiro.equals("d") && lobo.equals("d")) {
                    lobo = "e"; fazendeiro = "e";
                } else {
                    System.out.println("Movimento inválido! O fazendeiro e o lobo devem estar na direita.");
                }
                break;
            // Levar a Couve para a Direita
            case "CD":
                if (fazendeiro.equals("e") && couve.equals("e")) {
                    couve = "d"; fazendeiro = "d";
                } else {
                    System.out.println("Movimento inválido! O fazendeiro e a couve devem estar na esquerda.");
                }
                break;
            // Levar a Couve para a Esquerda
            case "CE":
                if (fazendeiro.equals("d") && couve.equals("d")) {
                    couve = "e"; fazendeiro = "e";
                } else {
                    System.out.println("Movimento inválido! O fazendeiro e a couve devem estar na direita.");
                }
                break;
            // Levar apenas o Fazendeiro para a Direita
            case "FD":
                if (fazendeiro.equals("e")) {
                    fazendeiro = "d";
                } else {
                    System.out.println("Movimento inválido! O fazendeiro já está na direita.");
                }
                break;
            // Levar apenas o Fazendeiro para a Esquerda
            case "FE":
                if (fazendeiro.equals("d")) {
                    fazendeiro = "e";
                } else {
                    System.out.println("Movimento inválido! O fazendeiro já está na esquerda.");
                }
                break;
            default:
                System.out.println("Comando de movimento inválido!");
                break;
        }
    }
}
