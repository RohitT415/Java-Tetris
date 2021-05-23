import java.awt.*;

/**
 * The main class for the Tetrad game.
 *
 * @author Rohit Tallapragada
 * @version Jan 23, 2021
 */
public class Tetrad
{
    private Block[] blockArray;
    private int random;
    private MyBoundedGrid<Block> grid;
    private final static Color [ ] COLORS = {Color.RED,     Color.GRAY,
                                             Color.CYAN,    Color.YELLOW,
                                             Color.MAGENTA, Color.BLUE,
                                             Color.GREEN,   Color.ORANGE};
    private final static int TETRAD_LENGTH = 4;
    
    /**
     * Recognizes if the current Tetrad is a bomb or not.
     * 
     * @return  false if not a bomb
     *          true if a bomb
     */
    public boolean amIABomb = false; 

    /**
     * Constructor for objects of class Tetrad.
     * 
     * @param grid1 the grid where this Tetrad is located
     */
    public Tetrad(MyBoundedGrid<Block> grid1)
    {
        grid = grid1;
        random = (int)(Math.random() * COLORS.length);
        blockArray = new Block[TETRAD_LENGTH];
        for(int i = 0; i < blockArray.length; i++)
        {
            blockArray[i] = new Block();
            blockArray[i].setColor(COLORS[random]);
        }
        Location[] locs = createLocationArray(random);
        addToLocations(grid1, locs);
    }

    /**
     * Adds a Tetrad to a location.
     * 
     * @param grid1 the grid where this Tetrad is located
     * @param locs1 the array of the block locations of the Tetrad
     */
    private void addToLocations(MyBoundedGrid<Block> grid1, 
    Location[] locs1)
    {
        for(int i = 0; i < locs1.length; i++)
        {
            blockArray[i].putSelfInGrid(grid1, locs1[i]);
        }
    }

    /**
     * Creates an array of locations for blocks to form Tetrads.
     * 
     * @param shape the number that determines the Tetrad
     */
    private Location[] createLocationArray(int shape)
    {
        int mid = grid.getNumCols()/2;
        Location[ ] locs = new Location[TETRAD_LENGTH];
        if(shape == 0)
        {
            //I
            locs[1] = new Location(0,mid);
            locs[0] = new Location(1,mid);  //pivot location in loc0
            locs[2] = new Location(2,mid);
            locs[3] = new Location(3,mid);
        }
        else if (shape == 1)
        {
            //T
            locs[1] = new Location(0,mid - 1);
            locs[0] = new Location(0,mid); //pivot location in loc0
            locs[2] = new Location(0,mid + 1);
            locs[3] = new Location(1,mid);
        }
        else if (shape == 2)
        {
            //O
            locs[0] = new Location(0,mid - 1);
            locs[1] = new Location(0,mid);
            locs[2] = new Location(1,mid - 1);
            locs[3] = new Location(1,mid);
        }
        else if (shape == 3)
        {
            //L
            locs[1] = new Location(0,mid);
            locs[0] = new Location(1,mid); //pivot location in loc0
            locs[2] = new Location(2,mid);
            locs[3] = new Location(2,mid + 1);
        }
        else if (shape == 4)
        {
            //J
            locs[1] = new Location(0,mid);
            locs[0] = new Location(1,mid); //pivot location in loc0
            locs[2] = new Location(2,mid);
            locs[3] = new Location(2,mid - 1);
        }
        else if (shape == 5)
        {
            //S
            locs[1] = new Location(0,mid);
            locs[0] = new Location(0,mid - 1); //pivot location in loc0
            locs[2] = new Location(1,mid - 1);
            locs[3] = new Location(1,mid - 2);
        }
        else if (shape == 6)
        {
            //Z
            locs[1] = new Location(0,mid);
            locs[0] = new Location(0,mid + 1); //pivot location in loc0
            locs[2] = new Location(1,mid + 1);
            locs[3] = new Location(1,mid + 2);
        }

        else if (shape == 7)
        {
            //BOMB
            locs[0] = new Location(0,mid - 1);
            locs[1] = new Location(0,mid);
            locs[2] = new Location(1,mid - 1);
            locs[3] = new Location(1,mid);
            amIABomb = true;
        }

        return locs;
    }

    /**
     * Removes Tetrad blocks.
     * 
     * @precondition    Tetrad blocks are on the grid
     */
    private Location[] removeBlocks()
    {
        //if the grid can be interacted with, execute the rest of the method
        //if(grid != null)
        //{
        //loops through contents of locs and removes each element from the grid
        Location [] locs = new Location [TETRAD_LENGTH];
        for(int i = 0; i < blockArray.length; i++)
        {
            locs[i] = blockArray[i].getLocation();
            blockArray[i].removeSelfFromGrid();
        } 
        //}
        return locs;
    }

    /**
     * Checks if an area is empty on the grid.
     */
    private boolean areEmpty(MyBoundedGrid<Block> grid2,
    Location[] locs2)
    {
        //loops through contents of locs2
        for(int i = 0; i < locs2.length; i++)
        {
            //if the position is empty and valid, then it is "empty"
            if(!grid2.isValid(locs2[i]) || (grid2.get(locs2[i]) != null))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Translates a Tetrad to a new location.
     * 
     * @param deltaRow the number of rows the tetrad moves
     * @param deltaCol the number of columns the tetrad moves
     * @return true when the new location is valid
     *         false otherwise
     */
    public boolean translate(int deltaRow, int deltaCol)
    {
        Location[] newLocationArray = new Location[TETRAD_LENGTH];

        //remove the tetrad
        Location[] oldLocationArray = removeBlocks();

        //update the location array with delta row and delta column
        for(int i = 0; i < newLocationArray.length; i++)
        {
            newLocationArray[i] = new Location((oldLocationArray[i].getRow()) + deltaRow,
                (oldLocationArray[i].getCol()) + deltaCol);
        }

        // if the new locations are empty then put the tetrad in the new location
        if (areEmpty(grid, newLocationArray))
        {
            addToLocations(grid, newLocationArray);
            return true;
        }
        addToLocations(grid, oldLocationArray);
        return false;
    }

    /**
     * Rotates a Tetrad.
     * 
     * @return true when the new location is valid
     *         false otherwise
     */
    public boolean rotate()
    {
        Location[] newLocationArray = new Location[TETRAD_LENGTH];
        int pivotRow;
        int pivotColumn;

        //remove the tetrad
        Location[] oldLocationArray; 

        //find pivotRow/Column

        pivotRow = blockArray[0].getLocation().getRow();
        pivotColumn = blockArray[0].getLocation().getCol();

        //update the location array with pivot row and pivot column
        for(int i = 0; i < newLocationArray.length; i++)
        {
            //newLocationArray[i] = new Location((oldLocationArray[i].getRow()) + pivotRow,
            //(oldLocationArray[i].getCol()) + pivotColumn);

            newLocationArray[i] = new Location(
                (pivotRow - pivotColumn + blockArray[i].getLocation().getCol()), 
                (pivotRow + pivotColumn - blockArray[i].getLocation().getRow()));
        }

        oldLocationArray = removeBlocks();

        // if the new locations are empty then put the tetrad in the new location
        if (areEmpty(grid, newLocationArray))
        {
            addToLocations(grid, newLocationArray);
            return true;
        }
        addToLocations(grid, oldLocationArray);
        return false;
    }
    
    /**
     * Gets the lowest row of the current Tetrad.
     * 
     * @return  the location of the current Tetrad
     */
    public int getRow()
    {
        return (blockArray[3].getLocation().getRow());
    }
}

