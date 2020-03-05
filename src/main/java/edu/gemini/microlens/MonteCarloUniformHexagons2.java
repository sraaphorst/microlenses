package edu.gemini.microlens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class MonteCarloUniformHexagons2 extends JPanel {
    private enum Microlenses {
        /**
         * The side of the hexagons in arcsecs and the filling factor as a percentage of hits.
         * The circles are the number of concentric circles forming the lens.
         */
        STANDARD_RESOLUTION(0.2272, 0.85, 2),
        HIGH_RESOLUTION(0.1365, 0.93, 3);

        final double side;
        final double filling_factor;
        final int circles;

        Microlenses(final double side, final double filling_factor, final int circles) {
            this.side = side;
            this.filling_factor = filling_factor;
            this.circles = circles;
        }
    }

    /**
     * We want 14 hexagons, with 7 hexagons inside of 7 hexagons with a
     * fixed padding with the goal of achieving a certain "filling factor",
     * i.e. percentage of hits.
     *
     * We then perform a Monte Carlo simulation by selecting points at
     * random and determining if they are in the inner hexagons. This gives
     * us the filling factor for this fixed padding. If we achieve the filling
     * factor, then the padding is the average distance between the
     * microlenses.
     */
    // The size of the component and the padding around it.
    // This PADDING is different from padding, which is the padding between microlenses.
    private final static int SIZE = 900;
    private final static int PADDING = 100;
    private final static Dimension d = new Dimension(SIZE + 2 * PADDING, SIZE + 2 * PADDING);

    private final List<RegularHexagon> hexagons;
    private final List<Point2D.Double> centres;

    final double conversion;
    final double hexagonRadius;

    // The number of hits and misses.
    public int hits = 0;
    public int misses = 0;

    /**
     * Initialize the simulation.
     * @param microlenses the type of simulation
     * @param padding the padding IN ARCSECS
     */
    public MonteCarloUniformHexagons2(final Microlenses microlenses, final double padding) {
        hexagons = new ArrayList<>();

        // The image is square. Calculate the arcseconds across so that we can determine the number of pixels
        // per arsecond. We can do this by drawing a line horizontally through the middle, which is four
        // paddedBisectors + two bisectors.
        hexagonRadius = microlenses.side;
        final double paddedHexagonRadius = hexagonRadius + padding;

        // We get the bisectors using the Pythagorean formula and the fact that a regular hexagon is simply
        // six equilateral triangles, so each side of the hexagon has the same length as the radius.
        final double bisector = Math.sqrt(3) / 2.0 * hexagonRadius;
        final double paddedBisector = Math.sqrt(3) / 2.0 * (hexagonRadius + padding);

        // Width of the world in arcseconds.
        final double width = 2 * bisector + 4 * paddedBisector;

        // Conversion factor from arcseconds to pixels.
        conversion = SIZE / width; // pixels / arcseconds

        /** FIRST ROW **/
        centres = new ArrayList<>();
        centres.add(new Point2D.Double(bisector + paddedBisector, hexagonRadius));
        centres.add(new Point2D.Double(bisector + 3 * paddedBisector, hexagonRadius));
        centres.add(new Point2D.Double(bisector, paddedHexagonRadius +  hexagonRadius / 2.0)); //The y coordinate may not be right.
        centres.add(new Point2D.Double(bisector + 2 * paddedBisector, paddedHexagonRadius + hexagonRadius / 2.0));
        centres.add(new Point2D.Double(bisector + 4 * paddedBisector, paddedHexagonRadius + hexagonRadius / 2.0));
        centres.add(new Point2D.Double(bisector + paddedBisector, 2 * paddedHexagonRadius));
        centres.add(new Point2D.Double(bisector + 3 * paddedBisector, 2 * paddedHexagonRadius));

        // First hexagon.
        final RegularHexagon hex_1_1 = new RegularHexagon();
        final AffineTransform tr_1_1 = new AffineTransform();
        tr_1_1.translate(PADDING + (bisector + paddedBisector) * conversion, PADDING + paddedHexagonRadius * conversion);
        tr_1_1.scale(hexagonRadius * conversion, hexagonRadius * conversion);
        tr_1_1.rotate(Math.PI / 2.0);
        hex_1_1.transform(tr_1_1);
        hexagons.add(hex_1_1);

        // Second hexagon.
        final RegularHexagon hex_1_2 = new RegularHexagon();
        final AffineTransform tr_1_2 = new AffineTransform();
        tr_1_2.translate(PADDING + SIZE - (bisector + paddedBisector) * conversion, PADDING + paddedHexagonRadius * conversion);
        tr_1_2.rotate(Math.PI / 2.0);
        tr_1_2.scale(hexagonRadius * conversion, hexagonRadius * conversion);
        hex_1_2.transform(tr_1_2);
        hexagons.add(hex_1_2);

        /** SECOND ROW **/
        final RegularHexagon hex_2_1 = new RegularHexagon();
        final AffineTransform tr_2_1 = new AffineTransform();
        tr_2_1.translate(PADDING + bisector * conversion, PADDING + SIZE / 2.0);
        tr_2_1.rotate(Math.PI / 2.0);
        tr_2_1.scale(hexagonRadius * conversion, hexagonRadius * conversion);
        hex_2_1.transform(tr_2_1);
        hexagons.add(hex_2_1);


        final RegularHexagon hex_2_2 = new RegularHexagon();
        final AffineTransform tr_2_2 = new AffineTransform();
        tr_2_2.translate((SIZE + 2 * PADDING) / 2.0, PADDING + SIZE / 2.0);
        tr_2_2.rotate(Math.PI / 2.0);
        tr_2_2.scale(hexagonRadius * conversion, hexagonRadius * conversion);
        hex_2_2.transform(tr_2_2);
        hexagons.add(hex_2_2);

        final RegularHexagon hex_2_3 = new RegularHexagon();
        final AffineTransform tr_2_3 = new AffineTransform();
        tr_2_3.translate(SIZE  + PADDING - bisector * conversion, PADDING + SIZE / 2.0);
        tr_2_3.rotate(Math.PI / 2.0);
        tr_2_3.scale(hexagonRadius * conversion, hexagonRadius * conversion);
        hex_2_3.transform(tr_2_3);
        hexagons.add(hex_2_3);

        /** THIRD ROW **/
        // First hexagon.
        final RegularHexagon hex_3_1 = new RegularHexagon();
        final AffineTransform tr_3_1 = new AffineTransform();
        tr_3_1.translate(PADDING + (bisector + paddedBisector) * conversion, PADDING + SIZE - paddedHexagonRadius * conversion);
        tr_3_1.scale(hexagonRadius * conversion, hexagonRadius * conversion);
        tr_3_1.rotate(Math.PI / 2.0);
        hex_3_1.transform(tr_3_1);
        hexagons.add(hex_3_1);

        // Second hexagon.
        final RegularHexagon hex_3_2 = new RegularHexagon();
        final AffineTransform tr_3_2 = new AffineTransform();
        tr_3_2.translate(PADDING + SIZE - (bisector + paddedBisector) * conversion, PADDING + SIZE - paddedHexagonRadius * conversion);
        tr_3_2.rotate(Math.PI / 2.0);
        tr_3_2.scale(hexagonRadius * conversion, hexagonRadius * conversion);
        hex_3_2.transform(tr_3_2);
        hexagons.add(hex_3_2);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.println(e.getPoint());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.println(e.getPoint());
            }
        });

        // Determine if a point is in a hexagon.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final Point p = e.getPoint();
                for (RegularHexagon h: hexagons)
                    if (h.contains(p)) {
                        ++hits;
                        System.out.println("Inside hexagon " + h.getId() + ", hits: " + hits);
                        return;
                    }
                ++misses;
                System.out.println("Misses: " + misses);
            }
        });

        setSize(d);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;

        final Dimension size = getSize();
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, size.width, size.height);

        g2d.setColor(Color.cyan);
        g2d.drawRect(PADDING, PADDING, SIZE, SIZE);

        final int cx = (size.width - SIZE) / 2;
        final int cy = (size.height - SIZE) / 2;
        System.out.println(cx + " " + cy);

        System.out.println("Drawing " + hexagons.size() + " inner hexagons.");
        g2d.setColor(Color.yellow);
        g2d.setStroke(new BasicStroke(1));
