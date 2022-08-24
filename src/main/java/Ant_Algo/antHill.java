package Ant_Algo;

import java.util.ArrayList;
import java.util.Collections;

public class antHill {

    static class Field {
        public ArrayList<Double> coords;
        public double pheromone;
        public double age = Params.regionAge;
    }

    static class Pair {
        public int numberOfCell;
        public Field pheromoneSpot;

        Pair(int numberOfCell,Field field){
            this.numberOfCell = numberOfCell;
            this.pheromoneSpot = field;
        }
    }

    private final int countRegions;
    private final ArrayList<ArrayList<Double>> regions;
    private ArrayList<ArrayList<Field>> dotsAndPheromone;

    private final int localFinders;
    private final int globalFinders;

    private final int dimension = 2;
    private ArrayList<myAnt> antHill;
    private double distance;
    public double best_fitness = Double.NEGATIVE_INFINITY;
    private int current_iteration = 1;
    private int numberOfConnentions;

    antHill(double lowerBound, double upperBound,int localFinders, int globalFinders){
        this.countRegions = localFinders + globalFinders;
        regions = new ArrayList<>(localFinders + globalFinders);

        createSearchAreas(lowerBound,upperBound);
        initializePointsInSearchAreas();

        this.localFinders = localFinders;
        this.globalFinders = globalFinders;



        antHill = new ArrayList<>(localFinders + globalFinders);
        for(int i =0 ; i < localFinders+globalFinders;i++){
            var ant = new myAnt();
            ant.position = dotsAndPheromone.get(i).get(0).coords;
            ant.Feromonecell = i;
            antHill.add(ant);
        }
        getNumberOfConnections();
        CalcFitness(); // Вычисляем значение фитнесс функции
        antHill.sort(BaseAnt::sort);
    }

    /**
     Алгоритм запускается здесь
     */
    public void process() {
        int counter_of_stagnation = 0;
        while (current_iteration <= Params.IterationCounter) {

            CalcFitness(); // Вычисляем значение фитнесс функции
            antHill.subList(0,globalFinders).sort(BaseAnt::sort);
            antHill.subList(globalFinders,globalFinders + localFinders).sort(BaseAnt::sort);
            //System.out.println("Current fitnes of last ant " + antHill.get(localFinders + globalFinders-1).fitness);
            /*System.out.print("Current position of ant_max: ( ");
            for(var s: antHill.get(globalFinders + localFinders -1).position){
                System.out.print(s + ",");
            }
            System.out.println(")");*/
            if(current_iteration > 100){
                // System.out.println("Current fitnes of last ant " + antHill.get(localFinders + globalFinders-1).fitness);
                if(antHill.get(globalFinders + localFinders -1).fitness ==  best_fitness && counter_of_stagnation == Params.stagnationCounter){
                    System.out.println("Reached stagnation point on " + current_iteration + " iteration");
                    break;
                }
                else if(antHill.get(globalFinders + localFinders -1).fitness == best_fitness){
                    ++counter_of_stagnation;
                }
                else{
                    if(antHill.get(globalFinders+localFinders-1).fitness > best_fitness){
                        best_fitness = antHill.get(globalFinders + localFinders -1).fitness;
                    }
                    counter_of_stagnation = 0;
                }
            }

            mutation(); // Мутируют 0,9*S^g первых муравьев
            crossOver();// Оставшиеся 0,1*S^g проходят через кроссовер
            newPheromone();// Расставляем новые феромонные точки

            getMidLengthBetweenAnts(); // Вычисляем среднее расстояние между муравьями
            getNewLocalSearchArea();

            ArrayList<Pair> areasToDelete = new ArrayList<>();
            for (int i =0; i < dotsAndPheromone.size(); i++) {
                for (int j =0; j < dotsAndPheromone.get(i).size();j++) {
                    dotsAndPheromone.get(i).get(j).pheromone = setNewPheromone(dotsAndPheromone.
                            get(i).get(j).pheromone);

                    areasToDelete.add(new Pair(i,dotsAndPheromone.get(i).get(j)));

                }
            }
            deletePheromones(areasToDelete);
            areasToDelete.clear();

            ++current_iteration;

        }
    }

