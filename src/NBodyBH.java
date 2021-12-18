/**
 * NBodyBH.java
 *
 * Reads in a universe of N bodies from stdin, and performs an
 * N-Body simulation in O(N log N) using the Barnes-Hut algorithm.
 *
 * Compilation:  javac NBodyBH.java
 * Execution:    java NBodyBH < inputs/[filename].txt
 * Dependencies: BHTree.java Body.java Quad.java StdDraw.java
 * Input files:  ./inputs/*.txt
 *
 * @author chindesaurus
 * @version 1.00
 */

import edu.princeton.cs.algs4.Vector;
import java.math.BigDecimal;
import java.util.Scanner;

public class NBodyBH {
    public static void main(String[] args) {

        // for reading from stdin
        Scanner console = new Scanner(System.in);

        final double dt = 0.1;                     // time quantum
        double radius = console.nextDouble();      // radius of universe
        int N = console.nextInt();                 // number of particles

        // read in and initialize bodies
        Ball[] balls = new Ball [N];               // array of N bodies
        for (int i = 0; i < N; i++) {
            double px   = console.nextDouble();
            double py   = console.nextDouble();
            double vx   = console.nextDouble();
            double vy   = console.nextDouble();
            double ballRadius = console.nextDouble();
            double ballMass = console.nextDouble();
            int r    = console.nextInt();
            int g   = console.nextInt();
            int b    = console.nextInt();
            balls[i]   = new Ball(new Vector(px,py), new Vector(vx,vy),new Vector(0,0),ballRadius,ballMass,r,g,b);
        }


        // simulate the universe
        for (double t = 0.0; true; t=add(t,dt)) {
            Quad quad = new Quad(radius , radius , radius * 2);
            BHTree tree = new BHTree(quad);

            // build the Barnes-Hut tree
            for (int i = 0; i < N; i++) {
                if (balls[i].in(quad)) {
                    tree.insert(balls[i]);

                }
            }

            for (int i = 0; i < N; i++) {
                balls[i].restForce();
                tree.updateForce(balls[i]);
                balls[i].update(dt);
            }

            Vector[] newForce = new Vector[N];
            for (int i = 0; i < N; i++) {
                newForce[i] = new Vector(0,0);
                newForce[i]=balls[i].force;
            }

        }
    }
    public static double add(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.add(b2).doubleValue();
    }
}
