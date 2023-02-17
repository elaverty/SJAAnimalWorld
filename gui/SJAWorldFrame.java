package gui;

/* 
 * AP(r) Computer Science GridWorld Case Study:
 * Copyright(c) 2002-2006 College Entrance Examination Board 
 * (http://www.collegeboard.com).
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
 * @author Julie Zelenski
 * @author Chris Nevison
 * @author Cay Horstmann
 */



//import info.gridworld.grid.Grid;
//import info.gridworld.grid.Location;
//import info.gridworld.world.World;

import world.SJAWorld;
import world.World;
import grid.Grid;
import grid.Location;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;


/**
 * The WorldFrame displays a World and allows manipulation of its occupants.
 * <br />
 * This code is not tested on the AP CS A and AB exams. It contains GUI
 * implementation details that are not intended to be understood by AP CS
 * students.
 */
@SuppressWarnings("serial")
public class SJAWorldFrame<T> extends JFrame implements ActionListener
{
    private SJAGUIController<T> control;
    private SJAGridPanel display;
    private JTextArea messageArea;
    //private ArrayList<JMenuItem> menuItemsDisabledDuringRun;
    private World<T> world;
    private ResourceBundle resources;
    private DisplayMap displayMap;
    private SJAScoreBoard scoreBoard;
    
    private Set<Class> gridClasses;
    private JMenuBar mb;
    private JMenu menu;
    private JMenuItem m1, m2, m3, m4;

    private static int count = 0;

    /**
     * Constructs a WorldFrame that displays the occupants of a world
     * @param world the world to display
     */
    public SJAWorldFrame(World<T> world)
    {
        this.world = world;
        count++;
        
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent event)
            {
                count--;
                if (count == 0)
                    System.exit(0);
            }
        });

        
        // Draw the Grid
        displayMap = new DisplayMap();  
        
        // Centered Title
        String title = "SJA Animal World";
        
        setTitle(title);
        setLocation(25, 15);

        URL appIconUrl = getClass().getResource("prize.gif");
        if (appIconUrl != null)
        {
            ImageIcon appIcon = new ImageIcon(appIconUrl);
            setIconImage(appIcon.getImage());
        }
        
        makeMenu();
        setJMenuBar(mb);

        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        content.setLayout(new BorderLayout());
        setContentPane(content);

        display = new SJAGridPanel(displayMap, resources);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new
           KeyEventDispatcher() 
           {
               public boolean dispatchKeyEvent(KeyEvent event)
               {
                   if (getFocusOwner() == null) return false;
                   String text = KeyStroke.getKeyStrokeForEvent(event).toString();
                   final String PRESSED = "pressed ";                  
                   int n = text.indexOf(PRESSED);
                   if (n < 0) return false;
                   if (event.getKeyChar() == KeyEvent.CHAR_UNDEFINED && !event.isActionKey()) 
                       return false;
                   text = text.substring(0, n)  + text.substring(n + PRESSED.length());
                   boolean consumed = keyPressed(text);
                   if (consumed) repaint();
                   return consumed;
               }
           });
        
        JScrollPane scrollPane = new JScrollPane();

        scrollPane.setViewportView(display);
        content.add(scrollPane, BorderLayout.CENTER);

        gridClasses = new TreeSet<Class>(new Comparator<Class>()
        {
            public int compare(Class a, Class b)
            {
                return a.getName().compareTo(b.getName());
            }
        });
        for (String name : world.getGridClasses())
            try
            {
                gridClasses.add(Class.forName(name));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        Grid<T> gr = world.getGrid();
        gridClasses.add(gr.getClass());


        control = new SJAGUIController<T>(this, display, displayMap, resources);
        content.add(control.controlPanel(), BorderLayout.SOUTH);

        messageArea = new JTextArea(1, 35);
        messageArea.setEditable(false);
        messageArea.setFocusable(false);
        messageArea.setBackground(new Color(0xFAFAD2));
        content.add(new JScrollPane(messageArea), BorderLayout.NORTH);
     

        //FOR ANIMALS...
       scoreBoard = new SJAScoreBoard(gr);
        
        
       content.add(scoreBoard, BorderLayout.EAST);
        
			pack();
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			setVisible(true);
			repaint(); // to show message
			display.setGrid(gr);
	}

    public void repaint()
    {
        String message = getWorld().getMessage();
        if (message == null)
            message = "Welcome to SJA Animal World";
        messageArea.setText(message);
        messageArea.repaint();
        display.repaint(); // for applet
        scoreBoard.repaint();
        super.repaint();
    }

    
    public void makeMenu()
    {
    	// create a menubar
        mb = new JMenuBar();
  
        // create a menu
        menu = new JMenu("Menu");
        
        m1 = new JMenuItem("Challenge 1 - Only one Prize / No Blocks (F1)");
        m2 = new JMenuItem("Challenge 2 - Plus-sign Blocks (F2)");
        m3 = new JMenuItem("Challenge 3 - Normal Mode (F3)");
        m4 = new JMenuItem("Quit ()");
        
        // add menu items to menu
        menu.add(m1);
        menu.add(m2);
        menu.add(m3);
        menu.add(m4);
      
        
        // add menu to menu bar
        mb.add(menu);
        
        // add the actionListeners to the menuItems
        m1.addActionListener(this);
        m2.addActionListener(this);
        m3.addActionListener(this);
        m4.addActionListener(this);
    
    	
    	
    }
    /**
     * Gets the world that this frame displays
     * @return the world
     */
    public World<T> getWorld()
    {
        return world;
    }

    /**
     * Sets a new grid for this world. Occupants are transferred from
     * the old world to the new.
     * @param newGrid the new grid
     */
    public void setGrid(Grid<T> newGrid)
    {
        Grid<T> oldGrid = world.getGrid();
        Map<Location, T> occupants = new HashMap<Location, T>();
        for (Location loc : oldGrid.getOccupiedLocations())
            occupants.put(loc, world.remove(loc));

        world.setGrid(newGrid);
        for (Location loc : occupants.keySet())
        {
            if (newGrid.isValid(loc))
                world.add(loc, occupants.get(loc));
        }

        display.setGrid(newGrid);
        repaint();
    }

 
    public boolean keyPressed(String description)
    {
        if(description.equals("F1"))
        { setPlayMode(SJAArena.CHALLENGE_1); return true; }
        if(description.equals("F2"))
        { setPlayMode(SJAArena.CHALLENGE_2); return true; }
        if(description.equals("F3"))
        { setPlayMode(SJAArena.CHALLENGE_3); return true; }
       
        
        if(description.equals("Q") || description.equals("q") ||description.equals("ESCAPE") )
        { System.exit(0); return true; }
        
        return false;
    }
    
    private void setPlayMode(int mode)
    {
        SJAWorld aw = (SJAWorld)world; 
        aw.getArena().setPlayMode(mode);
        aw.initializeGridForRound();
        display.repaint();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==m1)
			setPlayMode(SJAArena.CHALLENGE_1);
		if (e.getSource()==m2)
			setPlayMode(SJAArena.CHALLENGE_2);
		if (e.getSource()==m3)
			setPlayMode(SJAArena.CHALLENGE_3);
		if (e.getSource()==m4)
			System.exit(0);;
		
	}
}