    /**
     Инициализируем случайные регионы для создания первых |S| точек
     */
    private void createSearchAreas(double lowerBound, double upperBound){
        for(int i =0; i < countRegions; i++){
            ArrayList<Double> coords = new ArrayList<>(dimension);
            for(int j = 0; j < dimension; j++){
                coords.add(Math.random()*(upperBound - lowerBound+1)+ lowerBound);
            }
            regions.add(coords);
        }
    }

    /**
     Создаем |S| точек поиска
     */
    private void initializePointsInSearchAreas(){
        dotsAndPheromone = new ArrayList<>();
        ArrayList<Double> coords;
        ArrayList<Field> field;
        for(int i = 0; i < countRegions; i++){
            coords = new ArrayList<>();
            field = new ArrayList<>();
            for(int j = 0; j < dimension; j++){
                coords.add(Math.random() * (regions.get(i).get(j)+Params.r - (regions.get(i).get(j) - Params.r) + 1)
                        -Params.r+regions.get(i).get(j));
            }
            var pher = new Field();
            pher.coords = coords;
            pher.pheromone = Params.initPheromoneValue;
            field.add(pher);
            dotsAndPheromone.add(field);
        }


    }

    /**
     Обновляем феромонный след
     */
    private double setNewPheromone(double currentPheromone){
        if(currentPheromone < Params.minPheromoneValue){
            return 0.0;
        }
        return currentPheromone * Params.stabilityCoefficient;
    }

    /**
     Оператор мутации
     */
    private void mutation(){
        var Gauss = GaussMutator(mutationStepReduction(current_iteration));
        for(int i = 0;i <= Math.floor(0.9 * globalFinders);i++){
            var ant = antHill.get(i);
            if(Math.random() > Params.mutationProbability){
                var coords = ant.getPosition();
                for(int j = 0; j < dimension; j++){
                    coords.set(j, coords.get(j) + Gauss);
                }
                ant.position = coords;
                ant.Feromonecell = antHill.get(i).Feromonecell;
            }


        }
    }

    /**
     Гауссов Мутатор
     */
    private double GaussMutator(double sigma){
        java.util.Random random = new java.util.Random();
        return sigma*random.nextGaussian();
    }

    /**
    Редуцирование шага мутации
     */
    private double mutationStepReduction(int t){
        var tt = 1.0-(double)t/Params.IterationCounter;
        return Params.maxMutationStep*
                (1- Math.pow(Math.random() + 0.000000001,Math.pow(tt,Params.nonLinearParam)));
    }

    /**
     Оператор кроссовера
     */
    private void crossOver(){
        ArrayList<myAnt> children = new ArrayList<>();
        int begin_index = (int)Math.floor(0.9 * globalFinders);
        for(int i = begin_index; i < globalFinders ; i++){
            var par1 = (int) (Math.random() * (globalFinders - begin_index) +begin_index);
            var par2 = (int) (Math.random() * (globalFinders - begin_index) +begin_index);
            while(par2 == par1){
                par2 = (int) (Math.random() * (globalFinders - begin_index) +begin_index);
            }
            var parent1 = antHill.get(par1);
            var parent2 = antHill.get(par2);
            var child = new myAnt();
            child.position = new ArrayList<>(dimension);
            var u = Math.random();

            for(int j =0; j< dimension; j++){
                child.position.add(u* parent1.position.get(j)+
                        (1-u)*parent2.position.get(j));
            }
            child.Feromonecell = antHill.get(i).Feromonecell;
            children.add(child);
        }
        for(int i = 0; i < children.size();i++){
            antHill.set(i+begin_index, children.get(i));
        }


    }

