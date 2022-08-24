package Bee_algorithm;

import java.util.ArrayList;

public class floatbee {
    public ArrayList<Double> position;
    public ArrayList<Double> minVal;
    public ArrayList<Double> maxVal;
    public double fitness = 0.0;

    void calcFitness(){};


    public int sort(floatbee other) {
        return Double.compare(other.fitness,this.fitness);
    }


    public boolean otherpatch(ArrayList<mybee> bee_list, ArrayList<Double> range_list){
        if(bee_list.size() == 0){
            return true;
        }
        for(var bee : bee_list){
            var pos = bee.getPosition();
            for(int i =0; i < pos.size();i++){
                if(Math.abs(this.position.get(i) - pos.get(i)) > range_list.get(i)){
                    return true;
                }
            }
        }
        return false;
    }


    public void GoTo(ArrayList<Double> otherPos, ArrayList<Double> range_list){
        ArrayList<Double> new_pos = new ArrayList<>();
        for(int i =0; i<otherPos.size();i++){
            new_pos.add(otherPos.get(i)+ (Math.random() * (range_list.get(i) + Math.abs(range_list.get(i)) + 1))
                    -range_list.get(i));
        }
        this.position = new_pos;
        this.checkPosition();
        this.calcFitness();
    }


    public void goToRandom(){
        for(int i = 0 ; i< position.size();i++){
            position.set(i,(Math.random()*(maxVal.get(i) - minVal.get(i) + 1)+minVal.get(i)));
        }
        this.checkPosition();
        this.calcFitness();
    }


    public void checkPosition(){
        for(int i =0; i < this.position.size();i++){
            if(this.position.get(i) < this.minVal.get(i)){
                this.position.set(i,this.minVal.get(i));
            }
            else if(this.position.get(i) > this.maxVal.get(i)){
                this.position.set(i,this.maxVal.get(i));
            }
        }
    }


    public ArrayList<Double> getPosition(){
        return new ArrayList<>(position);
    }

}
