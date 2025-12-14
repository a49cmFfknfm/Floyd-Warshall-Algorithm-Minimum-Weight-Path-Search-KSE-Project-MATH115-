import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

class Graph {
    private final int V; // кількість вершин
    private final double[][] matrix;

    public Graph(int V) { //  конструктор для матриці з даною кількістю вершин
        this.V = V;
        this.matrix = new double[V][V]; // орієнтований зважений граф
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (i == j) {
                    matrix[i][j] = 0; // петлі = 0
                } else {
                    matrix[i][j] = Double.POSITIVE_INFINITY; // порожня матриця містить скрізь нескінченність доки не згенеровано випадкові числа
                }
            }
        }
    }

    private void addEdge(int from, int to, double weight) {
        matrix[from][to] = weight;
    }

    public void generateRandomGraph(double density) { // щільність має бути від 0 (0%) до 1 (100%)
        Random r = new Random();
//        while (true) {

            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (i == j) continue;

                    if (r.nextDouble() < density) { //  генерування випадкової ваги відповідно до щільності
                        double randomWeight = r.nextInt(-100, 100) + 1; // вага від - 100 до 100
                        addEdge(i, j, randomWeight);
                    }
//                    else {
//                        matrix[i][j] = Double.POSITIVE_INFINITY; //
//                    }
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

class FloydWarshall {
    public double[][] shortestPaths;

    public FloydWarshall(double[][] matrix) {
        int V = matrix.length;
        this.shortestPaths = runAlgorithm(matrix, V);
    }

    public double[][] runAlgorithm(double[][] matrix, int vertices) {
        double[][] shortestPaths = new double[vertices][vertices];

        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                shortestPaths[i][j] = matrix[i][j];
            }
        }

        for (int a = 0; a < vertices; a++) {
            for (int v1 = 0; v1 < vertices; v1++) {
                for (int v2 = 0; v2 < vertices; v2++) {
                    if (shortestPaths[v1][a] < Double.POSITIVE_INFINITY && shortestPaths[a][v2] < Double.POSITIVE_INFINITY) {
                        double nueva = shortestPaths[v1][a] + shortestPaths[a][v2];
                        if (nueva < shortestPaths[v1][v2]) {
                            shortestPaths[v1][v2] = nueva;
                        }
                    }

                }
            }

        }
        // Перевірка на негативний цикл.
        boolean hasNegativeCycle = false;
        for (int i = 0; i < vertices; i++) {
            if (shortestPaths[i][i] < 0) {
                hasNegativeCycle = true;
//                System.out.println("Граф містить негативний цикл, який проходить через вершину " + i);
            }
        }
        if (!hasNegativeCycle) {
//            System.out.println("Негативних циклів немає.");
        }
        return shortestPaths;
    }
}


public class Main {
    public static void main(String[] args) {
        System.out.println("1. Generate a graph.");
        System.out.println("2. Do all experiments.");
        System.out.print("    -> ");
        Scanner reader = new Scanner(System.in);
        String userChoice = reader.nextLine();
        if (userChoice.equals("1")) {
            System.out.print("Number of vertices (from 1 to 200): ");
            int vertices = reader.nextInt();
            Graph randomGraph = new Graph(vertices);
            System.out.print("Density (from 0.0 to 1.0): ");
            double density = reader.nextDouble();
            randomGraph.generateRandomGraph(density);
            double[][] matrix = randomGraph.getMatrix();
            for (int i = 0; i < matrix.length; i++) { // виведення матриці графа в консоль
                for (int j = 0; j < matrix[i].length; j++) {
                    if (matrix[i][j] == Double.POSITIVE_INFINITY) {
                        System.out.print("∞ ");
                    }
                    else {
                        System.out.print(matrix[i][j] + " ");
                    }

                }
                System.out.println();
            }
        } else if (userChoice.equals("2")) {
            int[] sizes = {20, 40, 60, 80, 100, 120, 140, 160, 180, 200};
            double[] densities = {0.1, 0.2, 0.4, 0.6, 0.8, 1};
            int iterations = 20;
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String fileName = "results_" + timeStamp + ".csv";
            System.out.println("Created file: " + fileName);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write("Size,Density,Iteration,Time_ns");
                writer.newLine();
                int totalTests = sizes.length * densities.length * iterations;
                int counter = 0;

                for (int n : sizes) {
                    for (double d : densities) {
                        for (int i = 0; i < iterations; i++) {
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

                            if (counter % 100 == 0) {
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
}