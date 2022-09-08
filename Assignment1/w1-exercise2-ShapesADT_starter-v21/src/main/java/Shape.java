import java.awt.Color;
/**
 * Abstract class for a shapes.Shape.  All Shape objects have a color and an area.
 **/
public abstract class Shape{

    private Color color;

    /*
    Create a Shape with a color
    @param Color
     */
    public Shape (Color color){
        this.color = color;
    }
    /**
     * Returns the area of a shapes.Shape
     **/
    public abstract double getArea();

    /**
     * Returns the color of a shapes.Shape
     **/
    public Color getColor(){
        return color;
    }

    /**
     * @return a String representation of this Shape
     */
    public String toString(){
        Class c = getClass();
        return "shapes.Shape: "+c.getSimpleName()+" with color: "+ color + " and its area is "+getArea();
    }

    /*
      Two Shapes are equal if their color is equal.  Note that area is not a basis for equality here.
     */

    public boolean equals (Object o){
        if (o instanceof Shape){
            Shape other = (Shape)o;
            return other.getColor().equals(color);
        }
        return false;
    }

    /*
        Returns an int which is a hashcode for this object.
     * This is the overidden version of the method inherited from Object and the documentation states:
     * "If two objects are equal according to the equals(Object) method,
     * then calling the hashCode method on each of the two objects must produce the same integer result"
     */

    @Override
    public int hashCode() {
        return color.hashCode();
    }
}
