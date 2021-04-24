# VibratingRobotsSim

This program is under development.

It simulates robots driven by an differential drive.
Currently the robots are displayed as circles with an black line
indicating its direction. 

Key bindings 
- 'w' | up         : move view up
- 'a' | left       : move view up
- 's' | down       : move view up
- 'd' | right      : move view up
- space            : pause
- 'r'              : toggle display rotation
- 'c'              : toggle display coordinates
- 'e'              : toggle display engines and trajectory velocity
- 'o'              : toggle display direction orientation
- '#' | 'g'        : toggle display grid 
- '+' | shift      : increase font size 
- '-' | ctrl Strg  : decrease font size
- 't'              : draw robots in ClassColor
- 'b'              : sets the robots back one position 
                     only functional when paused
- 'n'              : sets the robots to the next position
                     only functional when paused
                   
Functionality
Robots 'drive' through different settings on the two engines.
The robots will not leave the map.
They have basic collisions with some power transmission. 
The robots will be displayed fully dawn when in motion 
and empty if paused.