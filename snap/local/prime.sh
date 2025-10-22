#!/bin/bash
set -eux

ARCH_LIB_DIR="$CRAFT_ARCH_TRIPLET_BUILD_FOR"
STAGE_LIB_DIR="/root"
PRIME_LIB_DIR="$SNAPCRAFT_PRIME/usr/lib/$ARCH_LIB_DIR"

mkdir -p "$PRIME_LIB_DIR"

find "$STAGE_LIB_DIR" -type f -name "libappindicator3.so*" | while read -r lib; do
    DEST="$PRIME_LIB_DIR/libappindicator3.so"
    echo "Copying library:"
    echo "  Source:      $lib"
    echo "  Destination: $DEST"
    cp -L "$lib" "$DEST"
    break
done
