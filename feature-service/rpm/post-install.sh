#!/bin/bash

# Post install script. rpm.

# not a lot here right now.
chkconfig e8-api-server on

echo "Service installed, not started, but will start on the next boot."
