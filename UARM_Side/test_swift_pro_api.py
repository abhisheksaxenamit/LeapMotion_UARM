#!/usr/bin/env python3
# Software License Agreement (BSD License)
#
# Copyright (c) 2017, UFactory, Inc.
# All rights reserved.
#
# Author: Duke Fong <duke@ufactory.cc>


import sys, os
from time import sleep

##sys.path.append(os.path.join(os.path.dirname(__file__), '../..'))
import json
import requests


from uf.wrapper.swift_api import SwiftAPI
from uf.utils.log import *
from uf.ufc import ufc_init
from uf.swift import Swift
from uf.utils.log import *

#logger_init(logging.VERBOSE)
#logger_init(logging.DEBUG)
logger_init(logging.INFO)

print('setup swift ...')

#swift = SwiftAPI(dev_port = '/dev/ttyACM0')
#swift = SwiftAPI(filters = {'hwid': 'USB VID:PID=2341:0042'})
##swift = SwiftAPI() # default by filters: {'hwid': 'USB VID:PID=2341:0042'}
ufc = ufc_init()

swift_iomap = {
        'pos_in':  'swift_pos_in',
        'service': 'swift_service',
        'gripper': 'swift_gripper'
}

swift = Swift(ufc, 'swift', swift_iomap)

test_ports = {
        'swift_pos':     {'dir': 'out', 'type': 'topic'},
        'swift_service': {'dir': 'out', 'type': 'service'},
        'swift_gripper': {'dir': 'out', 'type': 'service'}
}

test_iomap = {
        'swift_pos':     'swift_pos_in',
        'swift_service': 'swift_service',
        'swift_gripper': 'swift_gripper'
}

# install handle for ports which are listed in the iomap
ufc.node_init('test', test_ports, test_iomap)

url = 'http://localhost//Coordinates.json'


print('sleep 2 sec ...')
sleep(2)

##print('device info: ')
##print(swift.get_device_info())

print('\nset X350 Y0 Z50 F1500 ...')
X=150
Y=0
Z=150
sp=6000
xprev=0
yprev=0
zprev=0
G_prev=0
##swift.set_position(X, Y, Z, speed = sp)
print('set X190: ' + test_ports['swift_service']['handle'].call('set cmd_sync G0 X150 Y0 Z150 F30000'))

try:
    while True:
        try:
            r = requests.get(url, verify=True)
        except Exception as e:
            print(e)
        try:
            data=r.json()
            if(int(data['X']) != 0):
                val=int(data['X'])
                if(val > 60):
                    val=60
                elif(val < -60):
                    val=-60
                X=X+val
                print('X: ',X,' xprev: ',xprev)
                if(X > 300):
                    X = 300
                elif(X < 100):
                    X = 100
##                print('X: ',X)
##                swift.set_position(x = X)
            if(int(data['Y']) != 0):
                val=int(data['Y'])
                if(val > 60):
                    val=60
                elif(val < -60):
                    val=-60
                Y=Y+val
                if(Y > 140):
                    Y = 140
                elif(Y < -140):
                    Y = -140
            if(int(data['Z']) != 0):
                val=int(data['Z'])
                if(val > 30):
                    val=30
                elif(val < -30):
                    val=-30
                Z=Z+val
                if(Z > 250):
                    Z = 200
                elif(Z < 25):
                    Z = 25
##                swift.set_position(z = Z)
##            swift.flush_cmd()
            if(X != xprev or Y != yprev or Z != zprev):
                cmd='set cmd_sync G0'
                print('X: ',X,' xprev: ',xprev)
                if(X != xprev):
                    cmd=cmd+' X'+str(X)
                    xprev=X
                if(Y != yprev):
                    cmd=cmd +' Y'+str(Y)
                    yprev=Y
                if(Z != zprev):
                    cmd=cmd +' Z'+str(Z)
                    zprev=Z
                cmd=cmd +' F'+str(sp)
##                print('cmd: ',cmd)
                print('set ' + str(cmd) + ' :' + test_ports['swift_service']['handle'].call(cmd))
                sleep(0.1)
            if(int(data['G']) == 0 and int(data['G']) != G_prev):
                G_prev = int(data['G'])
                print('gripper set off return: ' + test_ports['swift_gripper']['handle'].call('set value off'))
##                print('gripper get state return: ' + test_ports['swift_gripper']['handle'].call('get value'))
##                swift.set_gripper(False,1)
            elif(int(data['G']) == 1 and int(data['G']) != G_prev):
                G_prev = int(data['G'])
                print('gripper set on return: ' + test_ports['swift_gripper']['handle'].call('set value on'))
##                print('gripper get state return: ' + test_ports['swift_gripper']['handle'].call('get value'))
##                swift.set_gripper(True,1)
##            sleep(1)
##            swift.flush_cmd()
        except Exception as e:
            print(e)
except KeyboardInterrupt:
    pass
##print('set X320 ...')
##swift.set_position(y = 120)
##swift.set_position(z = 169)
##print('set X120.9 ...')
##swift.set_position(y = 121)
##print('set X300 ...')
##swift.set_position(z = 169)
##print('set X200 ...')
##swift.set_position(x = 200)
##print('set X190 ...')
##swift.set_position(x = 190)
##print('Flush Cmd')
# wait all async cmd return before send sync cmd
##swift.flush_cmd()
##
##print('set Z100 ...')
##swift.set_position(z = 100, wait = True)
##print('set Z150 ...')
##swift.set_position(z = 150, wait = True)
##swift.set_position(y = -0, wait = True)
##swift.set_buzzer()
##
##
##print('done ...')
while True:
    sleep(1)

