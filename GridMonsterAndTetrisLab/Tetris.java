import java.awt.*;

/**
 * The main class for the Tetris game.
 *
 * @author Rohit Tallapragada
 * @version Jan 23, 2021
 */
public class Tetris implements ArrowListener
{
    private MyBoundedGrid<Block> grid;
    private BlockDisplay display;
    private Tetrad currentTetrad;
    private InterruptedException e = new InterruptedException();

    /**
     * Constructor for objects of class Tetris
     */
    public Tetris()
    {
        grid = new MyBoundedGrid<Block>(20, 10); 
        display = new BlockDisplay(grid);
        display.setArrowListener(this);
        display.setTitle("Rohit's Tetris");
        currentTetrad = new Tetrad(grid);
        display.showBlocks();
    }

    /**
     * Responses to the up arrow.
     */
    public void upPressed()
    {
        if(currentTetrad.rotate())
            display.showBlocks();
    }

    /**
     * Responses to the down arrow.
     */
    public void downPressed()
    {
        if(currentTetrad.translate(1, 0))
            display.showBlocks();
    }

    /**
     * Responses to the left arrow.
     */
    public void leftPressed()
    {
        if(currentTetrad.translate(0, -1))
            display.showBlocks();
    }

    /**
     * Responses to the right arrow.
     */
    public void rightPressed()
    {
        if(currentTetrad.translate(0, 1))
            display.showBlocks();
    }

    /**
     * Responses to the space.
     */
    public void spacePressed()
    {
        while(currentTetrad.translate(1, 0))
        {
            display.showBlocks();
        }
    }

    /**
     * Checks if the game is over by seeing if any blocks have stacked 
     * to reach the Tetrad spawning area.
     * 
     * @return true if the game is over
     *         false if the game is not over
     */
    public boolean isGameOver()
    {
        if ( currentTetrad.amIABomb)
        {
            return false;
        }
        for (int colIndex = 0; colIndex < grid.getNumCols(); colIndex++)
        {
            if (grid.get(new Location(4, colIndex)) != null )
                return true;
        }
        return false;
    }

    /**
     * Executes the game.
     */
    public void play()
    {
        try 
        {
            Thread.sleep(1000);
            while(currentTetrad.translate(1, 0))
            {
                Thread.sleep(1000);    
                display.showBlocks();
            }
            //If the tetrad is a bomb clear just the row even if it is not completed
            if (currentTetrad.amIABomb)
            {
                clearRow(currentTetrad.getRow());
            }
            else
            {
                clearCompletedRows();
            }
            if(!isGameOver())
            {
                currentTetrad = new Tetrad(grid);
            }
            else
            {
                throw e;
            }

            display.showBlocks();
        }
        catch(InterruptedException e)
        {
            display.showBlocks();
            System.out.print("You lost. ");
            System.out.print("Make sure that no blocks reach the Tetrad spawning area!");
        }
    }

    /**
     * Checks if a row is completed.
     * 
     * @param row the row that you want to checked.
     */
    private boolean isCompletedRow(int row)
    {
        for (int columnIndex = 0; columnIndex < grid.getNumCols(); columnIndex++)
        {
            if (grid.get(new Location(row, columnIndex)) == null)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Clears the current row.
     * 
     * @param row the row that you want to be cleared.
     */
    private void clearRow(int row)
    {
        for(int columnIndex = 0; columnIndex < grid.getNumCols(); columnIndex++)
        {
            Location loc = new Location (row, columnIndex);
            if (grid.get(loc) != null)
            {
                grid.get(loc).removeSelfFromGrid();
            }
            for(int rowIndex = row - 1; rowIndex >= 0; rowIndex--)
            {
                Location currentLoc = new Location(rowIndex, columnIndex);
                Block dropping = grid.get(currentLoc);
                if(dropping != null)
                {
                    dropping.moveTo(new Location(rowIndex + 1, columnIndex));
                }
            }
        }
    }

    /**
     * Clears completed rows.
     */
    private void clearCompletedRows()
    {
        int rowIndex = grid.getNumRows() - 1;

        while(rowIndex >= 0)
        {
            if ((isCompletedRow(rowIndex)))
            {
                clearRow(rowIndex);
            }
            else
            {
                rowIndex--;
            }
        }

    }

    /**
     * Runs the Tetris game.
     * 
     * @param args  user's information from the command line
     */
    public static void main (String[] args)
    {
        Tetris runner = new Tetris();

        System.out.print("Make sure that no blocks");
        System.out.print(" reach the Tetrad spawning area");
        System.out.println(" the 5th row from the top) or you lose!");
        System.out.println("Orange blocks are 'BOMB' blocks.");

        while(!runner.isGameOver())
        {
            runner.play();
        }
    }
}
