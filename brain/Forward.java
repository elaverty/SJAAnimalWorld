package brain;

import actor.AnimalBrain;
import java.util.Random;

/**
 * Forward always goes forward the same direction as last time if possible.  
 * @author spockm
 */
public class Forward extends AnimalBrain
{
    Random r = new Random();
    int lastDirection = NORTH;
    
    public Forward()
    {
        setName("ForwardAnimal");
    }
    
    @Override
    public int chooseAction()
    {   // Move forward if you can.  If you can't, move right.     
        if(!canMove(lastDirection))
            lastDirection = r.nextInt(4)*90;
        
        return lastDirection;
    }
    
}