package maze_escape;

import java.util.function.BiFunction;

public class HugePrimMazeEscapeMain {
    private static final long SEED = 20221206L;

    private static final int WIDTH = 5000;
    private static final int HEIGHT = WIDTH;
    private static final int REMOVE = 2 * WIDTH;

    public static void main(String[] args) {
        System.out.println("Welcome to the HvA Huge Maze Escape");

        // generate a random maze
        Maze.reSeedRandomizer(SEED);
        Maze maze = new Maze(WIDTH, HEIGHT);
        maze.generateRandomizedPrim();
        maze.configureInnerEntry();
        maze.removeRandomWalls(REMOVE);
        System.out.printf("\nCreated %dx%d Randomized-Prim-Maze(%d) with %d walls removed\n", WIDTH, HEIGHT, SEED, REMOVE);

        doPathSearches(maze, "Breadth First Search", maze::breadthFirstSearch);
        doPathSearches(maze, "Dijkstra Shortest Path", (v1,v2)-> maze.dijkstraShortestPath(v1,v2,maze::manhattanTime));
        //doPathSearches(maze, "Depth First Search", maze::depthFirstSearch);
    }

    private static void doPathSearches(Maze maze, String title, BiFunction<Integer,Integer,Maze.GPath> searcher) {

        System.out.printf("\nResults from '%s' in %dx%d maze from vertex '%d' to '%d':\n",
                title, maze.getWidth(), maze.getHeight(), maze.getStartNode(), maze.getExitNode());

        // find the escape
        Maze.GPath path = searcher.apply(maze.getStartNode(), maze.getExitNode());
        if (path.getTotalWeight() == 0.0) path.reCalculateTotalWeight(maze::manhattanTime);
        System.out.println(title + ": " + path);

        // find the return
        path = searcher.apply(maze.getExitNode(), maze.getStartNode());
        if (path.getTotalWeight() == 0.0) path.reCalculateTotalWeight(maze::manhattanTime);
        System.out.println(title + " return: " + path);
    }
}
