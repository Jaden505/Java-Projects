package maze_escape;

import java.util.Set;
import java.util.function.BiFunction;

public class PrimMazeEscapeMain {

    private static final long SEED = 20221203L;
    private static final int WIDTH = 100;
    private static final int HEIGHT = WIDTH;
    private static final int REMOVE = 250;

    public static void main(String[] args) {
        System.out.println("Welcome to the HvA Maze Escape");

        // generate a random maze
        Maze.reSeedRandomizer(SEED);
        Maze maze = new Maze(WIDTH, HEIGHT);
        maze.generateRandomizedPrim();
        maze.configureInnerEntry();
        maze.removeRandomWalls(REMOVE);
        System.out.printf("\nCreated %dx%d Randomized-Prim-Maze(%d) with %d walls removed\n", WIDTH, HEIGHT, SEED, REMOVE);

        //maze.print();
        Set<Integer> vertices = maze.getAllVertices(maze.getStartNode());

        System.out.printf("Maze-Graph contains %d connected vertices in %d cells\n",
                vertices.size(), maze.getNumberOfCells());
        //System.out.println(maze.formatAdjacencyList(maze.getStartNode()));

        doPathSearches(maze, "Depth First Search", maze::depthFirstSearch, vertices);
        doPathSearches(maze, "Breadth First Search", maze::breadthFirstSearch, vertices);
        doPathSearches(maze, "Dijkstra Shortest Path",
                (v1,v2)-> maze.dijkstraShortestPath(v1,v2,maze::manhattanTime), vertices);

    }

    private static void doPathSearches(Maze maze, String title, BiFunction<Integer,Integer,Maze.GPath> searcher, Set<Integer> vertices) {

        System.out.printf("\nResults from '%s' in %dx%d maze from vertex '%d' to '%d':\n",
                title, maze.getWidth(), maze.getHeight(), maze.getStartNode(), maze.getExitNode());

        // find the escape
        Maze.GPath path = searcher.apply(maze.getStartNode(), maze.getExitNode());
        if (path.getTotalWeight() == 0.0) path.reCalculateTotalWeight(maze::manhattanTime);
        System.out.println(title + ": " + path);
        maze.svgDrawMap(String.format("%s-%d-%d.svg", title.replaceAll(" ", ""), maze.getWidth(), maze.getHeight()), vertices, path);

        // find the return
        path = searcher.apply(maze.getExitNode(), maze.getStartNode());
        if (path.getTotalWeight() == 0.0) path.reCalculateTotalWeight(maze::manhattanTime);
        System.out.println(title + " return: " + path);
        maze.svgDrawMap(String.format("%s-R-%d-%d.svg", title.replaceAll(" ", ""), maze.getWidth(), maze.getHeight()), vertices, path);
    }
}
