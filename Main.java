import brain.*;
import world.SJAWorld;

public class Main
{
    public static void main(String[] args)
    {
        SJAWorld world = new SJAWorld();
        world.show(); 
        world.add(new Laverty());
        world.show();
    }
}
