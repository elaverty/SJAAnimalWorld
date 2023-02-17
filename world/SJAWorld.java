
package world;

import actor.Prize;
import actor.Animal;
import actor.AnimalBrain;
import actor.GameObject;
import actor.Tail;
import grid.Grid;
import grid.Location;
import grid.SJAGrid;
import gui.SJAArena;
import java.util.ArrayList;
import java.util.Random;

/**
 * A SJAWorld is full of AnimalActors used in the game SJAWorld.  
 * @author Spock
 * updated Spring 2021 - Liz Laverty
 */
public class SJAWorld extends ActorWorld
{
    /**
     * The number of moves in a round of SJAAnimals competition.
     */
    public static final int NUM_MOVES_IN_ROUND = 500;
    /**
     * The number of rounds in a match of SJAAnimals competition.  
     */
    public static final int NUM_ROUNDS_IN_MATCH = 100;

    private String default_message = "SJAAnimals is awesome.";
    private static Random randy = new Random();

    private static int moveNum = 1;
    private static int roundNum = 1;
    
    private boolean roundRobin = false;
    private int rr1=0; 
    private int rr2=0;
    private boolean matchReady = false;    
    
    
    private SJAArena arena = new SJAArena();
    
    private ArrayList<Animal> animalsInMaze = new ArrayList<Animal>();
    private ArrayList<Animal> allAnimals = new ArrayList<Animal>();
    
    /**
     * Constructs a AnimalBrain world with a default grid.
     */
    public SJAWorld()
    {
        initializeGridForRound();
        initializeMatch();
    }

    /**
     * Constructs a AnimalBrain world with a given grid.
     * @param grid the grid for this world.
     */
    public SJAWorld(Grid<GameObject> grid)
    {
        super(grid);
        initializeGridForRound();
        initializeMatch();
    }

    /**
     * gets the Arena used in this World.
     * @return the Arena
     */
    public SJAArena getArena() { return arena; }
    /**
     * Gets the current move number in the round being played.  
     * @return the move number in the current round.
     */
    public static int getMoveNum() { return moveNum; }
    /**
     * Gets the current round number in the match being played.  
     * @return the round number in the current match.
     */
    public static int getRoundNum() { return roundNum; }
    
    public void startRoundRobin()
    {
        if(allAnimals.size() > 0)
        {
            roundRobin = true;
            System.out.println("Beginning Round-Robin for "+allAnimals.size()+" Animals.");
            initializeMatch();
        }
    }
    
    
    public void initializeMatch()
    {
        moveNum = 1;
        roundNum = 1;
        matchReady = true;
        
        if(roundRobin)
        {
            initializeRoundRobinMatch();
        }
        initializeGridForRound();
    }
    
    private void initializeRoundRobinMatch()
    {
        if(animalsInMaze.size() > 1)
        {
            System.out.println(animalsInMaze.get(0).getAnimal().getName()+","+ animalsInMaze.get(0).getRoundsWon()
                    +","+  animalsInMaze.get(1).getAnimal().getName()+","+ animalsInMaze.get(1).getRoundsWon() );//                        +"      time(sec)="+(int)((System.currentTimeMillis() - matchstart)/1000));
            if(animalsInMaze.get(0).getRoundsWon() + animalsInMaze.get(1).getRoundsWon()<100)
                System.out.println("Incomplete match...???");
            //score match
            if(animalsInMaze.get(0).getRoundsWon()>animalsInMaze.get(1).getRoundsWon())
            {
                animalsInMaze.get(0).increaseMatchesWon();
                animalsInMaze.get(1).increaseMatchesLost();
            }
            if(animalsInMaze.get(0).getRoundsWon()==animalsInMaze.get(1).getRoundsWon())
            {
                animalsInMaze.get(0).increaseMatchesTied();
                animalsInMaze.get(1).increaseMatchesTied();
            }
            if(animalsInMaze.get(0).getRoundsWon()<animalsInMaze.get(1).getRoundsWon())
            {
                animalsInMaze.get(1).increaseMatchesWon();
                animalsInMaze.get(0).increaseMatchesLost();
            }
            animalsInMaze.get(0).setRoundsWon(0);
            animalsInMaze.get(1).setRoundsWon(0);

        }
        animalsInMaze.clear(); 
        rr2++;
        if(rr2==allAnimals.size())
        {
            rr1++;
            rr2=rr1+1;
            if(rr1==allAnimals.size()-1)
            {
                for(Animal x : allAnimals)
                {
                    System.out.println(x.getAnimal().getName()+
                            ",  TP=,"+x.getTotalScore() +
                            ",  w=,"+x.getMatchesWon()+
                            ",  t=,"+x.getMatchesTied()+
                            ",  l=,"+x.getMatchesLost()                              
                            );
                }
                System.out.println("TOURNEY COMPLETE");
                roundRobin = false;
                return;
            }
        }
        animalsInMaze.add(allAnimals.get(rr1));
        animalsInMaze.add(allAnimals.get(rr2));
        
    }
    
