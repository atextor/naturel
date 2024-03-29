/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import naturel.analysis.*;

@SuppressWarnings("nls")
public final class AUnnamedVariable extends PVariable
{
    private PType _type_;
    private PExp _val_;

    public AUnnamedVariable()
    {
        // Constructor
    }

    public AUnnamedVariable(
        @SuppressWarnings("hiding") PType _type_,
        @SuppressWarnings("hiding") PExp _val_)
    {
        // Constructor
        setType(_type_);

        setVal(_val_);

    }

    @Override
    public Object clone()
    {
        return new AUnnamedVariable(
            cloneNode(this._type_),
            cloneNode(this._val_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAUnnamedVariable(this);
    }

    public PType getType()
    {
        return this._type_;
    }

    public void setType(PType node)
    {
        if(this._type_ != null)
        {
            this._type_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._type_ = node;
    }

    public PExp getVal()
    {
        return this._val_;
    }

    public void setVal(PExp node)
    {
        if(this._val_ != null)
        {
            this._val_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._val_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._type_)
            + toString(this._val_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._type_ == child)
        {
            this._type_ = null;
            return;
        }

        if(this._val_ == child)
        {
            this._val_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._type_ == oldChild)
        {
            setType((PType) newChild);
            return;
        }

        if(this._val_ == oldChild)
        {
            setVal((PExp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
