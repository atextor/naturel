/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import naturel.analysis.*;

@SuppressWarnings("nls")
public final class AStringExp extends PExp
{
    private TString _str_;

    public AStringExp()
    {
        // Constructor
    }

    public AStringExp(
        @SuppressWarnings("hiding") TString _str_)
    {
        // Constructor
        setStr(_str_);

    }

    @Override
    public Object clone()
    {
        return new AStringExp(
            cloneNode(this._str_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAStringExp(this);
    }

    public TString getStr()
    {
        return this._str_;
    }

    public void setStr(TString node)
    {
        if(this._str_ != null)
        {
            this._str_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._str_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._str_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._str_ == child)
        {
            this._str_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._str_ == oldChild)
        {
            setStr((TString) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
