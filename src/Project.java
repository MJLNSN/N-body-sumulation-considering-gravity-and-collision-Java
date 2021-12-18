import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Vector;

import java.math.BigDecimal;
import java.util.Scanner;

class Project {
    private final int ballNum;  //number of balls never change in a system
    public Ball[] balls; //store all balls in a Ball type array
    int outNum;
    String out;
    int length;
    double timePeriod;
    MinPQ<Event> pq;
    double specialTime;
    double[][] outputBalls;

    public Project() {
        Scanner input = new Scanner(System.in);
        out = input.next(); //terminal or gui
        length = input.nextInt();
        //side length of the square space
        ballNum = input.nextInt();
        //number of balls

        double[][] para = new double[ballNum][9];
        for (int i = 0; i < ballNum; i++) {
            for (int j = 0; j < 9; j++) {
                para[i][j] = input.nextDouble();
            }
        }
        //use para to save the 9 arguments of each ball


        outNum = input.nextInt();
        //number of lines of the output
        if(outNum != 0) {
            outputBalls = new double[outNum][2];
            for (int i = 0; i < outNum; i++) {
                for (int j = 0; j < 2; j++) {
                    outputBalls[i][j] = input.nextDouble();
                }
            }
        }
        else{
            outputBalls = new double[1][2];
        }


        balls = new Ball[ballNum];
        for (int j = 0; j < para.length; j++) {
            Vector tmp = new Vector(1, 1);
            balls[j] = new Ball(tmp, tmp, tmp, 1.0, 1.0, 1, 1, 1);
            balls[j].position = new Vector(para[j][0], para[j][1]);
            balls[j].velocity = new Vector(para[j][2], para[j][3]);
            balls[j].ballRadius = para[j][4];
            balls[j].ballMass = para[j][5];
            balls[j].r = (int) para[j][6];
            balls[j].g = (int) para[j][7];
            balls[j].b = (int) para[j][8];
        }
    }


    public Vector forceFrom(Ball a, Ball b) {
        Vector zero = new Vector(0, 0);
        double G = 6.67e-11;
//        double G = 0;
        Vector delta = b.position.minus(a.position);
        double dist = delta.magnitude();
        double magnitude = (G * a.ballMass * b.ballMass) / (dist * dist);
        if (delta.magnitude() == 0) {
            return zero;
        }
        return delta.direction().scale(magnitude);
    }
    //return the gravity in vector form between two balls


    public double timeToHitVerticalWall(Ball ball) {
        if (ball.velocity.cartesian(0) > 0)
            return (length - ball.position.cartesian(0) - ball.ballRadius) / ball.velocity.cartesian(0);
        else if (ball.velocity.cartesian(0) < 0)
            return (ball.ballRadius - ball.position.cartesian(0)) / ball.velocity.cartesian(0);
        else return 100;
    }
    //particular behaviour will happen in this time
    //return 100 means won't happen

    public double timeToHitHorizontalWall(Ball ball) {
        if (ball.velocity.cartesian(1) > 0)
            return ((length - ball.position.cartesian(1) - ball.ballRadius) / ball.velocity.cartesian(1));
        else if (ball.velocity.cartesian(1) < 0)
            return (ball.ballRadius - ball.position.cartesian(1)) / ball.velocity.cartesian(1);
        else return 100;
    }


    public double timeToHit(Ball b1, Ball b2) {
        if (b1 == b2) return 100;
        double dx = b1.position.cartesian(0) - b2.position.cartesian(0);
        double dy = b1.position.cartesian(1) - b2.position.cartesian(1);
        double dvx = b1.velocity.cartesian(0) - b2.velocity.cartesian(0);
        double dvy = b1.velocity.cartesian(1) - b2.velocity.cartesian(1);
        double dvdr = dx * dvx + dy * dvy;
        if (dvdr > 0) return 1;
        double dvdv = dvx * dvx + dvy * dvy;
        if (dvdv == 0) return 1;
        double drdr = dx * dx + dy * dy;
        double sigma = b1.ballRadius + b2.ballRadius;
        double d = (dvdr * dvdr) - dvdv * (drdr - sigma * sigma);
//        if (drdr < sigma*sigma) {
//            StdOut.println("overlapping particles");
//            return 0;
//
//        }
        if (Math.abs(d) < 1e-9) {
            return 100;
        }
        return -(dvdr + Math.sqrt(d)) / dvdv;
    }
    // two balls will collide in this time


