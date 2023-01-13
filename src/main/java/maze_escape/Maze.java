package maze_escape;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

public class Maze extends AbstractGraph<Integer> {

    /**
     * This class represents a rectangular maze of dimensions width * height of square cells
     * Cells are identified by coordinates (x,y): 0 <= x < this.width, 0 <= y < this.height
     * Cell[0,0] is at the top-left on the map of the maze.
     *
     * Each cell is a (candidate) vertex in AbstractGraph<Integer>
     * this.cellNumber(x,y) calculates the integer vertex number from given cell coordinates x and y
     * this.posX(number) and this.posY(number) calculate the cell coordinates x and y from a given vertex number
     * this.getNeighbours(number) calculates a set of neighbour cell/vertex numbers from a given vertex number
     *
     * Each cell is bounded by upto four walls in directions NORTH, EAST, SOUTH and WEST
     * The maze can be traversed along passages that are not blocked by a wall
     * this.getNeighbours(number) skips pass-through cells that have exactly two walls, if possible.
     */
    private int width;
    private int height;
    private int startNode;
    private int exitNode;

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    /**
     * compact representation of all different walls in the maze, minimizing memory footprint
     * notice: southWall[x,y] == northWall[x,y+1], eastWall[x,y] = westWall[x+1,y]
     * so we only need to represent northWalls and westWalls
     */
    private boolean[][] northWalls;
    private boolean[][] westWalls;

    // some extra constants supporting navigation
    private final static int NUM_DIRECTIONS = Direction.values().length;
    private final static int[] DELTA_X = {0, +1, 0, -1};
    private final static int[] DELTA_Y = {-1, 0, +1, 0};

    /**
     * the randomizer is used to generate random mazes
     * Repreducible results of calculations can be obtained by fixing the seed of the randomizer
     */
    private static Random randomizer = new Random();

    public static void reSeedRandomizer(long seed) {
        randomizer = new Random(seed);
    }


    public Maze(int width, int height) {
        this.width = width;
        this.height = height;

        // we need one extra vertically, because southWall[x,HEIGHT-1] == northWall[x,HEIGHT]
        this.northWalls = new boolean[width][height + 1];

        // we need one extra horizontally, because eastWall[WIDTH-1,y] == westWall[WIDTH,y]
        this.westWalls = new boolean[width + 1][height];
    }

    /**
     * calculate the cell/vertex number of given coordinate positions x, y
     * @param x
     * @param y
     * @return
     */
    private int cellNumber(int x, int y) {
        return x + y * this.width;
    }

    /**
     * calculate the X-coordinate position of given cell/vertex number
     * @param cell
     * @return
     */
    private int posX(int cell) {
        return cell % this.width;
    }

    /**
     * calculate the Y-coordinate position of given cell/vertex number
     * @param cell
     * @return
     */
    private int posY(int cell) {
        return cell / this.width;
    }

    /**
     * check existance of a wall in specified direction of cell(x,y)
     * @param x
     * @param y
     * @param direction
     * @return
     */
    public boolean getWall(int x, int y, Direction direction) {
        switch (direction) {
            case NORTH:
                return this.northWalls[x][y];
            case EAST:
                return this.westWalls[x + 1][y];
            case SOUTH:
                return this.northWalls[x][y + 1];
            case WEST:
                return this.westWalls[x][y];
        }
        return false;
    }

    private boolean getWall(int cell, Direction direction) {
        return this.getWall(posX(cell), posY(cell), direction);
    }

    /**
     * create or remove a wall in specified direction of cell(x,y)
     * @param x
     * @param y
     * @param direction
     * @param value     use true to create, false to remove
     */
    public void setWall(int x, int y, Direction direction, boolean value) {
        switch (direction) {
            case NORTH:
                this.northWalls[x][y] = value;
                break;
            case EAST:
                this.westWalls[x + 1][y] = value;
                break;
            case SOUTH:
                this.northWalls[x][y + 1] = value;
                break;
            case WEST:
                this.westWalls[x][y] = value;
                break;
        }
    }

    private void setWall(int cell, Direction direction, boolean value) {
        this.setWall(posX(cell), posY(cell), direction, value);
    }

