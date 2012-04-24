/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import naturel.analysis.*;

@SuppressWarnings("nls")
public final class TMinusMinus extends Token
{
    public TMinusMinus()
    {
        super.setText("--");
    }

    public TMinusMinus(int line, int pos)
    {
        super.setText("--");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TMinusMinus(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTMinusMinus(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TMinusMinus text.");
    }
}