    public void clearAllAnimals()
    {
        animalsInMaze.clear();
        initializeGridForRound();
    }
    
    public void zeroScoreAllAnimals()
    {
        for(Animal b : animalsInMaze)
        {
            b.clearScores();
        }
    }
    /**
     * Initialize the arena and each of the Animals for a round of competition.
     */
    public final void initializeGridForRound()
    {
        clearAllObjectsFromGrid();
        arena.initializeArena(this);
        for(Animal r : animalsInMaze) 
        {
            r.initialize();
            r.putSelfInGrid(getGrid(), getRandomEmptyCenterLocation());
            r.setDirection(getRandomDirection());
        }
        
        moveNum = 1; 
    }
    /**
     * Clears the Arena in preparation of starting a new round. 
     * @return an ArrayList of the Animals in the arena.
     */
    public void clearAllObjectsFromGrid()
    {
        SJAGrid<GameObject> gr = (SJAGrid<GameObject>)this.getGrid();
        for(int x=0; x<gr.getNumCols(); x++)
        {
            for(int y=0; y<gr.getNumRows(); y++)
            {
                Location loc = new Location(y,x);
                GameObject a = gr.get(loc);
                if(a != null)
                {
                    a.removeSelfFromGrid();
                }
            }
        }
    }
    /**
     * Scores the results from a round of competition.
     */
    public void scoreRound()
    {
        
        int max = calcMaxScore();
        for(Animal r : animalsInMaze)
        {
            if(r.getScore() == max)
                r.increaseRoundsWon();
        }
        roundNum++;
        setMessage("Starting round #"+roundNum);
    }
    public int calcMaxScore() 
    {
        int maxScore = -5000;
        for(Animal r : animalsInMaze)
        {
            if(r.getScore() > maxScore)
                maxScore = r.getScore();
        }
        return maxScore;
    }

    
    //inheirits javadoc comment from world.
    @Override
    public void show()
    {
        if (getMessage() == null)
            setMessage(default_message);
        super.show();
    }

    //inheirits javadoc comment from world.
    @Override
    public void step()
    {
        Grid<GameObject> gr = getGrid();
        
        //This only applies for the FIRST call to this method.  ?????
        if(!matchReady)
            initializeMatch();
        
        //Get all the Actors in the Grid----------------------
        ArrayList<GameObject> actors = new ArrayList<GameObject>();
        // Look at all grid locations.
        for (int r = 0; r < gr.getNumRows(); r++)
        {
            for (int c = 0; c < gr.getNumCols(); c++)
            {
                // If there's an object at this location, put it in the array.
                Location loc = new Location(r, c);
                if (gr.get(loc) != null) 
                    actors.add(gr.get(loc));
            }
        }
        
        //--------------Shuffle their order----------------------
        if(actors.size() > 1)
        {
            //shuffle their order for acting.
            for(int z=0;z<actors.size()*2;z++)
            {
                //Pick a random one.
                int from = randy.nextInt(actors.size());
                //Swap it to the front.
                GameObject a = actors.get(from);
                GameObject b = actors.get(0);
                actors.set(from,b);
                actors.set(0,a);              
            }
        }

        //-------------Have them each act()--------------------------
        for (GameObject a : actors)
        {
            // only act if another actor hasn't removed a
            if (a.getGrid() == gr)
                a.act();
        }
        if(((SJAGrid<GameObject>)gr).isMessageWaiting())
            setMessage(((SJAGrid<GameObject>)gr).getMessage());
        
        //-------ADD A PRIZE-----------------------------------------
        if(this.getArena().getPlayMode()>2)
        {
            if(moveNum%Prize.TURNS_PER_PRIZE==1)
            {
                addPrize();
            }
            if(moveNum%Prize.TURNS_PER_SUPER_PRIZE==0)
            {
                Location loc = getRandomEmptySuperPrizeLocation(); 
                if(loc != null)
                    new Prize(0,Prize.SUPER_PRIZE_VALUE).putSelfInGrid(getGrid(),loc);
            }
        }
        

        //--------------Is round/match over?-------------------------
        moveNum++;
        if(moveNum > NUM_MOVES_IN_ROUND)
        {
            scoreRound();
            initializeGridForRound();
            
//            moveNum = 1;
        }
        if(roundNum > NUM_ROUNDS_IN_MATCH && roundRobin) 
        {
            initializeMatch();
            return;
        } 
        
        
        
     
    }
    
    
    /**
     * Add a new AnimalBrain to the arena. 
     * @param the AnimalBrain to be added.  
     */
    public void add(AnimalBrain bot)
    {
        Animal newAnimal = new Animal(bot);
//        Location inCenter = this.getRandomEmptyCenterLocation();
        
       animalsInMaze.add(newAnimal);
        initializeGridForRound();
    }    
    public void addToAllAnimals(AnimalBrain bot)
    {
        Animal newAnimal = new Animal(bot);
        allAnimals.add(newAnimal);        
    }
    public void addFromAllAnimals(int num)
    {
        if(num<allAnimals.size())
        {
            try 
            {
                Class<? extends AnimalBrain> c = allAnimals.get(num).getAnimal().getClass();
                AnimalBrain r = c.newInstance();
                add(r);
            } 
            catch (InstantiationException ex) {  } //oh well...
            catch (IllegalAccessException ex) {  } //oh well...
        }
            
    }
    public void resetAnimalsInMaze()
    {
        for(Animal r : animalsInMaze)
            r.clearScores();
    }
    /**
     * Gets one of the 4 possible directions.
     * @return a random direction.
     */
    public int getRandomDirection()
    {
        return randy.nextInt(4)*90;
    }
    /**
     * Gets an empty Location in the center room.  
     * @return a random empty Location from the center room.  
     */
    public Location getRandomEmptyCenterLocation()
    {
        Grid<GameObject> grid = getGrid();
        int rows = grid.getNumRows();
        int cols = grid.getNumCols();
        int centerRow = rows/2; 
        int centerCol = cols/2;
        // get all valid empty locations and pick one at random
        ArrayList<Location> emptyLocs = new ArrayList<Location>();
        for(int row=centerRow-1; row<centerRow+2; row++)
        {
            for(int col=centerCol-1; col<centerCol+2; col++)
            {
                Location loc = new Location(row,col);
                if(grid.get(loc)==null)
                {
                    emptyLocs.add(loc);
                }
            }
        }
        
        if (emptyLocs.isEmpty()) //Go wider (24 places!)
        {
            for(int row=centerRow-2; row<centerRow+3; row++)
            {
                for(int col=centerCol-2; col<centerCol+3; col++)
                {
                    Location loc = new Location(row,col);
                    if(grid.get(loc)==null)
                    {
                        emptyLocs.add(loc);
                    }
                }
            }
        }
        if (emptyLocs.isEmpty())
        {
            System.out.println("WARNING: could not find an empty non-center location!!! ");
            return new Location(15,15);
        }
        int r = randy.nextInt(emptyLocs.size());
        return emptyLocs.get(r);       

    }
     
