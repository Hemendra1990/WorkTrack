#!/bin/bash

# Run jpackage to create the .app bundle
jpackage \
    --input . \
    --name "Unity Tracker" \
    --main-jar wt-1.0.0-jar \
    --type app-image \
    --app-version 1.0 \
    --mac-package-name "Unity Tracker" \
    --mac-package-identifier com.hemendra.WorkTrackApp
