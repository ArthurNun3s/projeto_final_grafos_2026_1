package br.com.unipe;

import java.util.*;

public class LinkedInAnalyzer {

    private Grafo redeSocial;

    // Missão 1: Construtor da Análise
    public LinkedInAnalyzer(Grafo grafo) {
        this.redeSocial = grafo;
    }

    // Missão 2: Sugestão de Conexões (Amigos de 2º Grau)
    public Map<String, Integer> sugerirConexoes(String nomeUsuario) {
        Vertice usuario = redeSocial.encontraVertice(nomeUsuario).orElse(null);
        if (usuario == null) {
            return new HashMap<>();
        }

        List<Vertice> amigosDiretos = usuario.getAdjacencias();
        Map<String, Integer> contagemAmigos = new HashMap<>();

        for (Vertice amigo : amigosDiretos) {
            for (Vertice amigoDoAmigo : amigo.getAdjacencias()) {
                boolean ehOUsuario = amigoDoAmigo.equals(usuario);
                boolean jaEhAmigo = amigosDiretos.contains(amigoDoAmigo);

                // Regras: Não sugerir o próprio usuário, nem quem já é contato de 1º grau
                if (!ehOUsuario && !jaEhAmigo) {
                    String nomeAmigoDoAmigo = amigoDoAmigo.getNome();
                    int qtdAtual = contagemAmigos.getOrDefault(nomeAmigoDoAmigo, 0);
                    contagemAmigos.put(nomeAmigoDoAmigo, qtdAtual + 1);
                }
            }
        }

        // Ordenando do maior para o menor manualmente (estilo mais básico)
        List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(contagemAmigos.entrySet());
        listaOrdenada.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> item1, Map.Entry<String, Integer> item2) {
                return item2.getValue().compareTo(item1.getValue()); // Ordem decrescente
            }
        });

        // Colocando em um LinkedHashMap para manter a ordem que acabamos de criar
        Map<String, Integer> resultadoFinal = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entrada : listaOrdenada) {
            resultadoFinal.put(entrada.getKey(), entrada.getValue());
        }

        return resultadoFinal;
    }

    // Missão 3: Grau de Separação (Quantos "passos" de distância)
    public int grauDeSeparacao(String nomeOrigem, String nomeDestino) {
        Vertice origem = redeSocial.encontraVertice(nomeOrigem).orElse(null);
        Vertice destino = redeSocial.encontraVertice(nomeDestino).orElse(null);

        // Se algum não existir ou forem isolados
        if (origem == null || destino == null) return -1;
        if (origem.equals(destino)) return 0;

        // Utilizamos uma Busca em Largura (BFS) comum para rastrear passos
        Queue<Vertice> fila = new LinkedList<>();
        Map<Vertice, Integer> passos = new HashMap<>();

        fila.add(origem);
        passos.put(origem, 0);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();

            // Se achou o destino, retorna o número de conexões intermediárias registradas
            if (atual.equals(destino)) {
                return passos.get(atual);
            }

            for (Vertice vizinho : atual.getAdjacencias()) {
                if (!passos.containsKey(vizinho)) {
                    passos.put(vizinho, passos.get(atual) + 1);
                    fila.add(vizinho);
                }
            }
        }

        return -1; // Totalmente isolados, retorna -1 como pedido na regra
    }

    // Missão 4: Rota e Custo de Maior Afinidade
    public Grafo.DadosRota rotaDeMaiorAfinidade(String nomeOrigem, String nomeDestino) {
        // Aproveita o Dijkstra "disfarçado" que adicionamos na classe Grafo
        return redeSocial.calcularDijkstra(nomeOrigem, nomeDestino);
    }

    // Missão 5: Mapear Grupos Isolados (Sub-redes)
    public List<List<String>> mapearGruposIsolados() {
        List<List<String>> todosOsGrupos = new ArrayList<>();
        List<String> pessoasJaVisitadas = new ArrayList<>();

        // Varre a rede inteira usando o novo método customizado
        for (Vertice pessoa : redeSocial.recuperarListaDeVertices()) {
            if (!pessoasJaVisitadas.contains(pessoa.getNome())) {
                
                // Puxa todo mundo que tem conexão com essa pessoa
                List<String> grupoAtual = redeSocial.dfsIterativo(pessoa.getNome(), null);
                
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