import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Vector;

import java.awt.*;

class Ball {
    public double xCor; // x坐标
    public double yCor; // y坐标
    public double vx; // 初始x方向速度
    public double vy; // 初始y方向速度
    public double ballRadius; // 小球半径
    public double ballMass; // 小球质量
    int r;
    int g;
    int b;
    public double vxt;//某一时间x方向速度
    public double vyt;//某一时间y方向速度
    public double xForce;//某一时间x方向合力
    public double yForce;//某一时间y方向合力
     public int count;

    public double fx;
    public double fy;

    public static final double G = 6.673e-6;//G，常数
    double[] xyCor = {xCor, yCor};//辅助用
    double[] velo = {vxt, vyt};//辅助用
    double[] acce = {xForce/ballMass, yForce/ballMass};//辅助用
   // private static final double INFINITY = Double.POSITIVE_INFINITY;
    double[] xZhou = {0,1};
    double[] yZhou = {1,0};
    double[] heli = {fx, fy};//辅助用
    //double[] acce = {xForce/ballMass, yForce/ballMass};//辅助用

    public Vector x = new Vector(xZhou);
    public Vector y = new Vector(yZhou);
    public Vector position = new Vector(xyCor);//使用向量表示小球位置
    public Vector velocity = new Vector(velo);//使用向量表示小球速度
    public Vector acceleration = new Vector(acce);//使用向量表示小球加速度
    public Vector collisionF = new Vector(heli);//使用向量表示小球碰撞过程的弹力
    public Vector force=new Vector(xForce,yForce);
    double timePeriod = 0.2;


    public Ball(Vector position, Vector velocity,Vector acceleration, double ballRadius, double ballMass, int r, int g, int b) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.ballRadius = ballRadius;
        this.ballMass = ballMass;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    //constructor, useless but elegant
    //小球具有的属性包扩：位置、速度、加速度、半径、质量、颜色



    public int count(){
        return count;
    }

    public void move(double dt) {
//        rx += vx * dt;
//        ry += vy * dt;
        this.position = new Vector(this.position.cartesian(0)+this.velocity.cartesian(0)*dt, this.position.cartesian(1)+this.velocity.cartesian(1)*dt);
    }

    public void moveGravity(Vector force, double dt, double ballMass) { // 在极微小时间内，更新引力作用后的速度和位置
        Vector acceleration = force.scale(1/ballMass);
        velocity = velocity.plus(acceleration.scale(dt));
        position = position.plus(velocity.scale(dt));
    }
    //during dt,also known as timePeriod, consider the velocity don't change
    //update velocity and position
    //calculate the movement during a timePeriod dt


    public Ball plus(Ball b) {
        double m = this.ballMass + b.ballMass;
        Vector newPosition = (this.position.scale(this.ballMass).plus(b.position.scale(b.ballMass))).scale(1/m);
        return new Ball(position, velocity, acceleration, ballRadius, ballMass, r, r, r);
    }
    //a ball that represents the centre of mass of two
    //just focus on its position to calculate the net force in one ball, no need of other arguments like velocity, colour or anything else


    public double distanceTo(Ball b) {
        return (this.position.minus(b.position)).magnitude();
    }

    public boolean in(Quad q) {
        return q.contains(this.position.cartesian(0), this.position.cartesian(1));
    }
    //Returns true if the body is in quadrant q, else false.

    public void update(double dt) {
        this.velocity= this.velocity.plus(new Vector(dt * this.force.cartesian(0) / this.ballMass,dt * this.force.cartesian(1) / this.ballMass));
        this.position=this.position.plus(new Vector(dt * this.velocity.cartesian(0),dt * this.velocity.cartesian(1)));
    }

    public void restForce() {
        force=new Vector(0,0);
    }

    public double timeToHit(Ball b2) {
        if (this == b2) return 100;
        double dx = this.position.cartesian(0) - b2.position.cartesian(0);
        double dy = this.position.cartesian(1) - b2.position.cartesian(1);
        double dvx = this.velocity.cartesian(0) - b2.velocity.cartesian(0);
        double dvy = this.velocity.cartesian(1) - b2.velocity.cartesian(1);
        double dvdr = dx * dvx + dy * dvy;
        if (dvdr > 0) return 1;
        double dvdv = dvx * dvx + dvy * dvy;
        if (dvdv == 0) return 1;
        double drdr = dx * dx + dy * dy;
        double sigma = this.ballRadius + b2.ballRadius;
        double d = (dvdr * dvdr) - dvdv * (drdr - sigma * sigma);
        // if (drdr < sigma*sigma) StdOut.println("overlapping particles");
        if (d < 0) return 100;
        return -(dvdr + Math.sqrt(d)) / dvdv;
    }

    public void bounceOff( Ball b2) {
        double dx  = this.position.cartesian(0) - b2.position.cartesian(0);
        double dy  = this.position.cartesian(1) - b2.position.cartesian(1);
        double dvx = this.velocity.cartesian(0) - b2.velocity.cartesian(0);
        double dvy = this.velocity.cartesian(1) - b2.velocity.cartesian(1);
        double dvdr = dx*dvx + dy*dvy;             // dv dot dr
        double dist = b2.ballRadius + this.ballRadius;   // distance between particle centers at collison

        // magnitude of normal force
        double magnitude = 2 * b2.ballMass * this.ballMass * dvdr / ((b2.ballMass + this.ballMass) * dist);

        // normal force, and in x and y directions
        double fx = magnitude * dx / dist;
        double fy = magnitude * dy / dist;

        // update velocities according to normal force
        b2.vx += fx / b2.ballMass;
        b2.vy += fy / b2.ballMass;
        b2.velocity = new Vector(b2.vx,b2.vy);

        this.vx -= fx / this.ballMass;
        this.vy -= fy / this.ballMass;
        this.velocity = new Vector(this.vx,this.vy);

    }

    public void addForce(Ball b) {
        double G = 6.67e-11;//G，常数
        Ball a = this;
        Vector delta=b.position.minus(a.position);
        double dist = delta.magnitude();
        double F = (G * a.ballMass * b.ballMass) / (dist * dist);
        if(dist!=0) {
            a.force = a.force.plus(new Vector(F * delta.cartesian(0) / dist, F * delta.cartesian(1) / dist));
        }

    }


    public void draw1(int length) {

        StdDraw.setPenRadius(0.3);
        StdDraw.setPenColor(new Color(this.r, this.g, this.b));

        StdDraw.filledCircle(this.position.cartesian(0)/length, this.position.cartesian(1)/length, this.ballRadius/length);//原本不需要0.05*,但是因为stdDraw存在范围（0：1）
    }


    public String output(){
        return this.position.cartesian(0)+" "+ this.position.cartesian(1)+" "+ this.velocity.cartesian(0)+" "+ this.velocity.cartesian(1);
    }
}
