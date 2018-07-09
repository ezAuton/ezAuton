[![travis-ci](https://travis-ci.org/Team-2502/ezAuton.svg?branch=master)](https://travis-ci.org/Team-2502/ezAuton) [![codecov](https://codecov.io/gh/Team-2502/ezAuton/branch/master/graph/badge.svg?token=mDoht49dKM)](https://codecov.io/gh/Team-2502/ezAuton)
# ezAuton
A collection of tools to help with FRC autonomous for Java robots. 

## Included

### Tank Drive
- Simulator for easy unit testing and whatnot
#### Localization
- voltage
- encoder-encoder
- encoder-gyro
- encoder-encoder-gyro
### Trajectories
- Pure Pursuit control law between waypoints
### Helper classes
- Interface for easier unit testing of commands and easy creation of commands which run faster than once every 20ms.

## Examples
ezAuton is not currently stable! If you are using the current _ezAuton_ library, your code might not work with most updated version a week later.

### Tank robot Pure Pursuit no helpers
This is code for encoder-encoder localization with a tank robot with no helper classes. The code is overly-verbose for most scenarios; it is usually not needed.

```Java
TalonSRX leftTalon = new TalonSRX(ID1);
TalonSRX rightTalon = new TalonSRX(ID2);

// (x, y, speed, acceleration, deceleration)
PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -3);
PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 12, 0, 3, -3);

PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoint1, waypoint2, waypoint3);

// dt = 0.05
Path path = pathGenerator.generate(0.05);

PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 0.1D);

IVelocityMotor leftMotor = velocity -> leftTalon.set(ControlMode.Velocity, velocity * Encoders.CTRE_MAG_ENCODER);
IVelocityMotor rightMotor = velocity -> rightTalon.set(ControlMode.Velocity, velocity * Encoders.CTRE_MAG_ENCODER);

IEncoder leftEncoder = Encoders.fromTalon(leftTalon, Encoders.CTRE_MAG_ENCODER);
EncoderWheel leftEncoderWheel = new EncoderWheel(leftEncoder, 3);

IEncoder rightEncoder = Encoders.fromTalon(rightTalon, Encoders.CTRE_MAG_ENCODER);
EncoderWheel rightEncoderWheel = new EncoderWheel(rightEncoder, 3);

// The lateral wheel distance between wheels
ITankRobotConstants constants = () -> 20;

TankRobotEncoderRotationEstimator locEstimator = new TankRobotEncoderRotationEstimator(leftEncoderWheel, rightEncoderWheel, constants);

ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, constants);
Command commmand = new PPCommand(ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable, locEstimator).buildWPI();
 ```
 
 ### Tank robot voltage localization
 A robot can still use Pure Pursuit if it does not have any encoders or a gyro! FRC motors have an increadibly linear relationship between voltage applied and motor speed. If you provide a map from voltage to velocity, ezAuton provides implementation to intepolate this map to provide localization. However, make sure to use `RampUpSimulatedMotor`s. These make sure that the voltage applied to the motor is fairly continuous. Without this helper class, if there is a sudden, large increase or decrease in voltage applied to the motor, the map from voltage to motor velocity is usually not accurate. 
 ```Java
TalonSRX leftTalon = new TalonSRX(1);
TalonSRX rightTalon = new TalonSRX(2);

PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(Paths.STRAIGHT_12FT, 0.1D);

// max speed of robot in feet. This can be any unit; however, units must be consistent across entire use of PP.
double maxRobotSpeed = 16;

// We need to limit acceleration for voltage drive because the motor will always need to run within its bounds to
// get accurate localization
// we need accel per 20ms because that is how often a command in WPILib is called
double maxAccelPerSecond = 3D;
double maxAccelPer20ms = 3 / 50D;

RampUpSimulatedMotor leftMotor = RampUpSimulatedMotor.fromVolt(voltage -> leftTalon.set(ControlMode.PercentOutput, voltage), maxRobotSpeed, maxAccelPer20ms);
RampUpSimulatedMotor rightMotor = RampUpSimulatedMotor.fromVolt(voltage -> rightTalon.set(ControlMode.PercentOutput, voltage), maxRobotSpeed, maxAccelPer20ms);

ITankRobotConstants constants = () -> 5;

TankRobotEncoderRotationEstimator locEstimator = new TankRobotEncoderRotationEstimator(leftMotor, rightMotor, constants);

ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, constants);
Command commmand = new PPCommand(ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable, locEstimator).buildWPI();
