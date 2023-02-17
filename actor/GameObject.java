package actor;

/* 
 * This class is an adaptation of the Actor class from GridWorld.  
 * 
 * AP(r) Computer Science GridWorld Case Study:
 * Copyright(c) 2005-2006 Cay S. Horstmann (http://horstmann.com)
 *
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * @author Cay Horstmann
 * 
 * updated Spring 2021 - Liz Laverty
 */



import grid.Grid;
import grid.Location;
import grid.SJAGrid;
import java.awt.Color;

/**
 * A <code>GameObject</code> is an entity with a color and direction that 
 * can act. <br />
  */
public class GameObject
{
    private Grid<GameObject> grid;
    private Location location;
    private int direction;
    private Color color;

    /**
     * Constructs a blue actor that is facing north.
     */
    public GameObject()
    {
        color = Color.BLUE;
        direction = Location.NORTH;
        grid = null;
        location = null;
    }
    /**
     * Constructs a GameObject that copies the properties of another GameObject.
     * @param in the GameObject to be copied to construct this GameObject
     */
    public GameObject(GameObject in)
    {
        color = in.getColor();
        direction = in.getDirection();
        grid = null;
        location = in.getLocation();        
    }

    /**
     * Gets the color of this GameObject.
     * @return the color of this GameObject
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Sets the color of this GameObject.
     * @param newColor the new color
     */
    public void setColor(Color newColor)
    {
        color = newColor;
    }

    /**
     * Gets the current direction of this GameObject.
     * @return the direction of this GameObject as an angle. (0,90,180 or 270)
     */
    public int getDirection()
    {
        return direction;
    }

    /**
     * Sets the current direction of this GameObject.
     * @param newDirection the new direction. The direction of this GameObject 
 is set to the nearest lower multiple of 90 degrees of
 <code>newDirection</code>. (0,90,180 or 270)
     */
    public void setDirection(int newDirection)
    {
        direction = newDirection % Location.FULL_CIRCLE;
        if (direction < 0)
            direction += Location.FULL_CIRCLE;
        //Animals can move diagonally
        if(direction%Location.HALF_RIGHT != 0)
            direction = direction - direction%Location.HALF_RIGHT;
    }

    /**
     * Gets the grid in which this GameObject is located.
     * @return the grid of this GameObject, or <code>null</code> if this 
 GameObject is not contained in a grid
     */
    public SJAGrid<GameObject> getGrid()
    {
        return (SJAGrid<GameObject>)grid;
    }

    /**
     * Gets the location of this GameObject.
     * @return the location of this GameObject, or <code>null</code> if this 
 GameObject is not contained in a grid
     */
    public Location getLocation()
    {
        return location;
    }
    
    public void setLocation(Location loc) { location = loc; }

    public int getRow() { return location.getRow(); }
    public int getCol() { return location.getCol(); }
    
    /**
     * Puts this GameObject into a grid. If there is another GameObject at the given
     * location, it is removed. <br />
     * Precondition: (1) This GameObject is not contained in a grid (2)
     * <code>loc</code> is valid in <code>gr</code>
     * @param gr the grid into which this GameObject should be placed
     * @param loc the location into which the GameObject should be placed
     */
    public void putSelfInGrid(Grid<GameObject> gr, Location loc)
    {
        if (grid != null)
            throw new IllegalStateException(
                    "This GameObject is already contained in a grid.");

        GameObject obj = gr.get(loc);
        if (obj != null)
            obj.removeSelfFromGrid();
        gr.put(loc, this);
        grid = gr;
        location = loc;
    }
/**
 * Puts this gameObj into a grid, but not into a specific Location.  
 * This is used for 'OffGrid' gameObjects 
 * @param gr the grid into which this actor should be placed
 */
    public void putSelfInGridNoLocation(Grid<GameObject> gr)
    {
        grid = gr;
    }

    /**
     * Removes this actor from its grid. <br />
     * Precondition: This actor is contained in a grid
     */
    public void removeSelfFromGrid()
    {
        if (grid == null)
            throw new IllegalStateException(
                    "This gameObj is not contained in a grid.");
        if (grid.get(location) != this)
            throw new IllegalStateException(
                    "The grid contains a different gameObj at location "
                            + location + ".");

        grid.remove(location);
        grid = null;
        location = null;
    }

    /**
     * Moves this actor to a new location. If there is another actor at the
     * given location, it is removed. <br />
     * Precondition: (1) This actor is contained in a grid (2)
     * <code>newLocation</code> is valid in the grid of this actor
     * @param newLocation the new location
     */
    public void moveTo(Location newLocation)
    {
        if (grid == null)
            throw new IllegalStateException("This gameObj is not in a grid.");
        if (grid.get(location) != this)
            throw new IllegalStateException(
                    "The grid contains a different gameObj at location "
                            + location + ".");
        if (!grid.isValid(newLocation))
            throw new IllegalArgumentException("Location " + newLocation
                    + " is not valid.");

        if (newLocation.equals(location))
            return;
        grid.remove(location);
        GameObject other = grid.get(newLocation);
        if (other != null)
            other.removeSelfFromGrid();
        location = newLocation;
        grid.put(location, this);
    }
    

    public boolean isNextSpaceValid()
    {
        Location next = location.getAdjacentLocation(direction);
        if(!getGrid().isValid(next))
            return false;
        return true;
    }
    /**
     * Reverses the direction of this gameObject. Override this method in subclasses
     * of <code>GameObject</code> to define types of gameObjects with different behavior
     * 
     */
    public void act()
    {
//        setDirection(getDirection() + Location.HALF_CIRCLE);
    }

    /**
     * Creates a string that describes this gameObject.
     * @return a string with the location, direction, and color of this gameObject
     */
    public String toString()
    {
        return getClass().getName() + "[location=" + location + ",direction="
                + direction + ",color=" + color + "]";
    }
    /**
     * Gets a clone of this GameObject
     * @return a new GameObject which copies the properties of this GameObject
     */
    public GameObject getClone()
    {
        GameObject clone = new GameObject(this);
        return clone;
    }
}