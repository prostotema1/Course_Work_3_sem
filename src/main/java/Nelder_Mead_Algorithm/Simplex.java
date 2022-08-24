package Nelder_Mead_Algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Simplex {

    static class Pair{
        public ArrayList<Double> coords;
        public double value;
    }
    private double alpha = 1;
    private double beta = 0.5;
    private double gamma = 2;
    private double delta = 0.000000001;
    private Double lowerBound;
    private Double upperBound;
    private ArrayList<ArrayList<Double>> dots;
    private int dimension;
    private ArrayList<Pair> result;
    private ArrayList<Double> midCoord;

    Simplex(int dimension,double lowerBound,double upperBound){
        this.dimension = dimension;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        result = new ArrayList<>(dimension + 1);
        for(int i =0; i<dimension+1;i++){
            ArrayList<Double> coord = new ArrayList<>(dimension+1);
            var U = getU();
            for(int j=0; j < dimension;j++){
                if(i == 0){
                    coord.add(Math.random()*(this.upperBound + Math.abs(this.lowerBound)+1)
                            - this.upperBound);
                }
                else{
                    coord.add(result.get(i-1).coords.get(j)+0.05*U.get(j));
                }
            }
            var tmp = new Pair();
            tmp.value = getFuncValue(coord);
            tmp.coords = coord;
            result.add(tmp);
            U.clear();
        }

    }
    private ArrayList<Double> getU(){
        ArrayList<Double> U = new ArrayList<>(dimension);
        double length = 0;
        for(int i =0; i < dimension; i++){
            U.add(Math.random()*(this.upperBound + Math.abs(this.lowerBound)+1)
                    - this.upperBound);
            length += U.get(i)*U.get(i);
        }
        for(int i=0; i < dimension; i++){
            U.set(i,U.get(i)/Math.sqrt(length));
        }
        return U;
    }

   /* private ArrayList<Pair> getFuncValueInEveryPoint(){
        ArrayList<Pair> result = new ArrayList<>();
        for (var pair: dots) {
            Pair temp = new Pair();
            temp.coords = dot;
            temp.value = Math.pow(dot.get(0), 2) +
                    dot.get(1) * dot.get(0) + Math.pow(dot.get(1), 2)
                    - 6 * dot.get(0) - 9 * dot.get(1);
            result.add(temp);
        }
        return result;
    }*/

    private double getFuncValue(ArrayList<Double> x){
        return Math.pow(x.get(0), 2) +
                x.get(1) * x.get(0) + Math.pow(x.get(1), 2)
                - 6 * x.get(0) - 9 * x.get(1);
    }

    private ArrayList<Double> getMidCoord(){
        ArrayList<Double> result = new ArrayList<>(dimension);
        for(int i =0; i < dimension;i++){
            for(int j = 0; j < dimension;j++){
                if(i==0){
                    result.add(this.result.get(i).coords.get(j));
                }
                else{
                    result.set(j,this.result.get(i).coords.get(j) + result.get(j));
                }
            }
        }
        for(int i = 0; i < dimension;i++){
            result.set(i,result.get(i)/(dimension));
        }
        return result;
    }

    private boolean checkEnd(){
        double sum = 0;
        double value = getFuncValue(midCoord);
        for (var dot : this.result) {
            sum += Math.pow(dot.value - value, 2);
        }
        sum *= 1.0 /result.size();
        sum = Math.sqrt(sum);
        return sum <= delta;
    }

    private ArrayList<Double> reflection(){
        ArrayList<Double> last_top = result.get(dimension).coords;
        ArrayList<Double> new_top = new ArrayList<>(dimension);
        for(int i =0; i < dimension;i++){
            new_top.add(midCoord.get(i)+ alpha*(midCoord.get(i)-last_top.get(i)));
        }
        return new_top;
    }

    private ArrayList<Double> stretching(ArrayList<Double> reflected_coord){
        ArrayList<Double> result = new ArrayList<>(dimension);
        for(int i=0; i < dimension;i++){
            result.add(midCoord.get(i)-gamma*(midCoord.get(i)-reflected_coord.get(i)));
        }
        return result;
    }

    private ArrayList<Double> compression(){
        ArrayList<Double> result = new ArrayList<>(dimension);
        for(int i =0 ; i < dimension;i++){
            result.add(midCoord.get(i)+beta*(this.result.get(dimension).coords.get(i)-midCoord.get(i)));
        }
        return result;
    }

    private void reduction(){
        for(int i =1; i < result.size();i++){
            for(int j = 0; j < dimension;j++){
                result.get(i).coords.set(j,result.get(0).coords.get(j) +
                        0.5*(result.get(i).coords.get(j)-result.get(0).coords.get(j)));
            }
        }
    }

    public void print_answer(){
        var coord = result.get(dimension).coords;
        var ans = result.get(dimension).value;
        System.out.print("x = (");
        for (Double aDouble : coord) {
            System.out.print(aDouble + ",");
        }
        System.out.println("), f(x) = " + ans);
    }

    public ArrayList<Double> NelderMeadAlgorithm(){
        do{
            result.sort(Comparator.comparingDouble(a -> a.value));
            midCoord = getMidCoord();
            if(checkEnd()){
                return result.get(0).coords;
            }
            var reflected_top = reflection();
            var reflected_top_value = getFuncValue(new ArrayList<>(reflected_top));

            if(reflected_top_value < getFuncValue(result.get(0).coords)){
                var stretch = stretching(reflected_top);
                if(getFuncValue(stretch) < reflected_top_value){
                    var tmp = new Pair();
                    tmp.value = getFuncValue(stretch);
                    tmp.coords = stretch;
                    result.set(dimension, tmp);
                }
                else if((getFuncValue(stretch) >= reflected_top_value) ||
                        (result.get(0).value < reflected_top_value &&
                                reflected_top_value < result.get(1).value)
                || (result.get(1).value < reflected_top_value && reflected_top_value < result.get(dimension).value)
                ){
                    var tmp = new Pair();
                    tmp.value = reflected_top_value;
                    tmp.coords = reflected_top;
                    result.set(dimension, tmp);

                }
            }



            else {
                var compressed = compression();
                var compressed_value = getFuncValue(compressed);
                if(compressed_value < result.get(dimension).value){
                    var tmp = new Pair();
                    tmp.value = compressed_value;
                    tmp.coords = compressed;
                    result.set(dimension, tmp);
                }
                else{
                    reduction();
                }
            }




            //print_answer();

        }while(true);

    }

    public static void main(String[] args) {

        for(int j = 0; j < 10000; j++){
            Simplex sp = new Simplex(2,-2000,2000);
            var answer = sp.NelderMeadAlgorithm();
            System.out.print("x = (");
            for(int i =0; i < answer.size();i++){
                System.out.print(answer.get(i) + ",");
            }
            System.out.println("), f(x) = " + sp.getFuncValue(answer));}

    }



}

