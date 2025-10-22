#!/bin/bash

set -eux

SOURCE_DIR="$SNAPCRAFT_STAGE/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"
DESTINATION_DIR="$SNAPCRAFT_PRIME/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"

mkdir -p "$DESTINATION_DIR"
# I am looking for libappindicator3.so in the destination dir, if it does not exists then copy.
# this because snapcraft skips some libraries, including the one that I want libappindicator3.so
for lib in "$SOURCE_DIR"/libappindicator3.so*; do
  BASENAME="$(basename "$lib")"
  SHORTNAME="${BASENAME%%.so*}.so"
  if [ ! -f "$DESTINATION_DIR/$SHORTNAME" ]; then
    cp -L "$lib" "$DESTINATION_DIR/$SHORTNAME"
  fi
done