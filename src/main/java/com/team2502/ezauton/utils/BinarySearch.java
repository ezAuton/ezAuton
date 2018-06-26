package com.team2502.ezauton.utils;

import java.util.List;

/**
 * A class for easy binary searches which can potentially result in massive improvements
 * over sequentially-based searches.
 *
 * @deprecated Needs testing!!!
 *
 * @param <T>
 */
public class BinarySearch<T> //TODO test
{

    private final List<T> list;
    int searchIndex;
    int dif;

    public BinarySearch(List<T> list)
    {
        this.list = list;
        searchIndex = list.size() / 2;
        dif = searchIndex / 2;
    }

    public T search(Search<T> search)
    {

        while(dif > 0)
        {
            T t = list.get(searchIndex);
            SearchEntryResult accept = search.accept(t);
            switch(accept)
            {
                case LOW:
                    searchIndex += dif;
                    break;
                case HIGH:
                    searchIndex -= dif;
                case CORRECT:
                    return t;
            }
            dif /= 2;
        }
        return null;
    }

    public enum SearchEntryResult
    {
        LOW,
        HIGH,
        CORRECT
    }

    public interface Search<A>
    {
        SearchEntryResult accept(A a);
    }
}
