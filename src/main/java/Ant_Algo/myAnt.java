package Ant_Algo;

public class myAnt extends BaseAnt{

    @Override
    void calcFitness(){
        double result = 0;
        for(int i = 0; i < position.size(); i++)
            result -= Math.pow(position.get(i),2);
        fitness = result;
    }
}
