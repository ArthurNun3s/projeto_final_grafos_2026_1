package br.com.unipe;

import java.util.*;

public class LinkedInAnalyzer {

    private Grafo redeSocial;

    //A linha do construtor recebe o grafo e guarda na variável da classe.
    public LinkedInAnalyzer(Grafo grafo) {
        this.redeSocial = grafo;
    }

    //O método de sugerir conexões tem dois laços "for" aninhados. O primeiro passa pelos amigos diretos. O segundo passa pelos amigos desses amigos.
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

                //A linha deste "if" aplica a regra principal. Ela impede de sugerir o próprio usuário ou quem já é contato direto.
                if (!ehOUsuario && !jaEhAmigo) {
                    String nomeAmigoDoAmigo = amigoDoAmigo.getNome();
                    int qtdAtual = contagemAmigos.getOrDefault(nomeAmigoDoAmigo, 0);
                    contagemAmigos.put(nomeAmigoDoAmigo, qtdAtual + 1); 
                }
            }
        }

        //As ocorrências são contadas no mapa. Depois, o código usa um comparador para ordenar a lista. O topo fica com quem tem mais amigos em comum.
        List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(contagemAmigos.entrySet());
        listaOrdenada.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> item1, Map.Entry<String, Integer> item2) {
                return item2.getValue().compareTo(item1.getValue()); 
            }
        });

        Map<String, Integer> resultadoFinal = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entrada : listaOrdenada) {
            resultadoFinal.put(entrada.getKey(), entrada.getValue());
        }

        return resultadoFinal;
    }

    //O grau de separação usa uma Busca em Largura.
    public int grauDeSeparacao(String nomeOrigem, String nomeDestino) {
        Vertice origem = redeSocial.encontraVertice(nomeOrigem).orElse(null);
        Vertice destino = redeSocial.encontraVertice(nomeDestino).orElse(null);

        if (origem == null || destino == null) return -1;
        if (origem.equals(destino)) return 0;

        //O código usa uma Fila comum para explorar as conexões em camadas. A cada vizinho visitado ele soma 1 salto na contagem. Se a fila esvaziar e não achar o destino a função retorna -1. Isso prova que os nós estão isolados.
        Queue<Vertice> fila = new LinkedList<>();
        Map<Vertice, Integer> passos = new HashMap<>();

        fila.add(origem);
        passos.put(origem, 0);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();

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

        return -1; 
    }

    //A rota de maior afinidade é simples. A linha apenas chama o algoritmo de Dijkstra implementado no grafo.
    public Grafo.DadosRota rotaDeMaiorAfinidade(String nomeOrigem, String nomeDestino) {
        return redeSocial.calcularDijkstra(nomeOrigem, nomeDestino);
    }

    //O mapeamento de grupos isolados cria uma lista de visitados. Um laço "for" passa por todos os vértices da rede.
    public List<List<String>> mapearGruposIsolados() {
        List<List<String>> todosOsGrupos = new ArrayList<>();
        List<String> pessoasJaVisitadas = new ArrayList<>();

        for (Vertice pessoa : redeSocial.recuperarListaDeVertices()) {
            if (!pessoasJaVisitadas.contains(pessoa.getNome())) {
                
                //Se a pessoa não foi visitada o código aciona a Busca em Profundidade. Essa linha levanta todos os perfis daquele grupo isolado. Todos são marcados como visitados e o bloco é salvo.
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