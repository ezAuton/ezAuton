package com.team2502.ezauton.recorder;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class CSVBuilder implements CSVAble
{

    StringBuilder stringBuilder = new StringBuilder();

    public static void main(String[] args)
    {

        new CSVBuilder(new NamedCallable<>("Abc", () -> 1234)).addLine();
    }

    private final NamedCallable<?>[] callables;

    public CSVBuilder(NamedCallable<?>... callables)
    {
        this.callables = callables;

        String header = Arrays.stream(callables).map(namedCallable -> namedCallable.name).collect(Collectors.joining(","));
        stringBuilder.append(header);
    }

    void addLine()
    {
        String line = Arrays.stream(callables).map(namedCallable -> {
            try
            {
                return namedCallable.callable.call().toString();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                throw new IllegalStateException("Received an exception while writing CSV!");
            }
        }).collect(Collectors.joining(","));

        stringBuilder.append('\n').append(line);
    }

    @Override
    public String toCSV()
    {
        return stringBuilder.toString();
    }

    public static class NamedCallable<V> implements Callable<V>
    {

        private final String name;
        private final Callable<V> callable;

        public NamedCallable(String name, Callable<V> callable)
        {
            this.name = name;
            this.callable = callable;
        }

        @Override
        public V call() throws Exception
        {
            return callable.call();
        }
    }
}
