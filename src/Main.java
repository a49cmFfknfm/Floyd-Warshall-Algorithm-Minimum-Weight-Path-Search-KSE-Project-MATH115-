import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

class Graph{
    private int V; /// num of verticles
    private double[][] matrix;

    public Graph(int V){ ///  constructor for matrix with preferable amount of verticles
        this.V = V;
        this.matrix = new double[V][V]; /// Directed Weighted Graph skin.
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (i == j) {
                    matrix[i][j] = 0; /// no loops itself
                } else {
                    matrix[i][j] = Double.POSITIVE_INFINITY; ///until we don't use generateRandomGraph() method our graph don't have any edge
                }
            }
        }
    }

    private void addEdge(int from, int to, double weight) {
        matrix[from][to] = weight;
    }

    public void generateRandomGraph(double density) { /// density must be from 0(0%) to 1(100%)
        Random r = new Random();
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (i == j) continue;

                if (r.nextDouble() < density) { ///  generating weight from i to j using random double and after comparing
                    double randomWeight = r.nextInt(100) + 1; /// random weight in range from 1 to 100
                    addEdge(i, j, randomWeight);
                } else {
                    matrix[i][j] = Double.POSITIVE_INFINITY; /// infinity means there is no road i-to-j. we can not use 0 here because it means that verticles are in exact one place
                }
            }
        }
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public int getNumberOfVertices() {
        return V;
    }
}

class FloydWarshall{
    public double[][] shortestPaths;

     public FloydWarshall(double[][] matrix){
         int V = matrix.length; ///this only works for Directed Weighted Graph, because we have same amount of 'rows' and 'columns'(double array[][]), so when we getting length of this array we get first index, that is same as second. In another cases this method will not be valid.
         this.shortestPaths = runAlgorithm(matrix, V);
     }

    public double[][] runAlgorithm (double[][] matrix, int verticles) {
        double[][] shortestPaths = new double[verticles][verticles];
        int V = verticles;
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                shortestPaths[i][j] = matrix[i][j];
            }
        }

        for (int a = 0; a < V; a++) {
            for (int v1 = 0; v1 < V; v1++) {
                for (int v2 = 0; v2 < V; v2++) {
                    if (shortestPaths[v1][a] < Double.POSITIVE_INFINITY && shortestPaths[a][v2] < Double.POSITIVE_INFINITY) {
                        double nueva = shortestPaths[v1][a] + shortestPaths[a][v2];
                        if (nueva < shortestPaths[v1][v2]) {
                            shortestPaths[v1][v2] = nueva;
                        }
                    }

                }
            }

        }
//        for (int i = 0; i<V; i++){
//            if(shortestPaths[i][i] < 0){
//                throw new IllegalStateException("Graph has negative cycle (vetricle " + i + ")");
//            }
//        }

        return shortestPaths;
    }
}






public class Main {
    public static void main(String[] args) {
        Graph example1 = new Graph(5);
        int[] sizes = {20, 40, 60, 80, 100, 120, 140, 160, 180, 200};
        double[] densities = {0.1, 0.2, 0.4, 0.6, 0.8, 1};
        int iterations = 20;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileName = "results_" + timeStamp + ".csv";
        System.out.println("Created file: " + fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
            writer.write("Size,Density,Iteration,Time_ns");
            writer.newLine();
            int totalTests = sizes.length * densities.length * iterations;
            int counter = 0;

            for(int n:sizes){
                for (double d : densities) {
                    for (int i = 0; i<iterations; i++) {
                        Graph graph = new Graph(n);
                        graph.generateRandomGraph(d);
                        long startTime = System.nanoTime();
                        new FloydWarshall(graph.getMatrix());
                        long endTime = System.nanoTime();
                        long duration = (endTime - startTime);

                        String line = n + "," + d + "," + (i + 1) + "," + duration;
                        writer.write(line);
                        writer.newLine();
                        counter++;

                        if (counter % 100 == 0){
                            System.out.println("Progress: " + counter + " out of " + totalTests + "tests completed");

                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }
    }