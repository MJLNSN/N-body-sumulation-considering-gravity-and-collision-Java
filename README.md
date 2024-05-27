# N-body-sumulation-considering-gravity-and-collision-Java
## Final Project for DSAA 

## Perforn a N Body Simulation

input1

terminal

100 

3

0.5 0.5 0 0 0.1 1000000 150 30 10

0.1 0.5 0 0.01 0.03 1 50 50 50

0.8 0.5 0 0.01 0.01 0.1 50 50 50

3

4 2

10 0

14 4


input2

gui

4

3

2.0 3.0 -0.0036712725360877 0.0 0.05 350000.0 50 50 50

1.1339745962155614 1.5 0.00183563626804385 -0.0031794152804680703 0.05 350000.0 50 50 50

2.8660254037844384 1.5 0.00183563626804385 0.0031794152804680703 0.05 350000.0 50 50 50

0

**There are 2 choices of line 1st: terminal and gui.** 

Terminal means you should not show a window.
Instead, you should print the required information in the terminal. gui means you should show a window.
The number of line 2nd indicates the side length of the square space, all the objects are supposed be in
the square space. For example, the second line contains 100, then the x and y coordinate of everything in
your space is in [0, 100].

The number of line 3rd num is the number of objects.
For each of the next num lines, there are 9 numbers represent the initial state of one object. The 9
numbers are:

• x-coordinate x
• y-coordinate y
• initial velocity in the x direction vx
• initial velocity in the y direction vy
• radius of the square radius
• mass of the square weight
• color of the square r g b.

The unit of x, y and radius is meter. The unit of vx and vy is meter/second. The unit of weight is
kilogram.

The objects are indexed. The first object is the 0th and the second object is the 1st,..., the last object is
num-1.

The number in the next line query indicates the number of querys. For each of the next query lines, there
are 2 numbers. The first is the time t in seconds and the second is index of the object. You should print a
line containing the x, y, vx, vy of the object at the time t seconds after the begining of the simulation.