    /**
     * create or remove all walls surrounding cell(x,y)
     * @param x
     * @param y
     * @param value     use true to create, false to remove
     */
    public void setWalls(int x, int y, boolean value) {
        this.northWalls[x][y] = value;
        this.westWalls[x + 1][y] = value;
        this.northWalls[x][y + 1] = value;
        this.westWalls[x][y] = value;
    }

    private void setWalls(int cell, boolean value) {
        this.setWalls(posX(cell), posY(cell), value);
    }

    /**
     * calculate the number of walls found at the cell with given coordinates x,y
     * @param x
     * @param y
     * @return
     */
    private int getNumWalls(int x, int y) {
        int numWalls = 0;
        for (Direction direction : Direction.values())
            numWalls += this.getWall(x, y, direction) ? 1 : 0;
        return numWalls;
    }

    private int getNumWalls(int cell) {
        return this.getNumWalls(posX(cell), posY(cell));
    }

    /**
     * calculate the number of walls that touch the top-left(north-west) corner
     * of the cell with given coordinates x,y
     * @param x     0 <= x <= this.width
     * @param y     0 <= y <= this.height
     * @return
     */
    private int getNumCornerWalls(int x, int y) {
        int numWalls = 0;
        if (y < this.height) {
            numWalls += (x < this.width && this.getWall(x, y, Direction.NORTH) ? 1 : 0);        // check right
            numWalls += (x > 0 && this.getWall(x - 1, y, Direction.NORTH)) ? 1 : 0;             // check left
        } else {
            numWalls += (x < this.width && this.getWall(x, y - 1, Direction.SOUTH) ? 1 : 0);    // check right
            numWalls += (x > 0 && this.getWall(x - 1, y - 1, Direction.SOUTH)) ? 1 : 0;         // check left
        }
        if (x < this.width) {
            numWalls += (y < this.height && this.getWall(x, y, Direction.WEST) ? 1 : 0);        // check down
            numWalls += (y > 0 && this.getWall(x, y - 1, Direction.WEST)) ? 1 : 0;              // check up
        } else {
            numWalls += (y < this.height && this.getWall(x - 1, y, Direction.EAST) ? 1 : 0);    // check down
            numWalls += (y > 0 && this.getWall(x - 1, y - 1, Direction.EAST)) ? 1 : 0;          // check up
        }
        return numWalls;
    }

    /**
     * Calculates the direct neighbour in the maze in specified direction
     * from the cell with given coordinates x,y
     * ignores the walls as a potential obstruction
     * @param x
     * @param y
     * @param direction
     * @return  the cell number of the neighbour cell, if found within the maze boundaries
     *          or -1 if the direction traverses out of the rectangular boundary of the maze.
     */
    private int getDirectNeighbour(int x, int y, Direction direction) {

        // compute neighbour position coordinates
        int neighbourX = x + DELTA_X[direction.ordinal()];
        int neighbourY = y + DELTA_Y[direction.ordinal()];

        // check if we exceeded any boundary
        if (neighbourX < 0 || neighbourX >= this.width) return -1;
        if (neighbourY < 0 || neighbourY >= this.height) return -1;

        // compute neighbour node number
        return cellNumber(neighbourX, neighbourY);
    }

    private int getDirectNeighbour(int cell, Direction direction) {
        return getDirectNeighbour(posX(cell), posY(cell), direction);
    }


