package ai;

import java.io.Serializable;

public class Layer implements Serializable{
	public Layer(int l, int prev){
		Length = l;
		Neurons = new Neuron[l];
		//
		for(int j = 0; j < Length; j++)
			Neurons[j] = new Neuron(prev);
	}
	
	//CHAMPS ...
	public Neuron 	Neurons[];
	public int 		Length;
}

class Neuron implements Serializable {
	public Neuron(int prevLayerSize){
		weights = new double[prevLayerSize];
		//
		bias = Math.random() / 10000000000000.0;
		delta = Math.random() / 10000000000000.0;
		value = Math.random() / 10000000000000.0;
		//
		for(int i = 0; i < weights.length; i++)
			weights[i] = Math.random() / 10000000000000.0;
	}
	
	//CHAMPS ...
	public double		value;
	public double[]		weights;
	public double		bias;
	public double		delta;
}