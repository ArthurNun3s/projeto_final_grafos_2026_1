// ARQUIVO NOVO: Classe criada para ser o Motor de Análises


package br.com.unipe;

import java.util.*;

public class LinkedInAnalyzer {

    private Grafo redeSocial;

    // [IMPLEMENTAÇÃO - Missão 1] Construtor que recebe a rede
    public LinkedInAnalyzer(Grafo grafo) {
        this.redeSocial = grafo;
    }

    // [IMPLEMENTAÇÃO - Missão 2] Lógica de Sugestão de Conexões (Amigos de 2º Grau)
    public Map<String, Integer> sugerirConexoes(String nomeUsuario) {
        Vertice usuario = redeSocial.encontraVertice(nomeUsuario).orElse(null);
        if (usuario == null) {
            return new HashMap<>();
        }

        List<Vertice> amigosDiretos = usuario.getAdjacencias();
        Map<String, Integer> contagemAmigos = new HashMap<>();

        // Laços aninhados para rastrear os "amigos dos amigos"
        for (Vertice amigo : amigosDiretos) {
            for (Vertice amigoDoAmigo : amigo.getAdjacencias()) {
                boolean ehOUsuario = amigoDoAmigo.equals(usuario);
                boolean jaEhAmigo = amigosDiretos.contains(amigoDoAmigo);

                // Regras estipuladas: Não sugerir o próprio usuário, nem quem já é contato direto
                if (!ehOUsuario && !jaEhAmigo) {
                    String nomeAmigoDoAmigo = amigoDoAmigo.getNome();
                    int qtdAtual = contagemAmigos.getOrDefault(nomeAmigoDoAmigo, 0);
                    contagemAmigos.put(nomeAmigoDoAmigo, qtdAtual + 1); // Soma a ocorrência
                }
            }
        }

        // Ordenando do maior para o menor manualmente (quem tem mais amigos em comum fica no topo)
        List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(contagemAmigos.entrySet());
        listaOrdenada.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> item1, Map.Entry<String, Integer> item2) {
                return item2.getValue().compareTo(item1.getValue()); // Ordem decrescente
            }
        });

        // Inserindo num LinkedHashMap para manter a ordem estruturada
        Map<String, Integer> resultadoFinal = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entrada : listaOrdenada) {
            resultadoFinal.put(entrada.getKey(), entrada.getValue());
        }

        return resultadoFinal;
    }

    // [IMPLEMENTAÇÃO - Missão 3] Conta os saltos/passos usando Busca em Largura (BFS)
    public int grauDeSeparacao(String nomeOrigem, String nomeDestino) {
        Vertice origem = redeSocial.encontraVertice(nomeOrigem).orElse(null);
        Vertice destino = redeSocial.encontraVertice(nomeDestino).orElse(null);

        // Se algum perfil não existir ou forem de grupos separados (isolados)
        if (origem == null || destino == null) return -1;
        if (origem.equals(destino)) return 0;

        // BFS comum para explorar em camadas e achar a menor distância ignorando pesos
        Queue<Vertice> fila = new LinkedList<>();
        Map<Vertice, Integer> passos = new HashMap<>();

        fila.add(origem);
        passos.put(origem, 0);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();

            // Se achou o destino, retorna o número de saltos intermediários salvos
            if (atual.equals(destino)) {
                return passos.get(atual);
            }

            for (Vertice vizinho : atual.getAdjacencias()) {
                if (!passos.containsKey(vizinho)) {
                    passos.put(vizinho, passos.get(atual) + 1); // Soma +1 passo do vizinho ao atual
                    fila.add(vizinho);
                }
            }
        }

        return -1; // Retorna -1 se a fila esvaziar e não encontrar (isolados)
    }

    // [IMPLEMENTAÇÃO - Missão 4] Chama o algoritmo de Dijkstra implementado no Grafo.java
    public Grafo.DadosRota rotaDeMaiorAfinidade(String nomeOrigem, String nomeDestino) {
        return redeSocial.calcularDijkstra(nomeOrigem, nomeDestino);
    }

    // [IMPLEMENTAÇÃO - Missão 5] Varre toda a rede mapeando componentes conexos (Busca em Profundidade)
    public List<List<String>> mapearGruposIsolados() {
        List<List<String>> todosOsGrupos = new ArrayList<>();
        List<String> pessoasJaVisitadas = new ArrayList<>();

        // Varre a rede inteira usando o meu método novo "recuperarListaDeVertices"
        for (Vertice pessoa : redeSocial.recuperarListaDeVertices()) {
            if (!pessoasJaVisitadas.contains(pessoa.getNome())) {
                
                // O dfsIterativo puxa todo mundo que tem conexão alcançável a partir desta pessoa
                List<String> grupoAtual = redeSocial.dfsIterativo(pessoa.getNome(), null);
                
                // Adiciono todos na lista de visitados para ignorar nas próximas rodadas
                for (String membro : grupoAtual) {
                    if (!pessoasJaVisitadas.contains(membro)) {
                        pessoasJaVisitadas.add(membro);
                    }
                }
                todosOsGrupos.add(grupoAtual);
            }
        }
        return todosOsGrupos;
    }
}
