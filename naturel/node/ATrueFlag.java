/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import naturel.analysis.*;

@SuppressWarnings("nls")
public final class ATrueFlag extends PFlag
{

    public ATrueFlag()
    {
        // Constructor
    }

    @Override
    public Object clone()
    {
        return new ATrueFlag();
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseATrueFlag(this);
    }

    @Override
    public String toString()
    {
        return "";
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        throw new RuntimeException("Not a child.");
    }
}
