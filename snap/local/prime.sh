#!/bin/bash
set -eux

mkdir -p "$SNAP_USER_DATA/tmp"

INSTALL_LIB_PATH="$SNAPCRAFT_STAGE/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"
DESTINATION="$SNAPCRAFT_PRIME/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"

mkdir -p "$DESTINATION"

# Tray Icon
cp -L "$INSTALL_LIB_PATH/libappindicator3.so.1.0.0" "$DESTINATION/libappindicator3.so"

# Sound
cp -L "$INSTALL_LIB_PATH/liboss4-salsa.so.2.0.0" "$DESTINATION/libasound.so"
lib="libasound_module_conf_pulse.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"
lib="libasound_module_ctl_arcam_av.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"
lib="libasound_module_ctl_oss.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"
lib="libasound_module_ctl_pulse.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"
lib="libasound_module_pcm_a52.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"
lib="libasound_module_pcm_jack.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"
lib="libasound_module_pcm_oss.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"
lib="libasound_module_pcm_pulse.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"
lib="libasound_module_pcm_speex.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"
lib="libasound_module_pcm_upmix.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"
lib="libasound_module_pcm_usb_stream.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"
lib="libasound_module_pcm_vdownmix.so"
cp -L "$INSTALL_LIB_PATH/alsa-lib/$lib" "$DESTINATION/$lib"

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