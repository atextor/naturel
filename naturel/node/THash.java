/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import naturel.analysis.*;

@SuppressWarnings("nls")
public final class THash extends Token
{
    public THash()
    {
        super.setText("#");
    }

    public THash(int line, int pos)
    {
        super.setText("#");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new THash(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTHash(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change THash text.");
    }
}
