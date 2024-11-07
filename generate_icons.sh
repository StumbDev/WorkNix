#!/bin/bash

# Create icons directory if it doesn't exist
mkdir -p src/main/resources/icons

# Generate terminal icon
convert -size 32x32 xc:black \
        -font DejaVu-Sans-Mono -pointsize 20 \
        -fill green -draw "text 8,24 '>_'" \
        src/main/resources/icons/terminal.png

# Generate file manager icon
convert -size 32x32 xc:navy \
        -draw "fill white rectangle 6,6 26,26" \
        -draw "fill navy rectangle 8,8 24,24" \
        -draw "fill white rectangle 10,10 22,22" \
        src/main/resources/icons/files.png 