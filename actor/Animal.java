package actor;

import grid.Location;
import gui.AnimalColorAssigner;
import java.awt.Color;
import java.util.ArrayList;
import world.SJAWorld;
/**
 * Animals are the competitors in the AnimalWorld game.  Every Animal has a AnimalBrain 
 that acts as its 'brain' by making decisions for its actions.  
 * @author Spock
 * updated Spring 2021 - Liz Laverty
 * 
 */
public class Animal extends GameObject
{       
    /**
     * Each Animal has an AnimalBrain as it's 'brain'.  The Animal 'tells' the AnimalBrain 
 what it 'sees' and the AnimalBrain makes the decision on how to act.  
     */
    private AnimalBrain aBrain; 
        
    private int score;
    private int roundsWon;
    private int matchesWon;
    private int matchesTied;
    private int matchesLost;
    private int totalScore = 0;
    
    private int mostRecentChoice = 0;
    
    /**
     * Constructs a red Animal with a generic AnimalBrain.  
     */
    public Animal()
    {
        setColor(Color.RED);
        aBrain = new AnimalBrain();
        initialize();
    }
    /**
     * Constructs a Animal with the given color and given AnimalBrain for its 'brain'.
     * All Animals have an AnimalBrain that chooses their action each turn.  
     * @param ab the AnimalBrain that makes decisions for this Animal.
     */
    public Animal(AnimalBrain b)
    {
        aBrain = b;
        if(b.getPreferredColor() == null)
        {
            setColor(AnimalColorAssigner.getAssignedColor());
            b.setPreferredColor(getColor());
            
        }
        else    
            setColor(b.getPreferredColor());
        
        initialize();
    }
    /**
     * Constructs a copy of this Animal (that does not include the AnimalBrain.)
     * @param in the Animal being copied.
     */
    public Animal(Animal in)
    {
        super(in);
        aBrain = new AnimalBrain();
        setLocation(in.getLocation());
        score = in.getScore();
    }

    
    public int getMostRecentChoice() { return mostRecentChoice; }
    
    
    /**
     * Overrides the <code>act</code> method in the <code>GameObject</code> 
 class to act based on what the AnimalBrain decides.
     */
    @Override
    public void act()
    {
        //Ask the AnimalBrain what to do. 
        giveDataToAnimal();
        int choice = aBrain.chooseAction();
        mostRecentChoice = choice;
        
        turn((choice%1000)); //Turn to face the direction of the choice.
        if( (choice%1000)%90 != 0) choice = -1; //An invalid choice.  
        
        if(choice < 0)
        {
            //REST
        }
        else if(0 <= choice && choice <= 999) 
        {
            //MOVE
            move();
        }
        else if(1000 <= choice && choice <= 1999) 
        {
            //DART
            dart();
        }
        else if(2000 <= choice && choice <= 2999) 
        {
            //BUILD BLOCK
            buildBlock();
        }
        else if(3000 <= choice && choice <= 3999) 
        {
            //BUILD WALLS
            buildWall();
        }
        
        //Color change...
        if(!getColor().equals(aBrain.getPreferredColor())
                && aBrain.getPreferredColor() != null)
            setColor(aBrain.getPreferredColor());
        
    } //end of act() method
    
    /**
     * Turns the Animal
     * @param newDirection the direction to turn to.   
     */
    public void turn(int newDirection)
    {
        setDirection(newDirection);
    }
     public boolean canMove()
    {
        return canMove(getLocation(), getDirection());
    }
   
    /**
     * This method does not allow you to move onto other Animals or Blocks 
     */
    public boolean canMove(Location loc, int dir)
    {
        Location next = loc.getAdjacentLocation(dir);
        if(!getGrid().isValid(next)) 
            return false;
        //Make sure destination is not occupied by another Animal
        if(getGrid().get(next) instanceof Animal)
            return false;
        //Make sure destination is not occupied by a Block
        if(getGrid().get(next) instanceof Block)
            return false;
        
        return true;
    }
    
    /**
     * Moves the animal forward, putting a Tail into the location it previously
     * occupied.
     */
    public void move()
    {        
        if(canMove())
        {
            Location old = getLocation();
            Location next = old.getAdjacentLocation(getDirection());

            processDestination(next);
            moveTo(next);
            placeTail(old);
        }
    }
    
    private void dart()
    {
        Location next = getLocation();
        Location old = getLocation();
        while(canMove(next,getDirection()))
        {
            
            next = next.getAdjacentLocation(getDirection()); 
            if(getGrid().get(next) == null || getGrid().get(next) instanceof Tail)
            {
                moveTo(next);
                placeTail(old);
                old = new Location(next.getRow(), next.getCol());
            }
        }
        processDestination(next);
        moveTo(next);
    }
    
    private void processDestination(Location next)
    {
        GameObject nextObj = getGrid().get(next);

        if(nextObj instanceof Prize)
        {
            Prize m = (Prize)nextObj;
            score+=m.getValue();  
        }
    }
    
    private void placeTail(Location loc)
    {
        Tail t = new Tail(getColor());
        t.putSelfInGrid(getGrid(), loc);
    }
    
