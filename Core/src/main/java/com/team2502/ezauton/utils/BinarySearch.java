package com.team2502.ezauton.utils;

import java.util.List;

/**
 * A class for easy binary searches which can potentially result in massive improvements
 * over sequentially-based searches.
 *
 * @param <T>
 * @deprecated Needs testing!!!
 */
public class BinarySearch<T> //TODO test
{

    private final List<T> list;
    int searchIndex;
    int left;
    int right;
//    int dif;

    public BinarySearch(List<T> list)
    {
        this.list = list;
        searchIndex = list.size() / 2;
        left = 0;
        right = list.size() - 1;
//        dif = searchIndex / 2;
    }

    public T search(Search<T> search)
    {
        int m;
        while(left <= right)
        {
            m = (int) Math.floor((double) (left + right) / 2);

            T t = list.get(m);
            SearchEntryResult accept = search.accept(t);
            switch(accept)
            {
                case LOW:
                    left = m + 1;
                    break;
                case HIGH:
                    right = m - 1;
                    break;
                case CORRECT:
                    return t;
            }
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