    public Location getEmptyRandomPrizeLocation()
    {
        Location loc;
        Grid<GameObject> grid = getGrid();
        int rows = grid.getNumRows();
        int cols = grid.getNumCols();
        
        int loopCount = 0;
        do
        {
            loopCount++;
            loc = new Location(randy.nextInt(rows),randy.nextInt(cols));
        } while( (isNearAnimal(loc) || isInRoom(loc) || !grid.isValid(loc)) && loopCount<100);
        
        if(loopCount==100) return null; //no space found!
        
        return loc;
    }
    
    public boolean isNearAnimal(Location loc)
    {
        int PRIZE_PROXIMITY = 10;
        SJAGrid<GameObject> grid = (SJAGrid<GameObject>)this.getGrid();
        ArrayList<Animal> allAnimals = grid.getAllAnimals();
        for(Animal r : allAnimals)
        {
            if(loc.distanceTo(r.getLocation()) < PRIZE_PROXIMITY)
                return true;
        }
        
        return false;
    }
    
    
    public ArrayList<Location> getSuperPrizeLocations()
    {
        Grid<GameObject> grid = getGrid();
        int rows = grid.getNumRows();
        int cols = grid.getNumCols();
        int centerRow = rows/2; 
        int centerCol = cols/2;
        
        ArrayList<Location> prizeSpots = new ArrayList<Location>();
        prizeSpots.add(new Location(centerRow,1));
        prizeSpots.add(new Location(centerRow,cols-2));
        prizeSpots.add(new Location(1,centerCol));
        prizeSpots.add(new Location(rows-2,centerCol));
        
        return prizeSpots;
    }
    
    public Location getRandomEmptySuperPrizeLocation()
    {
        ArrayList<Location> prizeSpots = getSuperPrizeLocations();
        for(int q=0;q<10;q++)
        {
            Location loc = prizeSpots.get(randy.nextInt(prizeSpots.size()));
            if(getGrid().get(loc) == null || getGrid().get(loc) instanceof Tail)
                return loc;
        }
        return null;
    }
        
    public boolean isInRoom(Location loc)
    {
        for(Location prize : getSuperPrizeLocations())
        {
            if(loc.distanceTo(prize) <= 2)
                return true;
        }
        return false;
    }
    
    public void addPrize()
    {
        SJAGrid<GameObject> gr = (SJAGrid<GameObject>)this.getGrid();
        Location p = gr.getEmptyRandomPrizeLocation();
        if(p != null)
            new Prize(0,Prize.DEFAULT_VALUE).putSelfInGrid(getGrid(), p);                
    }
    
}
