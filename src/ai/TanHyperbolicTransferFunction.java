package ai;

import java.io.Serializable;

public class TanHyperbolicTransferFunction implements TransferFunction, Serializable {
	public double evalute(double value) {return Math.tanh(value); }//{return 1 / (1 + Math.exp(- value));}
	public double evaluteDerivate(double value) {return 1.0 - Math.pow(evalute(value), 2); } //{return (value * (1.0 - value));}
}
