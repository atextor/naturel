/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import naturel.analysis.*;

@SuppressWarnings("nls")
public final class TExclMark extends Token
{
    public TExclMark()
    {
        super.setText("!");
    }

    public TExclMark(int line, int pos)
    {
        super.setText("!");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TExclMark(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTExclMark(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TExclMark text.");
    }
}
