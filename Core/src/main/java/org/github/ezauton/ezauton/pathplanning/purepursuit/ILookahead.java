package org.github.ezauton.ezauton.pathplanning.purepursuit;

/**
 * The distance to lookahead on the path in the Pure Pursuit control law. This is comparable to how
 * far we would want to look ahead on a path we were walking on. It is logical to adjust lookahead based
 * on how fast we are going or the curvature of the path.
 */
public interface ILookahead
{
    double getLookahead();
}
