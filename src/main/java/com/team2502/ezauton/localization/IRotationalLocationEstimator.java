package com.team2502.ezauton.localization;

/**
 * An interface for any object that knows our current heading
 */
//TODO: Suggestion - Rename to "IHeadingEstimator" since the word location implies position on a 2D plane to me -- rm
public interface IRotationalLocationEstimator
{
    double estimateHeading();
}
