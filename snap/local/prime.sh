#!/bin/bash
set -eux

INSTALL_LIB_PATH="$SNAPCRAFT_STAGE/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"
DESTINATION="$SNAPCRAFT_PRIME/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"

mkdir -p "$DESTINATION"

cp -L "$INSTALL_LIB_PATH/libappindicator3.so.1.0.0" "$DESTINATION/libappindicator3.so"
# cp -L "$INSTALL_LIB_PATH/libasound.so.2.0.0" "$DESTINATION/libasound.so"

return 0

##########################################
# workaround
##########################################
#
#copy_lib_with_new_name() {
#    local source_path="$1"
#    local destination_path="$2"
#    local source_lib_pattern="$3"
#    local destination_lib_name="$4"
#
#    mkdir -p "$destination_path"
#
#    lib=$(find "$source_path" -type f -name "$source_lib_pattern" | head -n1)
#
#    if [ -f "$lib" ]; then
#        echo "Copying library:"
#        echo "  SOURCE:      $lib"
#        echo "  Destination: $destination_path/$destination_lib_name"
#        cp -L "$lib" "$destination_path/$destination_lib_name"
#    else
#        echo "No library matching $source_lib_pattern found in $source_path."
#    fi
#}
#
#SOURCE="/root"
#DESTINATION="$SNAPCRAFT_PRIME/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"
#
#mkdir -p "$DESTINATION"
#
#copy_lib_with_new_name "$SOURCE" "$DESTINATION" "libappindicator3.so*" "libappindicator3.so"
#copy_lib_with_new_name "$SOURCE" "$DESTINATION" "liboss4-salsa.so.2.0.*" "libasound.so"
#copy_lib_with_new_name "$SOURCE/alsa-lib" "$DESTINATION" "libasound_module_pcm_pulse.so" "libasound_module_pcm_pulse.so"