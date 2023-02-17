package brain;

import actor.AnimalBrain;
import actor.Prize;
import grid.Location;
import java.util.Random;

/**
 * Finder moves randomly, but if it is next to a Prize, it gets it.  
 * @author spockm
 */
public class Finder extends AnimalBrain
{
    Random randy = new Random();
    
    public Finder()
    {
        setName("FinderAnimal");
    }
    
    @Override
    public int chooseAction()
    {      
        //Check each direction for a Prize
        for(int dir=0; dir<360; dir+=90)
        {
            if(isPrizeInDirection(dir))
                return dir;
        }
        
        //Otherwise move randomly.
        int direction = randy.nextInt(4)*90;
        if(randy.nextInt(10)==9) 
            direction += 1000; //DARTING!
        
        return direction;
    }
    
    /**
     * This method determines if the Bot is adjacent to a Prize.  
     * @param direction - the direction to check (0-N, 90-E, 180-S, 270-W)
     * @return true if there is an adjacent Prize in that direction.  
     */
    private boolean isPrizeInDirection(int direction)
    {
        Location next = getLocation().getAdjacentLocation(direction);
        if(next.isValidLocation())
        {
            if(getArena()[next.getRow()][next.getCol()] instanceof Prize)
                return true;
        }
        return false;        
    }
    
    
}

    
