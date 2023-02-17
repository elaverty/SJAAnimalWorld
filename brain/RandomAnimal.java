package brain;

import actor.AnimalBrain;

import java.awt.Color;
import java.util.Random;
/**
 * @author Laverty
 * 
 * RandomAnimal chooses a random move each turn.
 */
public class RandomAnimal extends AnimalBrain
{
    Random r = new Random();
    
    public RandomAnimal()
    {
        setName("RandomAnimal");
        setPreferredColor(Color.ORANGE);
    }
    
    @Override
    public int chooseAction()
    {        
        int direction = r.nextInt(4)*90;
       
        return direction;
    }
    

    public void initForRound()
    {
    	
    	
    }
    
}
