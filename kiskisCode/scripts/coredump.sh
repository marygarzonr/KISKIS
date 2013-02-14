#!/bin/bash

set -x

pid=`ps aux | grep kiskis | grep java | awk '{ print $2; }'`
sudo gcore -o /tmp/core $pid
grep EMOZaFay5498alt /tmp/core.$pid
grep "<Pass" /tmp/core.$pid

okteta /tmp/core.$pid

