[![travis-ci](https://travis-ci.org/ezAuton/ezAuton.svg?branch=master)](https://travis-ci.org/ezAuton/ezAuton) [![codecov](https://codecov.io/gh/ezAuton/ezAuton/branch/master/graph/badge.svg?token=mDoht49dKM)](https://codecov.io/gh/ezAuton/ezAuton)
# ezAuton
A collection of tools to help with FRC autonomous for Java robots. 

## Included

#### Localization
- currently all tank/differential drive, but built to be extended SUPER EASILY for other drivetrains. Just implement your own `IRotationalLocationEstimator` or `ITranslationalLocationEstimator`. 
  - voltage
  - encoder-encoder
  - encoder-gyro
  - encoder-encoder-gyro

### Trajectories
- Pure Pursuit control law between waypoints

### Actions (better Command replacement)
- Interface for easier unit testing of commands and easy creation of commands which run faster than once every 20ms.
- ![IAction base clases](https://imgur.com/2fJBqDd.png)
- All subclasses of `IAction` (including custom) can be converted to WPILib `Command` or a `Thread`.

### Simulation
All IActions can be simulated. As an example, look at one of our more simple unit tests
```java
TimeWarpedClock clock = new TimeWarpedClock(10); // a clock based off of real time but accelerated 10x

AtomicInteger count = new AtomicInteger(0);
count.compareAndSet(0, 1);
assertEquals(1, count.get());

DelayedAction action = new DelayedAction(3, TimeUnit.SECONDS, () -> count.compareAndSet(1, 3)); // an action that runs in 3 seconds
action.onFinish(() -> count.compareAndSet(3, 4));
ActionGroup group = new ActionGroup()
        .addSequential(action);

group.run(clock);
assertEquals(4, count.get());
```

### Recording
Additionally, you can easily record the robot. All data is stored in JSON and it is very easy to add more "subrecordings".
Here is a more complicated example which incorperates simulation, Pure Pursuit, and recordings.
```java
Path path = new PPWaypoint.Builder()
        .add(0, 0, 16, 13, -12)
        .add(0, 4, 16, 13, -12)
        .add(-0.5, 8.589, 16, 13, -12)
        .add(-0.5, 12.405, 13, 13, -12)
        .add(-0.5, 17, 8.5, 13, -12)
        .add(1.5, 19.4, 0, 13, -12)
        .buildPathGenerator()
        .generate(0.05);

PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 0.001);

// Not a problem
MultiThreadSimulation simulation = new MultiThreadSimulation(1);

// Might be a problem
SimulatedTankRobot robot = new SimulatedTankRobot(1, simulation.getClock(), 40, 0.3, 30D);

TankRobotEncoderEncoderEstimator locEstimator = robot.getDefaultLocEstimator();
locEstimator.reset();

ILookahead lookahead = new LookaheadBounds(1, 7, 2, 10, locEstimator);

TankRobotTransLocDriveable  tankRobotTransLocDriveable = robot.getDefaultTransLocDriveable();

PPCommand ppCommand = new PPCommand(20, TimeUnit.MILLISECONDS, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable);

Recording recording = new Recording();

RobotStateRecorder posRec = new RobotStateRecorder("robotstate", simulation.getClock(), locEstimator, locEstimator, robot.getLateralWheelDistance(), 1.5);
PurePursuitRecorder ppRec = new PurePursuitRecorder("pp", simulation.getClock(), path, ppMoveStrat);
TankDriveableRecorder tankRobot = new TankDriveableRecorder("td", simulation.getClock(), tankRobotTransLocDriveable);

recording.addSubRecording(posRec);
recording.addSubRecording(ppRec);
recording.addSubRecording(tankRobot);

BackgroundAction recAction = new BackgroundAction(10, TimeUnit.MILLISECONDS, recording);

BackgroundAction updateKinematics = new BackgroundAction(2, TimeUnit.MILLISECONDS, robot);

ActionGroup group = new ActionGroup()
        .with(updateKinematics)
        .with(recAction)
        .addSequential(ppCommand);

simulation.add(group);


// run the simulator with a timeout of 20 seconds
simulation.run(30, TimeUnit.SECONDS);

String homeDir = System.getProperty("user.home");
java.nio.file.Path filePath = Paths.get(homeDir, ".ezauton", "log.json");

Files.createDirectories(filePath.getParent());

BufferedWriter writer = getBufferedWriter();

writer.write(recording.toJson());
writer.close();
```

### Visualizer
- Once you record, you can _play_ the recording as well. Built to be extendible, so you can implement your own 
visualizer modules as well (use `ISubRecording` in the "Recording" module and `IDataProcessor` in the "Visualizer" module).
- !(https://i.imgur.com/OCRWotR.gif)
 

## Examples
ezAuton is not currently stable! If you are using the current _ezAuton_ library, your code might not work with most updated version a week later.

### Tank robot Pure Pursuit no helpers
This is code for encoder-encoder localization with a tank robot with no helper classes. The code is overly-verbose for most scenarios; it is usually not needed.

```Java
TalonSRX leftTalon = new TalonSRX(1);
TalonSRX rightTalon = new TalonSRX(2);

// (x, y, speed, acceleration, deceleration)
PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -3);
PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 12, 0, 3, -3);

PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoint1, waypoint2, waypoint3);

// Generates a path by looking for nominal poses every 0.05 (dt) seconds. A small dt will yield more precision. The path will automatically interpolate between generated poses.
Path path = pathGenerator.generate(0.05);

// The strategy for moving. The stop tolerance is the distance away from the endpoint where Pure Pursuit is happy.
PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 0.1D);

// Means to easily interface with motors
IVelocityMotor leftMotor = velocity -> leftTalon.set(ControlMode.Velocity, velocity * Encoders.CTRE_MAG_ENCODER);
IVelocityMotor rightMotor = velocity -> rightTalon.set(ControlMode.Velocity, velocity * Encoders.CTRE_MAG_ENCODER);

// Means to easily interface with encoders
IEncoder leftEncoder = Encoders.fromTalon(leftTalon, Encoders.CTRE_MAG_ENCODER);
EncoderWheel leftEncoderWheel = new EncoderWheel(leftEncoder, 3);

IEncoder rightEncoder = Encoders.fromTalon(rightTalon, Encoders.CTRE_MAG_ENCODER);
EncoderWheel rightEncoderWheel = new EncoderWheel(rightEncoder, 3);

// The lateral wheel distance between wheels
ITankRobotConstants constants = () -> 20;

// Encoder-encoder location estimator
TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(leftEncoderWheel, rightEncoderWheel, constants);

// Dynamic lookahead with speed (speed comes from location estimator) 
ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

// An implementation for the robot to move toward a point at a provided speed
TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, constants);

// Background task to periodically update location calculations
Thread thread = new BackgroundAction(locEstimator).buildThread(10);
thread.start();

// Command to start Pure Pursuit
Command commmand = new PPCommand(ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable).buildWPI();
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
double maxAccelPerSecond = 3D;

// These RampUpSimulatedMotors provide a ramp up when setting a voltage. For example, if you immediately want 100% voltage the motor will actually slowly be set
// From 0% to 100%. This smooth transition between voltage allows for easier localization as the relationship between voltage and velocity is predictable (and linear for most FRC motors)
RampUpSimulatedMotor leftMotor = RampUpSimulatedMotor.fromVolt(voltage -> leftTalon.set(ControlMode.PercentOutput, voltage), maxRobotSpeed, maxAccelPerSecond);
RampUpSimulatedMotor rightMotor = RampUpSimulatedMotor.fromVolt(voltage -> rightTalon.set(ControlMode.PercentOutput, voltage), maxRobotSpeed, maxAccelPerSecond);

ITankRobotConstants constants = () -> 5;

TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(leftMotor, rightMotor, constants);

ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, constants);

// Background task to update location and percent voltage applied to motors. Will run every 10ms.
Thread thread = new BackgroundAction(locEstimator, leftMotor, rightMotor).buildThread(10);
thread.start();

// Command to start Pure Pursuit
Command commmand = new PPCommand(ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable).buildWPI();
```