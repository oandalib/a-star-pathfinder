import java.util.Objects;
import lombok.Data;

@Data
public class Cell implements Comparable {
    private static final int GRID_SIZE = 20;
    private int x;
    private int y;
    private Cell cameFrom;
    private int gScore;
    private int hScore;
    // fScore is just sum of gScore + hScore, no need for a variable

    Cell(int position, Cell cameFrom, int gScore) {
        this.x = position % GRID_SIZE;
        this.y = position / GRID_SIZE;
        this.cameFrom = cameFrom;
        this.gScore = gScore;
    }

    Cell(int position, Cell cameFrom) {
        this(position, cameFrom, Integer.MAX_VALUE);
    }

    // starting cell
    Cell(int position) {
        this(position, null, 0);
    }

    public double getFScore() {
        return (gScore + hScore);
    }

    public int getPositionId() {
        return ((this.y * GRID_SIZE) + this.x);
    }

    // using Manhattan method
    public void setHScore(Cell endCell) {
        this.hScore = Math.abs(this.getX() - endCell.getX()) + Math.abs(this.getY() - endCell.getY());
    }

    @Override
    public int compareTo(Object object) {
        Cell otherCell = (Cell) object;
        return (int)(this.getFScore() - otherCell.getFScore());
    }

    @Override
    public boolean equals(Object object) {
        Cell cell = (Cell) object;
        return ( (this.x == cell.getX()) && (this.y == cell.getY()) ) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
}
