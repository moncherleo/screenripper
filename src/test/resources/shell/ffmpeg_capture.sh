#!/bin/bash
ffmpeg -y -f avfoundation -i "1:0" -vf  "crop=1280:700:0:60" -preset ultrafast /Volumes/FreeDisk/Stratoplan/output.mkv