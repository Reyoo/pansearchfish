package top.findfish.crawler.learnCode;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: Graph.java
 * @包 路 径： top.findfish.crawler.learnCode
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2023/10/26 09:47
 */

import java.util.ArrayList;
import java.util.List;

class Graph {
    private int V;
    private List<List<Integer>> adj;

    public Graph(int V) {
        this.V = V;
        adj = new ArrayList<>(V);
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int v, int w) {
        adj.get(v).add(w);
    }

    public boolean isDirected() {
        for (int v = 0; v < V; v++) {
            for (int w : adj.get(v)) {
                if (!adj.get(w).contains(v)) {
                    return true; // 存在一个边 (v, w)，但没有对应的 (w, v) 边，因此是有向图
                }
            }
        }
        return false; // 所有边都有对应的反向边，因此是无向图
    }

    public static void main(String[] args) {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);

        boolean isDirected = graph.isDirected();
        if (isDirected) {
            System.out.println("这是一个有向图。");
        } else {
            System.out.println("这是一个无向图。");
        }
    }

}