    /**
     * Calculates the set of vertex numbers within the graph abstraction that are connected to the given fromVertex
     * A vertex is connected if it is different from the given fromVertex
     * and it is bounded by 0, 1 or 3 walls
     * and there exists a (pass-through) passage in the maze between fromVertex and the connected vertex along cells that all have exactly two walls
     * and that passage follows at most two different directions (such that manhattan distance can be calculated)
     */
    @Override
    public Set<Integer> getNeighbours(Integer fromVertex) {

        Set<Integer> neighbours = new HashSet<>();
        // try all initial directions from fromVertex
        for (Direction direction : Direction.values()) {
            int nextNeighbour = this.getDirectNeighbour(fromVertex, direction);
            if (nextNeighbour < 0 || this.getWall(fromVertex, direction))
                continue; // no passage, try the next direction available from fromVertex

            int neighbour, numWalls;
            do {
                // pass through to the next neighbour along direction
                neighbour = nextNeighbour;
                numWalls = this.getNumWalls(neighbour);
                nextNeighbour = this.getDirectNeighbour(neighbour, direction);
            } while (nextNeighbour >= 0 &&    // we have a further neighbour
                    !this.getWall(neighbour, direction) && // the passage continues
                    numWalls == NUM_DIRECTIONS - 2    // there is no junction or dead-end
            );
            // we've got a neighbour node with a straight passage along the iterated direction
            // try to continue further around a corner
            Direction turnedDirection;
            if (numWalls == NUM_DIRECTIONS - 2) {
                // try a perpendicular direction
                turnedDirection = Direction.values()[(direction.ordinal() + 1) % NUM_DIRECTIONS];
                if (this.getWall(neighbour, turnedDirection)) // a wall is blocking, try the other perpendicular direction
                    turnedDirection = Direction.values()[(direction.ordinal() + NUM_DIRECTIONS - 1) % NUM_DIRECTIONS];
                nextNeighbour = this.getDirectNeighbour(neighbour, turnedDirection);

                while (nextNeighbour >= 0 &&    // we have a further neighbour
                        !this.getWall(neighbour, turnedDirection) && // the passage continues
                        numWalls == NUM_DIRECTIONS - 2) { // there is no junction or dead-end
                    do {
                        // pass through to the next neighbour along turnedDirection
                        neighbour = nextNeighbour;
                        numWalls = this.getNumWalls(neighbour);
                        nextNeighbour = this.getDirectNeighbour(neighbour, turnedDirection);
                    }
                    while (nextNeighbour >= 0 && // we have a further neighbour
                            !this.getWall(neighbour, turnedDirection) && // the passage continues
                            numWalls == NUM_DIRECTIONS - 2);    // there is no junction or dead-end

                    // try again around a corner along the original direction
                    nextNeighbour = this.getDirectNeighbour(neighbour, direction);
                    while (nextNeighbour >= 0 && // we have a further neighbour
                            !this.getWall(neighbour, direction) && // the passage continues
                            numWalls == NUM_DIRECTIONS - 2) {    // there is no junction or dead-end
                        // pass through to the next neighbour along direction
                        neighbour = nextNeighbour;
                        numWalls = this.getNumWalls(neighbour);
                        nextNeighbour = this.getDirectNeighbour(neighbour, direction);
                    }
                    nextNeighbour = this.getDirectNeighbour(neighbour, turnedDirection);
                }
            }

            // add this final neighbour at the end of the passage to the set
            neighbours.add(neighbour);
        }

        return neighbours;
    }

    /**
     * calculates manhattan distance in the maze between two vertices in the graph
     * @param vertex1
     * @param vertex2
     * @return
     */
    public double manhattanDistance(Integer vertex1, Integer vertex2) {
        int horizontalDelta = posX(vertex1) - posX(vertex2);
        int verticalDelta = posY(vertex1) - posY(vertex2);
        return Math.abs(horizontalDelta) + Math.abs(verticalDelta);
    }

    /**
     * We model manhattanTime proportional to manhattanDistance
     * with some fixed 'thinking time' at the start of the passage
     * @param vertex1
     * @param vertex2
     * @return
     */
    public double manhattanTime(Integer vertex1, Integer vertex2) {
        return 1.0 + this.manhattanDistance(vertex1, vertex2);
    }

    public int getNumberOfCells() {
        return this.width * this.height;
    }

    /**
     * tries to connect a cell in Prims spanning tree to one of its direct neighbours
     * that are not part of the tree yet
     * Unvisited cells have all their walls set by initialisation
     * @param incompleteCell
     * @return
     */
    private int primTryOpenAWallOf(int incompleteCell) {

        // pick a random direction where we start with trying to remove a wall
        int firstDirectionIndex = randomizer.nextInt(NUM_DIRECTIONS);
        for (int d = 0; d < NUM_DIRECTIONS; d++) {
            // try the next direction for removing a wall
            Direction direction = Direction.values()[(firstDirectionIndex + d) % NUM_DIRECTIONS];

            // check if this direction has a wall
            if (!this.getWall(incompleteCell, direction)) continue;

            // find the neighbour
            int neighbour = this.getDirectNeighbour(incompleteCell, direction);

            // check whether a boundary has exceeded
            if (neighbour < 0) continue;

            // check whether the neighbour has not been visited before
            if (this.getNumWalls(neighbour) == NUM_DIRECTIONS) {
                // open the wall
                this.setWall(incompleteCell, direction, false);

                return neighbour;
            }
        }

        // no further expansion options here...
        return -1;
    }

