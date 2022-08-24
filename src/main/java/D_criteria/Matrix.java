package D_criteria;

import java.util.Scanner;

public class Matrix {

    int dimension;
    double [] dots;
    double lower_bound;
    double higher_bound;
    double [][] matrix;
    double [] coef;


    private void inversion(double [][]A){
        double temp;
        double [][] E = new double [dimension][dimension];


        for (int i = 0; i < dimension; i++)
            for (int j = 0; j < dimension; j++)
            {
                E[i][j] = 0f;

                if (i == j)
                    E[i][j] = 1f;
            }

        for (int k = 0; k < dimension; k++)
        {
            temp = A[k][k];

            for (int j = 0; j < dimension; j++)
            {
                A[k][j] /= temp;
                E[k][j] /= temp;
            }

            for (int i = k + 1; i < dimension; i++)
            {
                temp = A[i][k];

                for (int j = 0; j < dimension; j++)
                {
                    A[i][j] -= A[k][j] * temp;
                    E[i][j] -= E[k][j] * temp;
                }
            }
        }

        for (int k = dimension - 1; k > 0; k--)
        {
            for (int i = k - 1; i >= 0; i--)
            {
                temp = A[i][k];

                for (int j = 0; j < dimension; j++)
                {
                    A[i][j] -= A[k][j] * temp;
                    E[i][j] -= E[k][j] * temp;
                }
            }
        }

        for (int i = 0; i < dimension; i++)
            System.arraycopy(E[i], 0, A[i], 0, dimension);

    }

    private double [][] find_inf_matrix(){
        double [][] inf_matrix = new double[dimension][dimension];
        double weight = 1.0/dimension;
        for(int k = 0; k < dimension;k++) {
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    inf_matrix[i][j] += Math.pow(dots[k]*coef[k], i) * Math.pow(dots[k]*coef[k], j)*weight;
                }
            }
        }
        return inf_matrix;
    }

    public void fill_dots(){
        for(int i =0; i < dimension;){
            double tmp = Math.ceil(1000*((Math.random() * (higher_bound - lower_bound+1)) + lower_bound))/1000;
            if(check_value(tmp,i)){
                dots[i] = tmp;
                System.out.print(tmp + " ");
                i++;
            }
        }
    }

    public boolean check_value(double value,int index){
        if(value > higher_bound || value < lower_bound){return false;}
        while(index >= 0){
            if(dots[index] == value){
                return false;
            }
            --index;
        }
        return true;

    }

    public void enter_data(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of dots you need: ");
        dimension = scanner.nextInt();
        dots = new double[dimension];
        System.out.print("\nEnter the lower bound of the interval: ");
        lower_bound = scanner.nextDouble();
        System.out.print("\nEnter the higher bound of the interval: ");
        higher_bound = scanner.nextDouble();
        System.out.print("\nEnter the coefficients: ");
        coef = new double[dimension];
        for(int i =0; i < dimension; i++){
            coef[i] = scanner.nextDouble();
        }
        System.out.print("\nSelect how to fill dots: Random(r) or Manual(m)");
        if(scanner.next().equals("r")){
            System.out.println("\nFilling array with dots randomly");
            fill_dots();
        }
        else{
            System.out.println("\nFilling array with dots manualy");
            for(int i =0; i < dimension; i++){
                double temp = scanner.nextDouble();
                while(temp < lower_bound && temp > higher_bound){
                    System.out.println("Error, enter the value again");
                    temp = scanner.nextDouble();
                }
                dots[i] = temp;
            }
        }
    }

    public void show_matrix(double [][] matrix){
        for(int i =0 ; i < dimension; i++){
            for(int j =0; j < dimension; j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public double[][] vector_cross_matrix(double [][] matrix){
        double [][] result = new double[dimension][dimension];
        for(int i = 0; i < dimension;i++){
            for(int j =0; j < dimension; j++){
                result[i][j] = coef[i] * matrix[j][i];
            }
        }
        return result;
    }

    public double [] multiplication(double [][] matrix){
        double [][] first =vector_cross_matrix(matrix);
        double [] result = new double[2*dimension-1];
        for(int i = 0;i<dimension;i++){
            for(int j =0; j < dimension; j++){
                result[i+j] += first[j][i] * coef[i];
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Matrix x = new Matrix();
        x.enter_data();
        x.matrix = x.find_inf_matrix();
        x.inversion(x.matrix);
        double [] result = x.multiplication(x.matrix);
        for(int i =0 ; i < result.length; i++){
            System.out.println(result[i] + "*x^" + i);
        }
    }

}
