/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import naturel.analysis.*;

@SuppressWarnings("nls")
public final class ADeclaration extends PDeclaration
{
    private PModifier _modifier_;
    private PVariable _var_;

    public ADeclaration()
    {
        // Constructor
    }

    public ADeclaration(
        @SuppressWarnings("hiding") PModifier _modifier_,
        @SuppressWarnings("hiding") PVariable _var_)
    {
        // Constructor
        setModifier(_modifier_);

        setVar(_var_);

    }

    @Override
    public Object clone()
    {
        return new ADeclaration(
            cloneNode(this._modifier_),
            cloneNode(this._var_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADeclaration(this);
    }

    public PModifier getModifier()
    {
        return this._modifier_;
    }

    public void setModifier(PModifier node)
    {
        if(this._modifier_ != null)
        {
            this._modifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._modifier_ = node;
    }

    public PVariable getVar()
    {
        return this._var_;
    }

    public void setVar(PVariable node)
    {
        if(this._var_ != null)
        {
            this._var_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._var_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._modifier_)
            + toString(this._var_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._modifier_ == child)
        {
            this._modifier_ = null;
            return;
        }

        if(this._var_ == child)
        {
            this._var_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._modifier_ == oldChild)
        {
            setModifier((PModifier) newChild);
            return;
        }

        if(this._var_ == oldChild)
        {
            setVar((PVariable) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