    /**
     Инициализируем феромон в новых точках феромонного следа
     */
    private void newPheromoneSpots(int numberOfAnt){
        Field field = new Field();
        field.coords = antHill.get(numberOfAnt).position;
        field.pheromone = Params.initPheromoneValue;
        field.age = Params.regionAge;
        dotsAndPheromone.get(antHill.get(numberOfAnt).Feromonecell).add(field);
    }

    /**
     Получаем центр тяжести текущих феромонных точек
     */
    private ArrayList<Double> middleCorrds(myAnt ant_i){
        ArrayList<Double> res = new ArrayList<>();
        for(int i =0; i < antHill.size(); i++){
            for(int j =0; j < dotsAndPheromone.get(antHill.get(i).Feromonecell).size();j++){
                var ans = interestToDots(ant_i,i,j);
                if(j == 0 && i ==0){
                    ArrayList<Double> new_coords = new ArrayList<>();
                    for(int k =0; k < dimension; k++){
                        new_coords.add(dotsAndPheromone.get(antHill.get(i).Feromonecell).get(j).coords.get(k));
                        new_coords.set(k,new_coords.get(k)*ans);
                    }
                    res = new_coords;
                }
                else{
                    for(int k = 0; k < dimension; k++){
                        res.set(k, res.get(k) + dotsAndPheromone.get(antHill.get(i).Feromonecell).get(j).coords.get(k)*ans);
                    }
                }
            }
        }
        return res;
    }

    private double interestToDots(myAnt ant_i,int numberOfSpot,int numberOfDot){
        double znamenatel;
        double ans = 0;
        for (var myAnt : antHill) {
            for (int j = 0; j < dotsAndPheromone.get(myAnt.Feromonecell).size(); j++) {
                ans += interestToDot(ant_i, myAnt.Feromonecell, j);
            }
        }
        znamenatel = 1.0/ans;
        return interestToDot(ant_i,antHill.get(numberOfSpot).Feromonecell,numberOfDot) * znamenatel;
    }

    private double interestToDot(myAnt ant_i, int numberOfSpot,int numberOfStep){
        var distanceBetweenAntAndPheromoneSpot = 0.0;
        for(int i = 0; i < dimension; i++){
            distanceBetweenAntAndPheromoneSpot += Math.pow(ant_i.position.get(i) -
                    dotsAndPheromone.get(numberOfSpot).get(numberOfStep).coords.get(i),2);
        }

        distanceBetweenAntAndPheromoneSpot = Math.exp(-Math.sqrt(distanceBetweenAntAndPheromoneSpot));
        return distance*distanceBetweenAntAndPheromoneSpot*
                dotsAndPheromone.get(numberOfSpot).get(numberOfStep).pheromone;
    }

    /**
     * Получаем количество связей между каждым муравьем в муравейнике для рассчета среднего расстояния между муравьями
     */
    private void getNumberOfConnections(){
        int counter = 0;
        for(int i =0; i < antHill.size()-1;i++){
            counter += antHill.size()-1-i;
        }
        numberOfConnentions = counter;
    }

    /**
     * Рассчитываем среднее расстояние между муравьями на текущей итерации
     */
    private void getMidLengthBetweenAnts(){
        double length = 0;
        for(int i = 0; i < antHill.size()-1;i++){
            double temp = 0;
            for(int j =i+1; j< antHill.size();j++){
                double current_length = 0;
                for(int k =0; k < dimension; k++){
                    current_length += Math.pow(antHill.get(i).position.get(k) - antHill.get(j).position.get(k),2);
                }
                current_length = Math.sqrt(current_length);
                temp += current_length;
            }
            length += temp;
        }
        this.distance = length /(2.0*numberOfConnentions);
    }

