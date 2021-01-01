public class Node {

    private double sum(double[] weights, char[] characters) {
        double sum = 0.0;

        double tempArr[] = new double[characters.length];

        for (int i = 0; i < characters.length; i++) {
            tempArr[i] = Character.getNumericValue(characters[i]);
            ///System.out.println(tempArr[i]);
            sum += weights[i] * tempArr[i]; //calculate the sum
        }
        return sum;
    }

    double outputSum(double[][] weights, double[] newArray, double bias) {
        double sum = 0.0;

            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i].length; j++) {
                    sum += weights[i][j] * newArray[i]; //calculate the sum
                }
            }
        return sigmoid(sum - bias);
    }

    public double sigmoid(double val){

        return (1/(1+Math.pow(Math.E,(-1*val))));
    }

    public double input(double[] weights, char[] characters, double bias){

        return sigmoid(sum(weights,characters) + bias);
    }



}

