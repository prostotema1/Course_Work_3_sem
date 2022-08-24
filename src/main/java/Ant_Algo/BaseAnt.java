package Ant_Algo;

import java.util.ArrayList;

public class BaseAnt {
    protected double fitness;
    public ArrayList<Double> position;
    public int Feromonecell;



    void calcFitness(){};

    public int sort(myAnt other){ return Double.compare(this.fitness,other.fitness);}

    public ArrayList<Double> getPosition(){
        return position;
    }
}
