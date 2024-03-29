/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import naturel.analysis.*;

@SuppressWarnings("nls")
public final class TStarEqual extends Token
{
    public TStarEqual()
    {
        super.setText("*=");
    }

    public TStarEqual(int line, int pos)
    {
        super.setText("*=");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TStarEqual(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTStarEqual(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TStarEqual text.");
    }
}
