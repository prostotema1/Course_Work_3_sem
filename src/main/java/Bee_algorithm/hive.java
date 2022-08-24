package Bee_algorithm;

import java.util.ArrayList;

public class hive {
    private ArrayList<Double> best_position;
    public ArrayList<Double> range;
    private ArrayList<mybee> bestsites = new ArrayList<>();
    private ArrayList<mybee> selSites = new ArrayList<>();
    private double best_fitness;
    private int scoutbeecount;
    private int selectedbeecount;
    private int bestbeecount;
    private int selsitescount;
    private int bestsitescount;
    private ArrayList<mybee> swarm = new ArrayList<>();

    hive(int scoutbeecount, int selectedbeecount, int bestbeecount, int selsitescount , int bestsitescount ,
         ArrayList<Double> range_list){
        this.scoutbeecount = scoutbeecount;
        this.selectedbeecount = selectedbeecount;
        this.bestbeecount = bestbeecount;
        this.selsitescount = selsitescount;
        this.bestsitescount = bestsitescount;
        this.range = range_list;
        this.best_position = null;

        int beecount = scoutbeecount + selectedbeecount * selsitescount + bestbeecount * bestsitescount;

        for(int i = 0; i < beecount;i++){
            swarm.add(new mybee());
        }

        this.swarm.sort(floatbee::sort);
        this.best_position = this.swarm.get(0).getPosition();
        this.best_fitness = this.swarm.get(0).fitness;
    }

    public int sendBees(ArrayList<Double> position,int index, int count){
        for(int i =0; i < count; i++){
            if(index == this.swarm.size()){
                break;
            }

            var current_bee = swarm.get(i);

            if(!bestsites.contains(current_bee) && !selSites.contains(current_bee)){
                current_bee.GoTo(position,this.range);
            }
            ++index;
        }
        return index;
    }

    public void nextIteration(){
        bestsites.clear();
        bestsites.add(swarm.get(0));
        int current_index = 1;

        for(var bee : swarm.subList(current_index, swarm.size())){
            if(bee.otherpatch(bestsites,range)){
                bestsites.add(bee);
            }
            if(bestsites.size() == bestsitescount){
                break;
            }
            ++current_index;
        }

        this.selSites = new ArrayList<>();

        for(var bee: swarm.subList(current_index, swarm.size())){
            if(bee.otherpatch(bestsites,range) && bee.otherpatch(selSites,range)){
                selSites.add(bee);
                if(selSites.size() == selsitescount){
                    break;
                }
            }
        }
        int bee_index = 1;

        for(var best_bee: bestsites){
            bee_index = sendBees(best_bee.getPosition(),bee_index,bestbeecount);
        }

        for(var sel_bee: selSites){
            bee_index = sendBees(sel_bee.getPosition(),bee_index,selectedbeecount);
        }

        for(var current_bee: swarm.subList(bee_index,swarm.size())){
            current_bee.goToRandom();
        }

        swarm.sort(floatbee::sort);
        best_position = swarm.get(0).getPosition();
        best_fitness = swarm.get(0).fitness;
    }

    public double getBest_fitness() {
        return best_fitness;
    }

    public ArrayList<Double> getBest_position() {
        return best_position;
    }

    public ArrayList<Double> getRange() {
        return range;
    }
}