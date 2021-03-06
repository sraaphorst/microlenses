package edu.gemini.microlens;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * A regular hexagon, i.e. all sides have the same length and all internal angles are 120 degrees.
 * It comprises six equilateral triangles extending from the central point.
 * Thus, since the triangles are equilateral, the distance from the central point to a vertex on the circle (the
 * radius) is the same as the length of the side of the hexagon.
 *
 * Given one vertex of the hexagon, we can find the remaining five vertices by rotations by 60 degrees = pi / 3 radians
 * We assume unless otherwise stated that the hexagon has (side, 0) as its initial point, i.e. has the form:
 *
 *       ----
 *      /    \
 *      \    /
 *       ----
 *
 * The hexagon is centered at (0,0) with sides of length 1. Affine transformations should be used to move it.
 */
public class RegularHexagon extends Path2D.Double {
    // Identifier for the hexagon.
    private static int idCount = 0;
    private final static Color[] colors = new Color[]{
        Color.green,
        Color.blue,
        Color.magenta,
        Color.yellow,
        Color.red,
        Color.white,
        Color.pink,
        Color.gray,
        Color.orange,
        Color.lightGray
    };
    private final int id;

    /**
     * A hexagon with center point and a vertex at the specified point.
     */
    public RegularHexagon() {
        super(WIND_EVEN_ODD);

        id = idCount++;
        // The rotation to create the edges of the hexagon.
        final AffineTransform rot = AffineTransform.getRotateInstance(Math.PI / 3.0);

        double x = 1;
        double y = 0;

        moveTo(x, y);
        lineTo(x, y);
        Point2D.Double p = new Point2D.Double(x, 0);
        for (int i = 0; i < 6; ++i) {
            rot.transform(p, p);
            lineTo(p.getX(), p.getY());
        }
        closePath();
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return colors[id % colors.length];
    }
}
