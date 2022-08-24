package Bee_algorithm;

public class Main {
    public static void main(String[] args) {
        mybee bee = new mybee();
        int scoutbeecount = 300;
        int selectedbeecount = 20;
        int bestbeecount = 30;
        int selsitescount = 15;
        int bestsitescount = 10;
        int runcount = 1;
        int maxiteration = 20000;
        int max_func_counter = 10;
        var coef = bee.getListOfReducedCoef();


        for(int i = 0; i <runcount;i++){
            hive current_hive = new hive(scoutbeecount,selectedbeecount,bestbeecount,
                    selsitescount,bestsitescount, bee.getStartList());

            double best_func = -1.0e9;
            int func_counter = 0;
            for(int j =0; j< maxiteration;j++){
                current_hive.nextIteration();
                if(current_hive.getBest_fitness() != best_func){
                    best_func = current_hive.getBest_fitness();
                    func_counter = 0;
                    System.out.println("iteration: " + (j+1));
                    System.out.println("Best position: ");
                    for(var s : current_hive.getBest_position()){
                        System.out.print(s + ", ");
                    }
                    System.out.println();
                    System.out.println("Best fitness: " + current_hive.getBest_fitness());
                }
                else{
                    ++func_counter;
                    if(func_counter == max_func_counter){
                        for(int k = 0; k< current_hive.range.size();k++){
                            current_hive.range.set(k,
                                    current_hive.range.get(k)*coef.get(k));
                        }
                        func_counter = 0;
                        System.out.println("iteration: " + (j+ 1));
                        System.out.println("New range: ");
                        for(var s : current_hive.getRange()){
                            System.out.print(s + ", ");
                        }
                        System.out.println();
                        System.out.println("Best position: ");
                        for(var s : current_hive.getBest_position()){
                            System.out.print(s + ", ");
                        }
                        System.out.println();
                        System.out.println("Best fitness: " + current_hive.getBest_fitness());
                    }
                }
            }


        }


    }
}
