package com.github.ezauton.core.test.utils;

import com.github.ezauton.core.utils.BinarySearch;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BinarySearchTest
{
    List<Integer> sortedIntegers = Arrays.asList(1, 3, 7, 8, 13, 18);

    private BinarySearch.Search<Integer> searchFor(int waldo)
    {
        return integer -> {
            if(integer < waldo)
            {
                return BinarySearch.SearchEntryResult.LOW;
            }
            if(integer > waldo)
            {
                return BinarySearch.SearchEntryResult.HIGH;
            }
            return BinarySearch.SearchEntryResult.CORRECT;
        };
    }

    @Test
    public void testBinarySearch()
    {
        for(int i : sortedIntegers)
        {
            BinarySearch<Integer> binarySearch = new BinarySearch<>(sortedIntegers);
            Integer result = binarySearch.search(searchFor(i));
            assertEquals(i, result.intValue());
        }
    }

    @Test
    public void testBinarySearchNotFound()
    {
        BinarySearch<Integer> binarySearch = new BinarySearch<>(sortedIntegers);
        Integer result = binarySearch.search(searchFor(2502));
        assertEquals(null, result);
    }
}
