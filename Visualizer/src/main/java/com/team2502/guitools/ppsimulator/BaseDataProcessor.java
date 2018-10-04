package com.team2502.guitools.ppsimulator;

public abstract class BaseDataProcessor implements IDataProcessor
{

    private IEnvironment environment;
    private IData data;

    @Override
    public void initData(IData data)
    {
        this.data = data;
    }

    @Override
    public void initEnvironment(IEnvironment environment)
    {
        this.environment = environment;
    }

    /**
     *
     * @param x
     * @return The frame x location given your x location in feet from the left end of the field
     */
    double x(double x)
    {
        return environment.getOriginX() + x * environment.getScaleFactorX();
    }

    /**
     *
     * @param y
     * @return The frame y location given your y location in feet from the bottom of the field
     */
    double y(double y)
    {
        return environment.getOriginY() + y * environment.getScaleFactorY();
    }

}
