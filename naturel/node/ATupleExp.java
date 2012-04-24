/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import java.util.*;
import naturel.analysis.*;

@SuppressWarnings("nls")
public final class ATupleExp extends PExp
{
    private final LinkedList<PExp> _values_ = new LinkedList<PExp>();

    public ATupleExp()
    {
        // Constructor
    }

    public ATupleExp(
        @SuppressWarnings("hiding") List<PExp> _values_)
    {
        // Constructor
        setValues(_values_);

    }

    @Override
    public Object clone()
    {
        return new ATupleExp(
            cloneList(this._values_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseATupleExp(this);
    }

    public LinkedList<PExp> getValues()
    {
        return this._values_;
    }

    public void setValues(List<PExp> list)
    {
        this._values_.clear();
        this._values_.addAll(list);
        for(PExp e : list)
        {
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._values_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._values_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PExp> i = this._values_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PExp) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}