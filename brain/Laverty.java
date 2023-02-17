package brain;

import actor.AnimalBrain;
import java.util.Random;
import java.util.ArrayList;

/**
 * Forward always goes forward the same direction as last time if possible.  
 * @author spockm
 */
public class Laverty extends AnimalBrain
{
    private Random r = new Random();
    private int lastDirection = NORTH;
		private Location bigPrizel;
		private int [][] arena;
		private Prize p;
		private ArrayList prizes;
    
    public Laverty()
    {
        setName("Laverty");
    }
    
    @Override
    public int chooseAction()
    {   
			
   
			getPrizes();
			chooseDirection();
			killTime();
			
			
		
        return NORTH;
    
			}

		public void initForRound()
    {
    	
    	
    }

	private void getPrizes()
	{
		 arena = getArena();
			for (int i = 0; i<21; i++)
				for (int j = 0; j<21; j++)
					 if(arena[i][j].isValidLocation())
        {
            if(arena[i][j] instanceof Prize)
						{
							p = (Prize)arena[i][j];
							prizes.add(p);
						}
							if (p.getValue()==500)
								System.out.println("I found the prize");

					bigPrize = new Location(i, j);

					
					}
	}
    
}