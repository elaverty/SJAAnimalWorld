package brain;

import actor.AnimalBrain;
import java.util.Random;

/**
 * Darter just randomly darts in a random direction each move.  
 * (Not very effective.)  
 * @author spockm
 */
public class Darter extends AnimalBrain
{
    Random r = new Random();
    
    public Darter()
    {
        setName("DarterAnimal");
    }
    
    @Override
    public int chooseAction()
    {        
        int direction = r.nextInt(4)*90;
            direction += 1000; //DARTING!
        return direction;
    }
    
}