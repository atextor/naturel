package naturel.generator;

import naturel.node.PExp;

public interface ILRNode {
	public PExp getL();
	public PExp getR();
	public void setL(PExp node);
	public void setR(PExp node);
}