    public void bounceOff(Ball b1, Ball b2) {
        double dx = b1.position.cartesian(0) - b2.position.cartesian(0);
        double dy = b1.position.cartesian(1) - b2.position.cartesian(1);
        double dvx = b1.velocity.cartesian(0) - b2.velocity.cartesian(0);
        double dvy = b1.velocity.cartesian(1) - b2.velocity.cartesian(1);
        double dvdr = dx * dvx + dy * dvy;             // dv dot dr
        double dist = b2.ballRadius + b1.ballRadius;   // distance between particle centers at collison

        // magnitude of normal force
        double magnitude = 2 * b2.ballMass * b1.ballMass * dvdr / ((b2.ballMass + b1.ballMass) * dist);

        // normal force, and in x and y directions
        double fx = magnitude * dx / dist;
        double fy = magnitude * dy / dist;

        b2.velocity = new Vector(b2.velocity.cartesian(0) + fx / b2.ballMass, b2.velocity.cartesian(1) + fy / b2.ballMass);
        b1.velocity = new Vector(b1.velocity.cartesian(0) - fx / b1.ballMass, b1.velocity.cartesian(1) - fy / b1.ballMass);
        b2.count++;
        b1.count++;
    }
    //b1 hit b2, their velocities and count changes

    public Ball bounceOffVerticalWall(Ball ball) {
        ball.velocity = new Vector(-ball.velocity.cartesian(0), ball.velocity.cartesian(1));
        ball.count++;
        return ball;
    }
    //hit the vertical wall, velocity in x axis *(-1)

    public Ball bounceOffHorizontalWall(Ball ball) {
        ball.velocity = new Vector(ball.velocity.cartesian(0), -ball.velocity.cartesian(1));  // 和竖直墙碰撞，y 方向速度不变
        ball.count++;
        return ball;
    }
    //hit the horizontal wall, velocity in y axis *(-1)


    public String output(int i) {
        return balls[i].output();
    }


    public static double add(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.add(b2).doubleValue();
    }

    private void redraw(double limit, double t, int length) {
        if (out.equals("gui")) {
            StdDraw.clear();
            for (int i = 0; i < balls.length; i++) {
                balls[i].draw1(length);
            }
            StdDraw.show();
            StdDraw.pause(20);
            if (t < limit) {
                pq.insert(new Event(t + timePeriod, null, null));
            }
        }
        else {
            if (t < limit) {
                pq.insert(new Event(t + timePeriod, null, null));
            }

        }
    }
    // draw the picture


    private void predict(Ball a, double limit, double t) {
        // limit is a timePeriod
        if (a == null) return;

        // particle-particle collisions
        for (int i = 0; i < balls.length; i++) {
            if (t + timeToHit(a, balls[i]) <= limit + 1e-9)
                pq.insert(new Event((t + timeToHit(a, balls[i])), a, balls[i]));
        }

        // particle-wall collisions
        if (t + timeToHitVerticalWall(a) <= limit + 1e-9) pq.insert(new Event((t + timeToHitVerticalWall(a)), a, null));
        if (t + timeToHitHorizontalWall(a) <= limit + 1e-9)
            pq.insert(new Event((t + timeToHitHorizontalWall(a)), null, a));
    }
    // if a hit can happen before limit, insert this to the priority queue


