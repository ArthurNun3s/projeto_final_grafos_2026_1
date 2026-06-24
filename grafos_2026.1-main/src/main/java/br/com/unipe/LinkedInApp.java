package br.com.unipe;

import java.util.List;
import java.util.Map;

public class LinkedInApp {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO O LINKEDIN ANALYZER ===\n");

        // 1. Instanciando o Grafo (Não-dirigido e Ponderado)
        Grafo rede = new Grafo(false, true);

        // 2. Adicionando os usuários sugeridos no documento do projeto
        rede.adicionaVertices("Ana", "Bruno", "Carlos", "Daniela", "Eduardo", "Fernanda", "Gabriel", "Hugo", "Igor", "Juliana");

        // 3. Cadastrando as conexões e os pesos (afinidades)
        rede.addAresta("Ana", "Bruno", 1);
        rede.addAresta("Ana", "Carlos", 2);
        rede.addAresta("Ana", "Daniela", 8);
        rede.addAresta("Bruno", "Eduardo", 1);
        rede.addAresta("Carlos", "Eduardo", 1);
        rede.addAresta("Daniela", "Fernanda", 5);
        rede.addAresta("Eduardo", "Fernanda", 1);

        // Grupo isolado 1
        rede.addAresta("Gabriel", "Hugo", 1);

        // Grupo isolado 2
        rede.addAresta("Igor", "Juliana", 1);

        // 4. Instanciando o motor de análises (Missão 1)
        LinkedInAnalyzer analisador = new LinkedInAnalyzer(rede);

        // --- TESTANDO A MISSÃO 2: Sugestão de Conexões ---
        System.out.println(">>> MISSÃO 2: Sugestões de conexões para Ana");
        Map<String, Integer> sugestoesAna = analisador.sugerirConexoes("Ana");
        if (sugestoesAna.isEmpty()) {
            System.out.println("Nenhuma sugestão encontrada.");
        } else {
            for (Map.Entry<String, Integer> sugestao : sugestoesAna.entrySet()) {
                System.out.println("- " + sugestao.getKey() + " (" + sugestao.getValue() + " amigos em comum)");
            }
        }
        System.out.println();

        // --- TESTANDO A MISSÃO 3: Grau de Separação ---
        System.out.println(">>> MISSÃO 3: Grau de Separação (Passos)");
        int passos = analisador.grauDeSeparacao("Ana", "Fernanda");
        System.out.println("Distância entre Ana e Fernanda: " + passos + " passos.");
        int passosIsolados = analisador.grauDeSeparacao("Ana", "Igor");
        System.out.println("Distância entre Ana e Igor (Isolados): " + passosIsolados + " (Retornou -1 como pedido).");
        System.out.println();

        // --- TESTANDO A MISSÃO 4: Rota de Maior Afinidade (Dijkstra) ---
        System.out.println(">>> MISSÃO 4: Rota de Maior Afinidade (Menor Custo)");
        Grafo.DadosRota melhorRota = analisador.rotaDeMaiorAfinidade("Ana", "Fernanda");
        System.out.println("De Ana para Fernanda:");
        System.out.println("Custo (Soma dos pesos): " + melhorRota.custoTotal);
        System.out.println("Caminho: " + String.join(" -> ", melhorRota.sequenciaNos));
        System.out.println();

        // --- TESTANDO A MISSÃO 5: Mapear Grupos Isolados ---
        System.out.println(">>> MISSÃO 5: Mapeamento de Grupos Isolados na Rede");
        List<List<String>> grupos = analisador.mapearGruposIsolados();
        int contador = 1;
        for (List<String> grupo : grupos) {
            System.out.println("Sub-rede " + contador + ": " + String.join(", ", grupo));
            contador++;
        }
    }
}