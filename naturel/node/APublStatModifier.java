/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import naturel.analysis.*;

@SuppressWarnings("nls")
public final class APublStatModifier extends PModifier
{

    public APublStatModifier()
    {
        // Constructor
    }

    @Override
    public Object clone()
    {
        return new APublStatModifier();
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPublStatModifier(this);
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