    private void buildBlock()
    {
        Location next = getLocation().getAdjacentLocation(getDirection());
        if(getGrid().isValid(next) && 
                !(getGrid().get(next) instanceof Animal) &&
                !(getGrid().get(next) instanceof Block))
        {
            new Block().putSelfInGrid(getGrid(), next);
            //Add a Prize!
            Location p = getGrid().getEmptyRandomPrizeLocation();
            if(p != null)
                new Prize(0,Prize.BUILT_VALUE).putSelfInGrid(getGrid(), p);                
            
        }
    }
    
    private void buildWall()
    {
        ArrayList<Location> places = new ArrayList<Location>();
        Location centerOfWall = getLocation().getAdjacentLocation(getDirection());
        places.add(centerOfWall);
        Location left = new Location(centerOfWall.getRow(), centerOfWall.getCol());
        Location right = new Location(centerOfWall.getRow(), centerOfWall.getCol());
        
        int WALL_SIZE_FROM_CENTER = 3;
        for(int q=0; q<WALL_SIZE_FROM_CENTER; q++)
        {
            left = left.getAdjacentLocation(getDirection()-90);
            places.add(left);
            right = right.getAdjacentLocation(getDirection()+90);
            places.add(right);
        }
        
        for(Location loc : places)
        {
            if(getGrid().isValid(loc) && 
                    !(getGrid().get(loc) instanceof Animal) &&
                    !(getGrid().get(loc) instanceof Block) )
            {
                new Block(Block.TEMPORARY_DURATION).putSelfInGrid(getGrid(), loc);
            }            
        }
    }
    

    @Override
    public String toString()
    {
        return "Animal: "+ aBrain.getName();
    }

    /**
     * Updates the most recent data (location, grid and status)
 information to the BotBrain.  This allows the AnimalBrain to make a decision
 based on current data every turn.  
     */
    public final void giveDataToAnimal()
    {
        //score, energy, col, row, myStuff ================
        aBrain.setScore(score);
        aBrain.setLocation(getLocation().getCol(), getLocation().getRow());
        //match stuff: bestScore, roundsWon ===================
        aBrain.setBestScore(this.calculateBestScore());
        aBrain.setRoundsWon(this.getRoundsWon());
        //world stuff: moveNumber, roundNumber ================
        aBrain.setMoveNumber(SJAWorld.getMoveNum());
        aBrain.setRoundNumber(SJAWorld.getRoundNum());

        //theArena!============================================
        int numRows = getGrid().getNumRows();
        int numCols = getGrid().getNumCols();
        GameObject[][] theArena = new GameObject[numRows][numCols];
        for(int row=0; row<numRows; row++)
        {
            for(int col=0; col<numCols; col++)
            {
                GameObject a = getGrid().get(new Location(row, col));
                if(a != null && !(a instanceof Tail) )
                    theArena[row][col] = a.getClone();
                //Might need to do each with instanceOf here...
                
            }
        }
        aBrain.setArena(theArena);
            
 }
    
    public int calculateBestScore()
    {
        int bestScore = getScore();
        ArrayList<Animal> bots = getGrid().getAllAnimals();
        for(Animal b : bots)
        {
            if(b.getScore() > bestScore)
            {
                bestScore = b.getScore();
            }
        }  
        return bestScore;
    }
    /**
     * Accessor method to get the AnimalBrain from an Animal.
     * @return the AnimalBrain 'brain' of this AnimalBrain.
     */
    public AnimalBrain getAnimal()
    {
        return aBrain;
    }

    /**
     * Gets the current score (from this round).  
     * @return the score
     */
    public int getScore() { return score; }
    /**
     * Sets the current score of this Animal.
     * @param in the score
     */
    public void setScore(int in) { score = in; }
    /**
     * Adds the given amount to score of this Animal.  
     * @param add the amount to add
     */
    public void addToScore(int add) { score += add; }
    /**
     * Gets the total points scored over all rounds in this match for this Animal.
     * @return the total score
     */
    public int getTotalScore() { return totalScore; }

    /**
     * Gets the number of rounds won by this Animal in the match.
     * @return the rounds won
     */
    public int getRoundsWon() { return roundsWon; }
    
    
    /**
     * Sets the number of rounds won by this Animal in the match.
     * @param in the rounds won
     */
    public void setRoundsWon(int in) { roundsWon = in; }
    /**
     * Increases the number of rounds won in this match by this Animal by one.
     */
    public void increaseRoundsWon() { roundsWon++; }

    // These methods are used for the RoundRobin tourney.
    public int getMatchesWon() { return matchesWon; }
    public int getMatchesTied() { return matchesTied; }
    public int getMatchesLost() { return matchesLost; }
    public void increaseMatchesWon() { matchesWon++; }
    public void increaseMatchesTied() { matchesTied++; }
    public void increaseMatchesLost() { matchesLost++; }
     /**
     * Initializes this Animal for a new round.  
     */
    public final void initialize()
    {
        totalScore += score;
        score = 0;
        aBrain.initForRound();
    }
    
    public void clearScores()
    {
        score = 0;
        roundsWon = 0;
        matchesWon = 0;
        matchesTied = 0;
        matchesLost = 0;
        totalScore = 0;
    }

    @Override
    public GameObject getClone()
    {
        GameObject clone = new Animal(this);
        return clone;
    }
    
}