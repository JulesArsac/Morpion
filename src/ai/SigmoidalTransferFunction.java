package ai;

import java.io.Serializable;

public class SigmoidalTransferFunction implements TransferFunction, Serializable {
	public double evalute(double value) {return 1 / (1 + Math.exp(- value));}
	public double evaluteDerivate(double value) {return (value * (1.0 - value));}
}
