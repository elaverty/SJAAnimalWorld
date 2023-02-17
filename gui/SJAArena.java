package gui;

import actor.Block;
import actor.GameObject;
import actor.Prize;
import grid.Location;
import grid.SJAGrid;
import java.util.ArrayList;
import java.util.Random;
import world.SJAWorld;
import world.World;

/**
 * The Arena class includes all of the methods needed to setup the arena 
 * according to the rules of the game.  
 * @author Spock
 * updated Spring 2021 - Liz Laverty
 */
public class SJAArena 
{
    /**
     * The size of a side of the central starting room in the arena. 
     */
    private Random randy = new Random();
    private boolean withBlocks = true;
    
    public static final int CHALLENGE_1 = 1; //1 prize - no blocks
    public static final int CHALLENGE_2 = 2; //plus sign blocks
    public static final int CHALLENGE_3 = 3; //normal
    
    
    private int playMode = CHALLENGE_3; 
    
    
    public void setPlayMode(int in) 
    { 
        playMode = in;
        if(playMode < 3) withBlocks = false;
        else withBlocks = true;
        
    }
    public int getPlayMode()
    {
        return playMode;
    }
    
    /**
     * Toggles whether the grid will include Blocks or not.  
     * This is an option in the Arena menu.
     */
    public void toggleShowBlocks(World<?> world) 
    { 
        withBlocks = ! withBlocks; 
    }

    /**
     * Initializes the Arena based on the selected rules.  
     * @param world the world that the Arena is within
     */
    public void initializeArena(World<GameObject> world)
    {
        if(withBlocks)
            addStandardBlocks(world);
        if (playMode==2)
        	addPlusSignBlocks(world);
        addRandomSuperPrize(world);
        
    
        
        	
    }
    
    private void addStandardBlocks(World<GameObject> world)
    {
        SJAGrid<GameObject> grid = (SJAGrid<GameObject>)world.getGrid();
        int rows = grid.getNumRows();
        int cols = grid.getNumCols();
        int centerRow = rows/2; 
        int centerCol = cols/2;

        //One Block in the center
        new Block(1000).putSelfInGrid(grid, new Location(centerRow,centerCol));
        
        //Containers
        new Block(1000).putSelfInGrid(grid, new Location(3,centerCol));
        new Block(1000).putSelfInGrid(grid, new Location(rows-4,centerCol));
        new Block(1000).putSelfInGrid(grid, new Location(centerRow,3));
        new Block(1000).putSelfInGrid(grid, new Location(centerRow,cols-4));
        for(int off=0; off<4;off++)
        {
            //Top
            new Block(1000).putSelfInGrid(grid, new Location(off,centerCol-2));
            new Block(1000).putSelfInGrid(grid, new Location(off,centerCol+2));
            //Bottom
            new Block(1000).putSelfInGrid(grid, new Location(rows-off-1,centerCol-2));
            new Block(1000).putSelfInGrid(grid, new Location(rows-off-1,centerCol+2));
            //Left
            new Block(1000).putSelfInGrid(grid, new Location(centerRow-2,off));
            new Block(1000).putSelfInGrid(grid, new Location(centerRow+2,off));
            //Right
            new Block(1000).putSelfInGrid(grid, new Location(centerRow-2,cols-off-1));
            new Block(1000).putSelfInGrid(grid, new Location(centerRow+2,cols-off-1));
        }
    }
    
    private void addPlusSignBlocks(World<GameObject> world)
    {
        SJAGrid<GameObject> grid = (SJAGrid<GameObject>)world.getGrid();
        int rows = grid.getNumRows();
        int cols = grid.getNumCols();
        int centerRow = rows/2; 
        int centerCol = cols/2;

        //One Block in the center
        new Block(1000).putSelfInGrid(grid, new Location(centerRow,centerCol));
        
        //five to the left
        for (int i=1; i<6; i++)
        new Block(1000).putSelfInGrid(grid, new Location(centerRow,centerCol-i));
        for (int i=1; i<6; i++)
        new Block(1000).putSelfInGrid(grid, new Location(centerRow,centerCol+i));
        for (int i=1; i<6; i++)
        new Block(1000).putSelfInGrid(grid, new Location(centerRow-i,centerCol));
        for (int i=1; i<6; i++)
        new Block(1000).putSelfInGrid(grid, new Location(centerRow+i,centerCol));
      
    }
    
    
    private void addRandomSuperPrize(World<GameObject> world)
    {
        SJAWorld rbw = (SJAWorld)world;
        ArrayList<Location> places = rbw.getSuperPrizeLocations();
        int choice = randy.nextInt(places.size());
        Prize awesome = new Prize(Prize.SUPER_PRIZE_TYPE,Prize.SUPER_PRIZE_VALUE);
        awesome.putSelfInGrid(world.getGrid(), places.get(choice));
        
    }
    
   
    
}
