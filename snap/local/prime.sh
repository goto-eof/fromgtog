#!/bin/bash

set -eux

. "$SNAPCRAFT_PRIME/usr/lib/functions.sh"

########################################################################
# temporary directory for the snap
# I am overriding the java temporary dir path env var in run.sh
# so I need to create the temporary directory in the $SNAP dir
########################################################################
mkdir -p "$SNAP_USER_DATA/tmp"

########################################################################
# Fix for Tray Icon Issue
########################################################################
INSTALL_LIB_PATH="$SNAPCRAFT_STAGE/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"
DESTINATION="$SNAPCRAFT_PRIME/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"
mkdir -p "$DESTINATION"
cp -L "$INSTALL_LIB_PATH/libappindicator3.so.1.0.0" "$DESTINATION/libappindicator3.so"

########################################################################
# Fix for Play Sound Issue
########################################################################
DESTINATION_DIR="$SNAPCRAFT_PRIME/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"
SOURCE_DIR="$SNAPCRAFT_STAGE/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"

SOURCE_FILE="$SOURCE_DIR/libpulse.so.0.24.2"
create_symlink_following_real_file "$SOURCE_FILE" "$DESTINATION_DIR" "libpulse.so"

SOURCE_FILE="$SOURCE_DIR/libasound.so.2.0.0"
create_symlink_following_real_file "$SOURCE_FILE" "$DESTINATION_DIR" "libasound.so"

########################################################################
# Deleting useless files
########################################################################
rm "$SNAPCRAFT_PRIME/usr/lib/functions.sh"
rm "$SNAPCRAFT_PRIME/usr/lib/prime.sh"