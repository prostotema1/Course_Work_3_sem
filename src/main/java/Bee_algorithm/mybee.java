package Bee_algorithm;

import java.util.ArrayList;

public class mybee extends floatbee{
    int dimension = 4;

    mybee(){
        maxVal = new ArrayList<>();
        minVal = new ArrayList<>();
        position = new ArrayList<>();
        for(int i = 0 ; i < dimension; i++){
            maxVal.add(150.0);
            minVal.add(-150.0);
        }
        for(int i = 0 ; i< dimension;i++){
            position.add(Math.random()*(maxVal.get(i) - minVal.get(i) + 1)+minVal.get(i));
        }
        this.calcFitness();
    }

    @Override
    public void calcFitness(){
        fitness = 0.0;
        for(var s : position){
            fitness-=s*s;
        }

    }


    public ArrayList<Double> getListOfReducedCoef(){
        ArrayList<Double> Reduced = new ArrayList<>();
        for(int i = 0; i < dimension;i++){
            Reduced.add(0.98);
        }
        return Reduced;
    }

    public ArrayList<Double> getStartList(){
        ArrayList<Double> begin_coord = new ArrayList<>();
        for(int i =0 ; i < dimension;i++){
            begin_coord.add(150.0);
        }
        return begin_coord;
    }

}