    /**
     Обновляем позицию муравья из S^l
     */
    private boolean getNewPose(myAnt ant_i, ArrayList<Double> new_vec){
        var coords = ant_i.position;
        var pheromone_cell = ant_i.Feromonecell;
        ArrayList<Double> res = new ArrayList<>();
        var pos = dotsAndPheromone.get(pheromone_cell).size()-1;
        var multip = Math.abs(Params.beginLocalFindStep - Params.LocalFindIncrement *
                dotsAndPheromone.get(pheromone_cell).
                        get(pos).age);
        for(int i = 0;i<dimension;i++){
            var tmp = coords.get(i) - new_vec.get(i);
            res.add(coords.get(i) + tmp*multip);
        }
        var ant = new myAnt();
        ant.position = res;
        ant.calcFitness();
        if(ant_i.fitness < ant.fitness){
            dotsAndPheromone.get(pheromone_cell).get(dotsAndPheromone.get(pheromone_cell).size()-1).age++;
            Field field = new Field();
            field.coords = ant.position;
            field.age = Params.regionAge;
            field.pheromone = Params.initPheromoneValue;
            ant_i.position = field.coords;
            dotsAndPheromone.get(pheromone_cell).add(field);
            return true;
        }
        else{
            dotsAndPheromone.get(pheromone_cell).get(dotsAndPheromone.get(pheromone_cell).size()-1).age--;
            return false;
        }


    }

    /**
     * Пересчитываем значение фитнес-функций для каждого муравья
     */
    private void CalcFitness(){
        for(var s: antHill){
            s.calcFitness();
        }
    }

    /**
     * Обновляем позиции муравьев из S^l
     */
    private void getNewLocalSearchArea(){
        var subArr = antHill.subList(globalFinders,globalFinders+localFinders);
        var flag = false;
        Collections.shuffle(subArr);
        for (var myAnt : subArr) {
            var vector = middleCorrds(myAnt);
            var fl = getNewPose(myAnt, vector);
            if (fl) {
                flag = fl;
            }
        }
        if(!flag){
            goToRandomSpot(subArr.get((int) (Math.random() * (subArr.size()))));
        }
    }

    /**
     * Метод, чтобы отправить муравья из S^l на случайную точку в случае неудачного локального поиска
     */
    private void goToRandomSpot(myAnt ant){
        var new_coords = new ArrayList<>(ant.position);
        for(int i =0; i < new_coords.size();i++){
            new_coords.set(i,new_coords.get(i) + Math.random() * Params.r);
        }
        var new_ant = new myAnt();
        new_ant.position = new_coords;
        new_ant.calcFitness();
        if(new_ant.fitness > ant.fitness){
            new_ant.Feromonecell = ant.Feromonecell;
            ant = new_ant;
            Field field = new Field();
            field.coords = ant.position;
            field.age = Params.regionAge;
            field.pheromone = Params.initPheromoneValue;
            dotsAndPheromone.get(ant.Feromonecell).add(field);
        }
    }

    /**
     * Удаляем точки из феромонного следа, если значение феромона в ней = 0
     */
    private void deletePheromones(ArrayList<Pair> to_delete){
        if(to_delete.size()  != 0) {
            for (var s : to_delete) {
                if(dotsAndPheromone.get(s.numberOfCell).size() != 1) {
                    dotsAndPheromone.get(s.numberOfCell).remove(s.pheromoneSpot);
                }
            }
        }
    }

    /**
     * Расставляем новые феромонные точки для муравьев из S^g после операторов мутации и кроссовера
     */
    private void newPheromone(){
        for(int i =0; i < globalFinders;i++){
            newPheromoneSpots(i);
        }
    }


    public static void main(String[] args) {
        var global_best_fitness = Double.NEGATIVE_INFINITY;
        for(int i =0 ; i < 100; i++){
            System.out.println("Iteration #" + i);
            var ss =new antHill(-100.0,100.0,10,15);
            ss.process();
            System.out.println("Best fitness: " + ss.best_fitness);
            if(global_best_fitness < ss.best_fitness){
                global_best_fitness = ss.best_fitness;
            }
            System.out.println();
        }
        System.out.println("Best fitness through all iterations: " + global_best_fitness);

    }

}