//        hexagons.forEach(h -> {
//            g2d.setColor(h.getColor());
//            g2d.draw(h);
//        });
        centres.forEach(c -> {
            final RegularHexagon hexagon = new RegularHexagon();
            final double hcx = c.getX();
            final double hcy = c.getY();

            final AffineTransform trans = new AffineTransform();
            trans.translate(PADDING + hcx * conversion, PADDING + hcy * conversion);
            trans.rotate(Math.PI / 2.0);
            trans.scale(hexagonRadius * conversion, hexagonRadius * conversion);
            hexagon.transform(trans);
            g2d.setColor(hexagon.getColor());
            g2d.draw(hexagon);
        });
    }

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Hexagon");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final MonteCarloUniformHexagons2 hexagonPanel = new MonteCarloUniformHexagons2(Microlenses.STANDARD_RESOLUTION, 0.04);
        frame.add(hexagonPanel);

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int xPos = (screenSize.width - hexagonPanel.getWidth()) / 2;
        final int yPos = (screenSize.height - hexagonPanel.getHeight()) / 2;
        frame.setLocation(xPos, yPos);
        frame.setSize(d);
        frame.setMinimumSize(d);
        frame.setPreferredSize(d);
        frame.setMaximumSize(d);
        frame.setVisible(true);
    }
}