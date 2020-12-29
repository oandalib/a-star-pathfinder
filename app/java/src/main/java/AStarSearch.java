import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;

public class AStarSearch {
    private static final MySingletonConnection myConnection = MySingletonConnection.getInstance();
    private static final int GRID_SIZE = 20;
    private Cell startCell;
    private Cell endCell;
    private HashSet<Cell> barriers;

    private PriorityQueue<Cell> openSet;
    private PriorityQueue<Cell> closedSet;

    AStarSearch(int startPosition, int endPosition, int[] barriers) {
        startCell = new Cell(startPosition);
        endCell = new Cell(endPosition, null, Integer.MAX_VALUE);

        startCell.setHScore(endCell);

        openSet = new PriorityQueue<Cell>();
        closedSet = new PriorityQueue<Cell>();

        this.barriers = new HashSet<Cell>();
        for (int i = 0; i < barriers.length; i++) {
            this.barriers.add(new Cell(barriers[i]));
        }
        openSet.add(startCell);
    }

    public void findPath() {
        if (openSet.isEmpty()) {
            return;
        }

        try {
            while (!openSet.isEmpty()) {
                Cell cell = openSet.remove();
                if ( (!cell.equals(startCell)) && (!cell.equals(endCell)) ) {
                    myConnection.sendMessage("r" + cell.getPositionId());
                }

                closedSet.add(cell);
                if (cell.equals(endCell)) {
                    // we found the path
                    Cell previous = cell.getCameFrom();
                    if (!cell.equals(startCell)) {
                        myConnection.sendMessage("p" + previous.getPositionId());
                    }

                    while (previous.getCameFrom() != null) {
                        previous = previous.getCameFrom();
                        if (!previous.equals(startCell)) {
                            myConnection.sendMessage("p" + previous.getPositionId());
                        }
                    }
                    return;
                }
                addNeighborsToOpenSet(cell);
            }
        } finally {
            myConnection.sendMessage("end");
            MySingletonConnection.closeConnection();
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
                if (barriers.contains(new Cell(checkBarrier))) {
                    continue;
                }

                int xPosition = cell.getX() + x;
                int yPosition = cell.getY() + y;
                int position = (yPosition * GRID_SIZE) + xPosition;

                Cell neighbor = new Cell(position, cell, cell.getGScore() + 1);
                if (openSet.contains(neighbor) || closedSet.contains(neighbor)) {
                    continue;
                }
                neighbor.setHScore(endCell);
                openSet.add(neighbor);
                if (!neighbor.equals(endCell)) {
                    myConnection.sendMessage("g" + neighbor.getPositionId());
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            int startPosition = Integer.parseInt(args[0]);
            int endPosition = Integer.parseInt(args[1]);
            int[] numbers = {};
            if (args.length == 3) {
                numbers = Arrays.stream(args[2].split(",")).mapToInt(Integer::parseInt).toArray();
            }

            AStarSearch app = new AStarSearch(startPosition, endPosition, numbers);
            app.findPath();
        }  catch (Exception e) {
            System.out.println(e);
        }
    }
}
