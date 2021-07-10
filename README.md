# VibratingRobotsSim

This program simulates robots driven by a differential drive.
The robots are displayed as circles with a black line which
indicating its direction. 
If the simulation is paused the robots will not 
be filled.

Key bindings 
- 'w' | up         : move view up
- 'a' | left       : move view up
- 's' | down       : move view up
- 'd' | right      : move view up
- space            : pause
- F1               : resets the simulation (also resets random)
- F2               : saves current logging to a log file
- 'r'              : toggle rotation indicator
- 'c'              : toggle display coordinates
- 'e'              : toggle display engines and trajectory velocity
- 'o'              : toggle display direction orientation
- '#' | 'g'        : toggle display grid 
- 'l'              : toggle display information on the left side 
- '+'              : zoom in 
- '-'              : zoom out
-  shift           : increase font size 
-  'ctrl' 'strg'   : decrease font size
- 't'              : draw robots in ClassColor
- 'b'              : sets the robots back one position 
                     only functional when paused
- 'n'              : sets the robots to the next position
                     only functional when paused 
                     (any new steps taken will be single threaded)
- 'k'              : draws the center of any robot class
- 'v'              : toggle display the vision cone of LightConeRobots
- 'x'              : toggle display the signal of robots
                   
Functionality

Robots drive through different settings on the two engines.

The robots will not leave the map.

The collision is an elastic collision.

The robots will be displayed fully drawn when in motion 
and empty if paused.

The torus mode will enable any kind of large scale scenario.
It will transfer any objects passing the wall as if the map is a torus.

Any threaded entity can log.

Logging will create csv files for easy data handling.

Simulation mode will automatically simulate and create log files.

