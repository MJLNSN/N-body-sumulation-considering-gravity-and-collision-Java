class BHTree {

    // threshold value
    private final double Theta = 0.5;

    public Ball ball;// ball or aggregate ball stored in this node树中原有的球
    public Quad quad;// square region that the tree represents
    public BHTree NW;// tree representing northwest quadrant
    public BHTree NE;// tree representing northeast quadrant
    public BHTree SW;// tree representing southwest quadrant
    public BHTree SE;// tree representing southeast quadrant


    public BHTree(Quad q) {
        this.quad = q;//create a brand-new quad q in BHTree
        this.ball = null;
        this.NW = null;
        this.NE = null;
        this.SW = null;
        this.SE = null;
    }
    //constructor:creates a new empty Barnes-Hut tree

    public void insert(Ball b) {

        // if this node does not contain a body, put b here
        if (ball == null) {
            ball = b;
            return;
        }

        // internal node
        if (!isExternal()) {
            ball = ball.plus(b);
            // update the center-of-mass and total mass, put b into ball, recursively insert Body b into the appropriate quadrant
            putBall(b);
        }

        // external node
        else {
            // subdivide the region further by creating four children
            NW = new BHTree(quad.NW());
            NE = new BHTree(quad.NE());
            SE = new BHTree(quad.SE());
            SW = new BHTree(quad.SW());

            // recursively insert both this body and Body b into the appropriate quadrant
            putBall(this.ball);
            putBall(b);

            // update the center-of-mass and total mass
            ball = ball.plus(b);
        }
    }
    //insert a ball to a tree

    private void putBall(Ball b) {// put b in the appropriate quadrant
        if (b.in(quad.NW()))
            NW.insert(b);
        else if (b.in(quad.NE()))
            NE.insert(b);
        else if (b.in(quad.SE()))
            SE.insert(b);
        else if (b.in(quad.SW()))
            SW.insert(b);
    }

    public boolean isExternal() {
        // a node is external iff all four children are null
        boolean i=new Boolean(SW==null);
        return (SW == null &&NE== null && NW == null &&  SE == null);
    }

    public void updateForce(Ball b) {
        // the force of ball by b
        if (ball == null || b.equals(ball)) {
            return;
        }

        // if the current node is external, update net force acting on b

        if (isExternal())//ball is external
        {//update net force acting on ball
           b.addForce(ball);

        }
        // for internal nodes
        else {
            // width of region represented by internal node
            double s = quad.length();

            // distance between Body b and this node's center-of-mass
            double d = ball.distanceTo(b);

            if ((s / d) < Theta) {
                // b is far away
                b.addForce(ball);
                }
                // recurse on each of current node's children
            else {
                NW.updateForce(b);
                NE.updateForce(b);
                SW.updateForce(b);
                SE.updateForce(b);
            }
        }
    }
}

