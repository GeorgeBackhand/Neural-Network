import java.util.Random;

public class Main {

    //MARKER CAN CHANGE THESE VARIABLES
    double learningRate = 0.1; //Alpha. Changing the learning rate
    int binaryIndex = 8; //Selecting which 4-bit input you want to use(use ReadMe text file to choose specific binary at that index)
    int numHidden = 8; //changing the hidden layer size
    int numOutput = 2; //changing the number of output nodes.
    int Epochs = 10000; //changing the iterations for training
    //////////////////////////////////////////////////////////

    Node node = new Node();
    int size = 16;

    String binaryS; //binary string
    String binaryValue; //the binaries value
    String[] array = new String[size]; // create array of size 16 holding all 16 binary
    String[] binaryTemp =new String[size]; //create a temporary array for holding all 16 binary

    int parityBit; //the parity bit used either 0 or 1 for training
    int seed = 3456;
    Random rand = new Random(seed);

    double[][] weights = new double[numHidden][4]; //each node in hidden layer there is 4 weights because binary is 4digits. 8 rows 4 columns
    double[][] secondWeights = new double[numHidden][numOutput]; //output layer from hidden layer to output layer

    Node[] hiddenLayer = new Node[numHidden]; //8 nodes for the hidden layer
    double[] errorHidden = new double[numHidden];
    double[] errorHiddenD = new double[numHidden]; //derivative of hidden
    double[] outputHidden = new double[numHidden]; //output hidden layers
    int hiddenTracker = 0;

    double[] bias = new double[numHidden];
    double newBias = 0.0;

    double[] Output = new double[numOutput]; //first output node

    char characters[] = new char[4];
    int count = 0;
    //MSE
    double MSE;
    double[] outputError = new double[numOutput];
    double[] errorDerivative = new double[numOutput];

    public Main() {
        binary();
        checkOnes(binaryIndex); //parity bit
        weights(); //random weights
        for(int i = 1; i <= Epochs; i++) { //backwards propagation
            theHidden(binaryIndex); //hidden layer
            train(parityBit);
        }
    }

    public void weights(){
        //assign in the 8x4 2d weights array a random weight at each index.
        for(int i = 0; i < weights.length; i++) {
            for(int j = 0; j < weights[i].length; j++){
                double randNum = rand.nextDouble() * 2 - 1; //get a random weight between -1 and 1
                weights[i][j] = randNum;

            }
        }
        //assign random weights for the weights after the hidden layer
        for(int i = 0; i < secondWeights.length; i++) {
            for (int j = 0; j < secondWeights[i].length; j++) {
                double randNum = rand.nextDouble() * 2 - 1; //get a random weight between -1 and 1
                secondWeights[i][j] = randNum;
            }
        }
    }

    public void theHidden(int newSpot){

        for(int i = 0; i < hiddenLayer.length; i++){
            hiddenLayer[i] = new Node();
            characters = array[newSpot].toCharArray();
            outputHidden[i] = hiddenLayer[i].input(weights[i], characters, bias[i]); //outputs each index at the hidden layer
        }
        hiddenTracker += hiddenLayer.length;

        for(int i = 0; i < numOutput; i++) { //run for as many output nodes we gave
            Output[i] = node.outputSum(secondWeights, outputHidden, newBias); //call my node class method to do my calculations
        }

        //Every 200 epochs/iterations output
        if(count % 200 == 0){
            System.out.println(" ");
            System.out.println("Epoch(iteration): " + count + " -> " + Output[0]);
            System.out.println("Current MSE: " + MSE);
            }
        //Final Output
        if(count == Epochs-1){
            System.out.println(" ");
            System.out.println("Epoch(iteration): " + count);
            System.out.println("# of Hidden Nodes Used: " + numHidden);
            System.out.println("Learning Rate: " + learningRate);
            System.out.println("Final Mean Square Error: " + MSE);
            System.out.println("Output Value: " + Output[0]);
            System.out.println("Expected Value: " + parityBit);
            System.out.println("Binary: " +  array[binaryIndex]);
        }
        count++; //my i the epoch count
    }

    public double derivative(int i){
        return Output[i] * (1 - Output[i]);

    }

    public double newDerivative(double hiddenL){ //derivative of the hidden errors

        return hiddenL*(1-hiddenL);
    }

    public void MSE(){

        for(int i = 0; i < numOutput; i++) { //run for as long as the output layer is
            MSE += Math.pow(outputError[i], 2) / 2;
        }
        MSE /= 2;
    }

    public void train(int target) {

        double[] deriv = new double[numOutput];

        for(int i = 0; i < numOutput; i++) {
            outputError[i] = Output[i] - target;
        }

        MSE();

        for(int i = 0; i < numOutput; i++) {
            deriv[i] = derivative(i);
            errorDerivative[i] = outputError[i] * deriv[i]; //derivative of the output layer
        }

        //outputHidden stores my hidden layer double values
        //creating errorHidden initially null(size 8)
        //secondWeights are my second set of weights after the hidden layer
        //errorHiddenD is the derivative of the error of hidden layer
        for (int i = 0; i < secondWeights.length; i++){
            for(int j = 0; j < secondWeights[i].length; j++) {
                errorHidden[i] = secondWeights[i][j] * errorDerivative[j]; //weights after the hidden layer * error derivative
                errorHiddenD[i] = errorHidden[i] * newDerivative(outputHidden[i]);
            }
        }

        for(int i = 0; i < weights.length; i++) {
            for(int j = 0; j < weights[i].length; j++) {
                weights[i][j] = weights[i][j] - (learningRate * errorHiddenD[i] * Character.getNumericValue(characters[j])); //update first set of weights
            }
        }

        for (int i = 0; i < secondWeights.length; i++){
            for(int j = 0; j < secondWeights[i].length; j++) {
                secondWeights[i][j] = secondWeights[i][j] - (learningRate * errorDerivative[j] * outputHidden[i]); //update second set of weights
            }
        }
    }

    public void binary() { //16 binary numbers
        for (int i = 0; i < array.length; i++) {
            binaryS = Integer.toBinaryString(i);
            binaryValue = String.format("%4s", binaryS).replaceAll(" ", "0");
            binaryTemp[i] = binaryValue; //store the binary value in a temp array for output later
            array[i] = binaryValue;
        }
    }

    public void checkOnes(int binaryIndex){ //calculate the parity bit
        int totOnes = 0; //total # of 1s for each input pattern
            char[] digit = array[binaryIndex].toCharArray(); //get the 4bit pattern for that index in the array and have it as a character array
            for(int j = 0; j < digit.length; j++){ //go through the character array
                if(digit[j] == '1'){ //if the digit at that index is 1 then increase the totalOnes variable
                    totOnes++;
                }
            }
            if(totOnes % 2 == 0){ //if the remainder is 0 (2%2 = 0)
                parityBit = 1;
            }
            else{ // if the remainder is 1 (3%2 = 1)
                parityBit = 0;
            }
        }

    public static void main(String[] args) {
        Main t = new Main();
    }
}