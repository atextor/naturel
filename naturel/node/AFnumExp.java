/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import naturel.analysis.*;

@SuppressWarnings("nls")
public final class AFnumExp extends PExp
{
    private TFloatSequence _fnum_;

    public AFnumExp()
    {
        // Constructor
    }

    public AFnumExp(
        @SuppressWarnings("hiding") TFloatSequence _fnum_)
    {
        // Constructor
        setFnum(_fnum_);

    }

    @Override
    public Object clone()
    {
        return new AFnumExp(
            cloneNode(this._fnum_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFnumExp(this);
    }

    public TFloatSequence getFnum()
    {
        return this._fnum_;
    }

    public void setFnum(TFloatSequence node)
    {
        if(this._fnum_ != null)
        {
            this._fnum_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._fnum_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._fnum_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._fnum_ == child)
        {
            this._fnum_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._fnum_ == oldChild)
        {
            setFnum((TFloatSequence) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