    /**
     * Randomized Prim maze generator.
     * Populazes a rectangular grid with a random spanning tree, using all cells in the grid.
     * Hereafter there will be exactly one path of traversal between any two cells in the maze.
     * see https://en.wikipedia.org/wiki/Maze_generation_algorithm
     */
    public void generateRandomizedPrim() {

        // initialize all walls
        this.forAllCells(n -> {
            this.setWalls(n, true);
        });

        // track visited but incomplete cells
        // these are cells that are part of the maze, but may have unvisited neighbours
        int[] incompleteCells = new int[this.getNumberOfCells()];
        incompleteCells[0] = randomizer.nextInt(this.getNumberOfCells());
        int numIncompleteCells = 1;

        while (numIncompleteCells > 0) {
            // pick a random incomplete cell
            int incompleteCellIndex = randomizer.nextInt(numIncompleteCells);
            int incompleteCell = incompleteCells[incompleteCellIndex];

            // try to expand the incomplete cell to a neighbour
            int newNeighbour = this.primTryOpenAWallOf(incompleteCell);

            if (newNeighbour < 0) {
                // no further expansion opportunity, cell is complete, remove from the list
                numIncompleteCells--;
                incompleteCells[incompleteCellIndex] = incompleteCells[numIncompleteCells];
            } else {
                // add the new neighbour to the list of incomplete cells
                incompleteCells[numIncompleteCells] = newNeighbour;
                numIncompleteCells++;
            }
        }
    }

    /**
     * configures an entry at the top-left position of the maze
     * and an exit at the bottom-right position
     */
    public void configureTopEntry() {
        // set left-top entry
        this.setWall(0, 0, Direction.NORTH, false);
        this.startNode = cellNumber(0, 0);
        // set bottom-right exit
        this.setWall(this.width - 1, this.height - 1, Direction.SOUTH, false);
        this.exitNode = cellNumber(this.width - 1, this.height - 1);
    }

    /**
     * configures an entry node at the 33%,33% inner coordinate position
     * and an exit node at a random position along the west wall or north wall of the maze
     */
    public void configureInnerEntry() {
        // set central entry at one third
        this.startNode = cellNumber(this.width/3, this.height/3);
        // open all walls of the entry node
        this.setWalls(this.startNode,false);

        // calculate random north or west exit
        boolean choice = randomizer.nextBoolean();
        Direction exitDirection = choice ? Direction.NORTH: Direction.WEST;
        int exitX = choice ? randomizer.nextInt(this.width) : 0;
        int exitY = choice ? 0 : randomizer.nextInt(this.height);

        this.setWall(exitX, exitY, exitDirection, false);
        this.exitNode = cellNumber(exitX, exitY);
    }

    /**
     * tries to open a wall at the given cell and direction
     * if that can be done without leaving behind a 'dangling corner'
     * @param cell
     * @param direction
     * @param deltaX
     * @param deltaY
     * @return
     */
    private boolean tryOpenWallWithoutCreatingEmptyCorner(int cell, Direction direction, int deltaX, int deltaY) {
        int cellX = posX(cell);
        int cellY = posY(cell);
        // check if this direction has a wall
        if (this.getWall(cellX, cellY, direction)) {
            // find the neighbours
            int neighbour = this.getDirectNeighbour(cellX, cellY, direction);
            if (neighbour >= 0 &&
                    getNumCornerWalls(cellX, cellY) > 1 &&
                    getNumCornerWalls(cellX + deltaX, cellY + deltaY) > 1) {
                this.setWall(cell, direction, false);
                return true;
            }
        }
        return false;
    }

