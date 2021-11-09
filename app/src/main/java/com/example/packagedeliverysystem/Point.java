package com.example.packagedeliverysystem;

import java.io.Serializable;

public class Point  implements Serializable{
    private double x,y;

    Point(){


    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }



    double dist(Point a) {
        return Math.sqrt(Math.pow(this.x-a.x, 2)+Math.pow(this.y-a.y, 2));
    }

    boolean isInside(Point a, Point b, Point c, Point d){
        double ab=a.dist(b);
        double bc=b.dist(c);
        double cd=c.dist(d);
        double da=d.dist(a);
        double ap=a.dist(this);
        double bp=b.dist(this);
        double cp=c.dist(this);
        double dp=d.dist(this);

        //rect
        double r=ab*bc;
        //triangle 1
        double s=(ab+ap+bp)/2;
        double t1=Math.sqrt(s*(s-ab)*(s-ap)*(s-bp));

        //triangle 2
        s=(bc+bp+cp)/2;
        double t2=Math.sqrt(s*(s-bc)*(s-bp)*(s-cp));

        //triangle 3
        s=(cd+cp+dp)/2;
        double t3=Math.sqrt(s*(s-cd)*(s-cp)*(s-dp));

        //triangle 4
        s=(da+dp+ap)/2;
        double t4=Math.sqrt(s*(s-da)*(s-dp)*(s-ap));

        return String.format("%.10g%n", (t1+t2+t3+t4)).equals(String.format("%.10g%n",r));
    }

    void printPoint(){
        System.out.println("("+x+", "+y+")");
    }

}
