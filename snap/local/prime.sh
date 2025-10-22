#!/bin/bash

set -eux

SOURCE_DIR="gnome-platform/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"
DESTINATION_DIR="$SNAPCRAFT_PRIME/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"

mkdir -p "$DESTINATION_DIR"
# WORKAROUND
# I am looking for libappindicator3.so in the destination dir, if it does not exists then copy.
# this because snapcraft skips some libraries, including the one that I need, libappindicator3.so,
# otherwise the system tray not works and the application does not start
for lib in "$SOURCE_DIR"/libappindicator3.so*; do
  if [ ! -f "$DESTINATION_DIR/libappindicator3.so" ]; then
    cp -L "$lib" "$DESTINATION_DIR/libappindicator3.so"
  fi
done