    /**
     * tries to open the North or West wall of the given cell
     * if that can be done without leaving behind a 'dangling corner'
     * @param cell
     * @return
     */
    private boolean tryOpenNORTHorWESTWallOf(int cell) {
        // ensure random order of trying north and west wall to prevent skew in maze
        if (randomizer.nextBoolean()) {
            if (!this.tryOpenWallWithoutCreatingEmptyCorner(cell, Direction.NORTH, 1, 0))
                return this.tryOpenWallWithoutCreatingEmptyCorner(cell, Direction.WEST, 0, 1);
            else
                return true;
        } else {
            if (!this.tryOpenWallWithoutCreatingEmptyCorner(cell, Direction.WEST, 0, 1))
                return this.tryOpenWallWithoutCreatingEmptyCorner(cell, Direction.NORTH, 1, 0);
            else
                return true;
        }
    }

    /**
     * tries to remove up to n walls at randomly selected cells and directions from the maze
     * if that can be done without leaving behind a 'dangling corner'
     * This may not be feasible if n is too high.
     * So we make only n^2 attempts...
     * @param n
     */
    public void removeRandomWalls(int n) {
        int maxAttempts = n * n;
        int wallsToOpen = n;
        while (wallsToOpen > 0 && maxAttempts > 0) {
            if (tryOpenNORTHorWESTWallOf(randomizer.nextInt(this.getNumberOfCells()))) {
                wallsToOpen--;
            }
            maxAttempts--;
        }
        if (wallsToOpen > 0) {
            System.out.printf("Exceeded maximum attempts to open a random wall: opened %d out of %d\n",
                    n - wallsToOpen, n);
        }
    }

    /**
     * generates a random maze with a given density of opened walls
     * walls will be removed only if that can be done without leaving behind a 'dangling corner'
     * the resulting maze may have unreachable sectors, may not be solvable and may have cycles.
     * @param passageFactor
     */
    public void generateRandomized(double passageFactor) {
        // initialize all walls
        this.forAllCells(n -> {
            this.setWalls(n, true);
        });

        this.removeRandomWalls((int) (passageFactor * ((this.width - 1) * this.height + this.width * (this.height - 1))));
    }

    private void forAllCells(Consumer<Integer> action) {
        for (int cell = 0; cell < this.getNumberOfCells(); cell++) {
            action.accept(cell);
        }
    }

    /**
     * shows the lay-out of the maze in character format at the console
     */
    public void print() {

        for (int y = 0; y < this.height; y++) {
            printHorizontal(y);
            for (int x = 0; x < this.width; x++) {
                System.out.print(this.westWalls[x][y] ? "|  " : "   ");
            }
            System.out.println(this.westWalls[this.width][y] ? "|" : " ");
        }
        printHorizontal(this.height);
    }

    private void printHorizontal(int y) {
        System.out.print("+");
        for (int x = 0; x < this.width; x++) {
            System.out.print(this.northWalls[x][y] ? "--+" : "  +");
        }
        System.out.println();
    }

    /**
     * produces an .svg file in the target classpath folder, which depicts the maze lay-out and the optional solution path
     * optionally also the vertex numbers of the cells are shown
     * and their colour indicates whether the cell has been visited or not.
     * .svg files can be viewed with a regular browser
     * @param resourceName  name of the file to be generated
     * @param path          optional search path with visited vertices to be coloured into the map
     */
    private static final double LINE_WIDTH = 0.1;
    private static final double FONT_SIZE = 0.3;
    private static final String PATH_COLOUR = "lime";
    private static final String VISITED_COLOUR = "red";
    private static final String TEXT_COLOUR = "blue";
    private static final String ENTRY_COLOUR = "orange";
    private static final String EXIT_COLOUR = "green";

