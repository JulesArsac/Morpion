package ai;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MultiLayerPerceptron implements Cloneable, Serializable {
	
	public MultiLayerPerceptron(int[] layers, double learningRate, TransferFunction fun){
		fLearningRate = learningRate;
		fTransferFunction = fun;
		fLayers = new Layer[layers.length];
		
		for(int i = 0; i < layers.length; i++){			
			if(i != 0)
				fLayers[i] = new Layer(layers[i], layers[i - 1]);
			else
				fLayers[i] = new Layer(layers[i], 0);
		}
	}
	
	public double[] forwardPropagation(double[] input){
		int i, j, k;
		double new_value;
		double output[] = new double[fLayers[fLayers.length - 1].Length];
		
		for(i = 0; i < fLayers[0].Length; i++)
			fLayers[0].Neurons[i].value = input[i];
		
		for(k = 1; k < fLayers.length; k++){
			for(i = 0; i < fLayers[k].Length; i++){
				new_value = 0.0;
				for(j = 0; j < fLayers[k - 1].Length; j++)
					new_value += fLayers[k].Neurons[i].weights[j] * fLayers[k - 1].Neurons[j].value;
				
				new_value += fLayers[k].Neurons[i].bias;				
				fLayers[k].Neurons[i].value = fTransferFunction.evalute(new_value);
			}
		}
		
		for(i = 0; i < fLayers[fLayers.length - 1].Length; i++)
			output[i] = fLayers[fLayers.length - 1].Neurons[i].value;
		
		return output;
	}
	
	public double backPropagate(double[] input, double[] output){
		double new_output[] = forwardPropagation(input);
		double error;
		int i, j, k;
		
		for(i = 0; i < fLayers[fLayers.length - 1].Length; i++)
			fLayers[fLayers.length - 1].Neurons[i].delta = (output[i] - new_output[i]) * fTransferFunction.evaluteDerivate(new_output[i]);

		for(k = fLayers.length - 2; k >= 0; k--){
			for(i = 0; i < fLayers[k].Length; i++){
				error = 0.0;
				for(j = 0; j < fLayers[k + 1].Length; j++)
					error += fLayers[k + 1].Neurons[j].delta * fLayers[k + 1].Neurons[j].weights[i];
				fLayers[k].Neurons[i].delta = error * fTransferFunction.evaluteDerivate(fLayers[k].Neurons[i].value);				
			}
			for(i = 0; i < fLayers[k + 1].Length; i++){
				for(j = 0; j < fLayers[k].Length; j++)
					fLayers[k + 1].Neurons[i].weights[j] += fLearningRate * fLayers[k + 1].Neurons[i].delta * 
							fLayers[k].Neurons[j].value;
				fLayers[k + 1].Neurons[i].bias += fLearningRate * fLayers[k + 1].Neurons[i].delta;
			}
		}	
		//
		error = 0.0;
		for(i = 0; i < output.length; i++)
			error += Math.abs(new_output[i] - output[i]);
		//
		return (error / output.length);
	}
	
	public boolean save(String path){
		try{
			FileOutputStream fout = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(this);
			oos.close();
		}
		catch (Exception e) { 
			return false;
		}
		return true;
	}
	
	public static MultiLayerPerceptron load(String path){
		try{
			FileInputStream fin = new FileInputStream(path);
			ObjectInputStream oos = new ObjectInputStream(fin);
			MultiLayerPerceptron net = (MultiLayerPerceptron) oos.readObject();
			oos.close();
			
			return net;
		}
		catch (Exception e) { 
			System.err.println("load: "+e.getMessage());
		}
		return null;
	}
	
	//CHAMPS ...
	double			fLearningRate = 0.6;
	Layer[]			fLayers;
	TransferFunction 	fTransferFunction;
}