    public void simulate(double limit, Ball[] balls) {//simulate works during [t, limit]
        double t = 0;// simulate clock time

        pq = new MinPQ<Event>();
        // initialize PQ with collision events and redraw event

        for (int i = 0; i < balls.length; i++) {
            predict(balls[i], limit, t);
            //predict all balls' hit time during a timePeriod
        }

        pq.insert(new Event(0, null, null));
        // redraw event

        // the main event-driven simulation loop
        while (!pq.isEmpty()) {

            Event e = pq.delMin();
            if (!e.isValid()) continue;

            // two balls in the recent event
            Ball a = e.a;
            Ball b = e.b;

            // physical collision, so update positions, and then simulation clock
            for (int i = 0; i < balls.length; i++)
                balls[i].move(e.time - t);
            t = e.time;

            // process event
            if (a != null && b != null) bounceOff(a, b);                      // particle-particle collision
            else if (a != null && b == null) bounceOffVerticalWall(a);        // particle-wall collision
            else if (a == null && b != null) bounceOffHorizontalWall(b);      // particle-wall collision
            else if (a == null && b == null) redraw(timePeriod, t, length);   // redraw event


            predict(a, limit, t);
            predict(b, limit, t);
            // update the priority queue with new collisions involving a or b
        }

        pq = null;
        System.gc();
    }


    //during a timePeriod(dt），update information of all balls
    public void increaseTime(double dt)
    {
        Vector[] force = new Vector[ballNum];
        for (int i = 0; i < ballNum; i++) {
            force[i] = new Vector(0, 0);
        }

        // if there are not too many balls, simply use brute force
        if (ballNum <= 7) {
            for (int i = 0; i < ballNum; i++) {
                for (int j = 0; j < ballNum; j++) {
                    if (i != j) {
                        force[i] = force[i].plus(forceFrom(balls[i], balls[j]));
                    }
                }
//                System.out.println(force[i]);//
            }
            // for balls[i], its has a net gravity force of force[i]
        }

        // if there are too many balls, use BHTree to calculate force[]
        else{
            Quad quad = new Quad(length * 0.5, length * 0.5, length);
            BHTree tree = new BHTree(quad);

            // build the Barnes-Hut tree
            for (int i = 0; i < ballNum; i++) {
                if (balls[i].in(quad)) {
                    tree.insert(balls[i]);
                }
            }

            for (int i = 0; i < ballNum; i++) {
                balls[i].restForce();
                tree.updateForce(balls[i]);
            }

            for (int i = 0; i < ballNum; i++) {
                force[i] = balls[i].force;
                }

            }

            for (int i = 0; i < ballNum; i++) {
                balls[i].moveGravity(force[i], dt, balls[i].ballMass);
            }
            // move the balls by gravities
        simulate(timePeriod, balls);
    }



        public static void main (String[]args){
        Project nBodySimulation = new Project();
        double time = 0;//just a time simulation, has no relationship with real word time
        double specialTime ;
            if(nBodySimulation.outNum != 0){
                specialTime = nBodySimulation.outputBalls[nBodySimulation.outNum - 1][0];//need to output balls' position at this time
            }
            else{
                specialTime = 666e100;
            }

            // refresh the system every this small time, consider velocities don't change during every timePeriod
            double timePeriod = 0.25;          //not too many balls, brute force
            if(nBodySimulation.ballNum>7){
                timePeriod = 0.005;
            }                                  //too many balls, BHTree

            if (nBodySimulation.out.equals("terminal")) {
                // terminal
                int i = 0;
                while (time <= specialTime) {
                    if(nBodySimulation.outNum != 0) {
                        while (i < nBodySimulation.outNum) {
                            if (time >= nBodySimulation.outputBalls[i][0]-timePeriod/2 && time <= nBodySimulation.outputBalls[i][0] + timePeriod/2) {
                                System.out.println(nBodySimulation.output((int) nBodySimulation.outputBalls[i][1]));
                                i++;
                            } else {
                                break;
                            }
                        }
                    }
                    time = add(time, timePeriod);
                    nBodySimulation.increaseTime(timePeriod);// refresh balls behaviours

                }
            }
        else {
            //gui
            StdDraw.enableDoubleBuffering();
            StdDraw.setCanvasSize(666, 666);
            int i = 0;
            while (true) {

                if(nBodySimulation.outNum != 0) {
                    while (i < nBodySimulation.outNum) {
                        if (time == nBodySimulation.outputBalls[i][0]) {
                            System.out.println(nBodySimulation.output((int) nBodySimulation.outputBalls[i][1]));
                            i++;
                        } else {
                            break;
                        }
                    }
                }

                time = add(time, timePeriod);
//                System.out.println(time);
                nBodySimulation.increaseTime(timePeriod);
            }
        }
    }
}
