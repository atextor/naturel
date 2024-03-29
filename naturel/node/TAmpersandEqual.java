/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import naturel.analysis.*;

@SuppressWarnings("nls")
public final class TAmpersandEqual extends Token
{
    public TAmpersandEqual()
    {
        super.setText("&=");
    }

    public TAmpersandEqual(int line, int pos)
    {
        super.setText("&=");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TAmpersandEqual(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTAmpersandEqual(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TAmpersandEqual text.");
    }
}
