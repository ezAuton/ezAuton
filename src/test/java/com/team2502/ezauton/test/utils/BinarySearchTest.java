package com.team2502.ezauton.test.utils;

import com.team2502.ezauton.utils.BinarySearch;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class BinarySearchTest
{
    @Test
    public void testBinarySearch()
    {
        List<Integer> sortedIntegers = Arrays.asList(1, 3, 7, 8, 13, 18);
        BinarySearch<Integer> binarySearch = new BinarySearch<>(sortedIntegers);
        Integer result = binarySearch.search(integer -> {
            if(integer < 8)
            {
                return BinarySearch.SearchEntryResult.LOW;
            }
            if(integer > 8)
            {
                return BinarySearch.SearchEntryResult.HIGH;
            }
            return BinarySearch.SearchEntryResult.CORRECT;
        });
        Assert.assertEquals(8,result.intValue());
    }
}