    public void svgDrawMap(String resourceName, Set<Integer> vertices, GPath path) {
        try {
            //Path resources = Paths.get(this.getClass().getResource("/").getPath());
            //String svgPath = resources.toAbsolutePath() + "/" + resourceName;
            File svgFile = urlAsFile(getClass().getResource("/"), resourceName);
            PrintStream svgWriter = new PrintStream(svgFile);

            // header for an .svg file
            svgWriter.println("<?xml version='1.0' standalone='no'?>");
            // configure the viewBox to match the coordinate ranges of the Maze
            svgWriter.printf("<svg width='20cm' height='20cm' viewBox='%d %d %d %d' preserveAspectRatio='xMidYMid'\n",
                    -1, -1, this.width + 2, this.height + 2);
            svgWriter.println("     version='1.1' xmlns='http://www.w3.org/2000/svg'>");

            // draw the background colours of entry and exit nodes.
            this.svgDrawEntryAndExit(svgWriter);

            // draw the maze lay-out
            for (int y = 0; y < this.height; y++) {
                this.svgDrawHorizontal(svgWriter, y);
                for (int x = 0; x <= this.width; x++) {
                    if (this.westWalls[x][y])
                        svgWriter.printf(Locale.ENGLISH, "<line x1='%d' y1='%d' x2='%d' y2='%d' stroke-width='%.3f' stroke='%s'/>\n",
                                x, y, x, y + 1, LINE_WIDTH, "black");

                }
            }
            this.svgDrawHorizontal(svgWriter, this.height);

            // draw the solution path, if found
            this.svgDrawPath(svgWriter, path, PATH_COLOUR);

            // draw vertex numbers on top
            if (vertices != null) {
                for (int v : vertices) {
                    double posX = this.posX(v) + 0.5;
                    double posY = this.posY(v) + 0.5 + FONT_SIZE / 2;
                    String colour = (path != null && path.getVisited().contains(v) ? VISITED_COLOUR : TEXT_COLOUR);
                    svgWriter.printf(Locale.ENGLISH, "<text x='%.3f' y='%.3f' font-size='%.3f' fill='%s' text-anchor='middle'>%d</text>\n",
                            posX, posY, FONT_SIZE, colour, v);
                }
            }

            svgWriter.println("</svg>");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void svgDrawHorizontal(PrintStream svgWriter, int y) {
        for (int x = 0; x < this.width; x++) {
            if (this.northWalls[x][y])
                svgWriter.printf(Locale.ENGLISH, "<line x1='%d' y1='%d' x2='%d' y2='%d' stroke-width='%.3f' stroke='%s'/>\n",
                        x, y, x + 1, y, LINE_WIDTH, "black");
        }
    }

    /**
     * draws the startVertex and targetVertex of the maze in orange and green colour
     * @param svgWriter
     */
    private void svgDrawEntryAndExit(PrintStream svgWriter) {
        this.svgFillCell(svgWriter, posX(this.startNode), posY(this.startNode), ENTRY_COLOUR);
        this.svgFillCell(svgWriter, posX(this.exitNode), posY(this.exitNode), EXIT_COLOUR);
    }
    private void svgFillCell(PrintStream svgWriter, int x, int y, String colour) {
        svgWriter.printf(Locale.ENGLISH, "<rect x='%d' y='%d' width='1' height='1' fill-opacity='0.5' fill='%s'/>\n",
                x, y, colour);
    }

    /**
     * draws the solution path in the maze
     * @param svgWriter
     * @param path
     * @param colour
     */
    private void svgDrawPath(PrintStream svgWriter, GPath path, String colour) {
        if (path == null) return;

        double prevPosX = 0.0;
        double prevPosY = 0.0;
        for (Integer v : path.getVertices()) {
            double posX = this.posX(v) + 0.5;
            double posY = this.posY(v) + 0.5;
            if (prevPosX > 0 && prevPosY > 0)
                svgWriter.printf(Locale.ENGLISH, "<line x1='%.2f' y1='%.2f' x2='%.2f' y2='%.2f' stroke-width='%.3f' stroke='%s'/>\n",
                        prevPosX, prevPosY, posX, posY, 2 * LINE_WIDTH, colour);
            prevPosX = posX;
            prevPosY = posY;
        }
    }

    private static File urlAsFile(URL baseUrl, String resource) {
        try {
            URI uri = baseUrl.toURI();
            if (resource != null) uri = uri.resolve(resource);
            return new File(uri.getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI syntax error found on URL: " +
                    baseUrl.getPath() + (resource != null ? "/"+resource : ""));
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getStartNode() {
        return startNode;
    }

    public int getExitNode() {
        return exitNode;
    }
}
