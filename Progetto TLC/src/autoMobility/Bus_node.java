/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autoMobility;

/**
 *
 * @author afsantamaria
 */
public class Bus_node {
    double x;
    double y;
    int id;
    int verso = -1;

    public Bus_node(double x, double y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public int getVerso() {
		return verso;
	}

	public void setVerso(int verso) {
		this.verso = verso;
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
    
}
