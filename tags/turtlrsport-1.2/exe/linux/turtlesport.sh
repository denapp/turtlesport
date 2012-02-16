#!/bin/sh
# Copyright (C) 2009-2010 Turtle Sport and contributors
#
#
# http://turtlesport.sourceforge.net
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
 
# Retrieve turtlesport directory
PROGRAM=`readlink "$0"`
if ["$PROGRAM" = ""]; then
  PROGRAM=$0
fi
PROGRAM_DIR=`dirname "$PROGRAM"`

# Run Turtle Sport
exec java -jar "$PROGRAM_DIR"/turtlesport.jar
