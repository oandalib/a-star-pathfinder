import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;

public class AStarSearch {
    private static final MySingletonConnection myConnection = MySingletonConnection.getInstance();
    private static final int GRID_SIZE = 20;
    private final String QUEUE_NAME;
    private final Cell START_CELL;
    private final Cell END_CELL;
    private final HashSet<Cell> BARRIERS;

    private PriorityQueue<Cell> openSet;
    private PriorityQueue<Cell> closedSet;

    AStarSearch(int startPosition, int endPosition, String queueCounter, int[] barriers) {
        QUEUE_NAME = "queue" + queueCounter;
        START_CELL = new Cell(startPosition);
        END_CELL = new Cell(endPosition, null, Integer.MAX_VALUE);

        if ( !myConnection.generateQueue(QUEUE_NAME) ) {
            System.out.println("Failed to create queue.");
            System.exit(-1);
        }

        START_CELL.setHScore(END_CELL);

        openSet = new PriorityQueue<Cell>();
        closedSet = new PriorityQueue<Cell>();

        this.BARRIERS = new HashSet<Cell>();
        for (int i = 0; i < barriers.length; i++) {
            this.BARRIERS.add(new Cell(barriers[i]));
        }
        openSet.add(START_CELL);
    }

    public void findPath() {
        if (openSet.isEmpty()) {
            return;
        }

        try {
            while (!openSet.isEmpty()) {
                Cell cell = openSet.remove();
                if ( (!cell.equals(START_CELL)) && (!cell.equals(END_CELL)) ) {
                    myConnection.sendMessage("r" + cell.getPositionId(), QUEUE_NAME);
                }

                closedSet.add(cell);
                if (cell.equals(END_CELL)) {
                    // we found the path
                    Cell previous = cell.getCameFrom();
                    if (!cell.equals(START_CELL)) {
                        myConnection.sendMessage("p" + previous.getPositionId(), QUEUE_NAME);
                    }

                    while (previous.getCameFrom() != null) {
                        previous = previous.getCameFrom();
                        if (!previous.equals(START_CELL)) {
                            myConnection.sendMessage("p" + previous.getPositionId(), QUEUE_NAME);
                        }
                    }
                    return;
                }
                addNeighborsToOpenSet(cell);
            }
        } finally {
            myConnection.sendMessage("end", QUEUE_NAME);
            myConnection.closeConnection(QUEUE_NAME);
        }
    }

    private void addNeighborsToOpenSet(Cell cell) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                // prevent move on itself
                if (x == 0 && y == 0) {
                    continue;
                }
                // prevent diagonal moves
                if ( (x == -1 && y == -1) || (x == -1 && y == 1) || (x == 1 && y == -1) || (x == 1 && y == 1) ){
                    continue;
                }
                // prevent out of grid
                if ( (cell.getX() + x < 0) || (cell.getX() + x > GRID_SIZE - 1) ||
                     (cell.getY() + y < 0  || (cell.getY() + y > GRID_SIZE -1 ))) {
                    continue;
                }
                int checkBarrier = ((cell.getY() + y) * GRID_SIZE) + cell.getX() + x;
                // prevent moves on barriers
                if (BARRIERS.contains(new Cell(checkBarrier))) {
                    continue;
                }

                int xPosition = cell.getX() + x;
                int yPosition = cell.getY() + y;
                int position = (yPosition * GRID_SIZE) + xPosition;

                Cell neighbor = new Cell(position, cell, cell.getGScore() + 1);
                if (openSet.contains(neighbor) || closedSet.contains(neighbor)) {
                    continue;
                }
                neighbor.setHScore(END_CELL);
                openSet.add(neighbor);
                if (!neighbor.equals(END_CELL)) {
                    myConnection.sendMessage("g" + neighbor.getPositionId(), QUEUE_NAME);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            int startPosition = Integer.parseInt(args[0]);
            int endPosition = Integer.parseInt(args[1]);
            String queueCounter = args[2];

            int[] barriers = {};
            if (args.length == 4) {
                barriers = Arrays.stream(args[3].split(",")).mapToInt(Integer::parseInt).toArray();
            }

            AStarSearch app = new AStarSearch(startPosition, endPosition, queueCounter, barriers);
            app.findPath();
        }  catch (Exception e) {
            System.out.println(e);
        }
    }
}
