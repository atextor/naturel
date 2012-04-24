/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.node;

import java.util.*;
import naturel.analysis.*;

@SuppressWarnings("nls")
public final class ANaturel extends PNaturel
{
    private final LinkedList<PImport> _uses_ = new LinkedList<PImport>();
    private final LinkedList<PClass> _classes_ = new LinkedList<PClass>();

    public ANaturel()
    {
        // Constructor
    }

    public ANaturel(
        @SuppressWarnings("hiding") List<PImport> _uses_,
        @SuppressWarnings("hiding") List<PClass> _classes_)
    {
        // Constructor
        setUses(_uses_);

        setClasses(_classes_);

    }

    @Override
    public Object clone()
    {
        return new ANaturel(
            cloneList(this._uses_),
            cloneList(this._classes_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANaturel(this);
    }

    public LinkedList<PImport> getUses()
    {
        return this._uses_;
    }

    public void setUses(List<PImport> list)
    {
        this._uses_.clear();
        this._uses_.addAll(list);
        for(PImport e : list)
        {
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
        }
    }

    public LinkedList<PClass> getClasses()
    {
        return this._classes_;
    }

    public void setClasses(List<PClass> list)
    {
        this._classes_.clear();
        this._classes_.addAll(list);
        for(PClass e : list)
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
            + toString(this._uses_)
            + toString(this._classes_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._uses_.remove(child))
        {
            return;
        }

        if(this._classes_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PImport> i = this._uses_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PImport) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        for(ListIterator<PClass> i = this._classes_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PClass) newChild);
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