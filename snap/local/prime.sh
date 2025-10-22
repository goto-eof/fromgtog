#!/bin/bash
set -eux


##########################################
# workaround
##########################################
SOURCE="/root"
DESTINATION="$SNAPCRAFT_PRIME/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"

copy_lib_with_new_name() {
    local source_path="$1"
    local destination_path="$2"
    local source_lib_pattern="$3"
    local destination_lib_name="$4"

    mkdir -p "$destination_path"

    lib=$(find "$source_path" -type f -name "$source_lib_pattern" | head -n1)

    if [ -f "$lib" ]; then
        echo "Copying library:"
        echo "  SOURCE:      $lib"
        echo "  Destination: $destination_path/$destination_lib_name"
        cp -L "$lib" "$destination_path/$destination_lib_name"
    else
        echo "No library matching $source_lib_pattern found in $source_path."
    fi
}

DESTINATION="$SNAPCRAFT_PRIME/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"

copy_lib_with_new_name "$SOURCE" "$DESTINATION" "libappindicator3.so*" "libappindicator3.so"
copy_lib_with_new_name "$SOURCE" "$DESTINATION" "liboss4-salsa.so.2.0.*" "libasound.so"





##########################################
# workaround
##########################################
#create_symlink_if_necessary() {
#    local target_lib_name="$1"
#    local link_name="$2"
#    local lib_dir="$3"
#
#    local target_lib="$lib_dir/$target_lib_name"
#    local link_path="$lib_dir/$link_name"
#
#    if [ ! -d "$lib_dir" ]; then
#        echo "Directory $lib_dir not found -> skipping symlink creation."
#        return 0
#    fi
#
#    if [ ! -f "$target_lib" ]; then
#        echo "Target library $target_lib not found -> skipping symlink creation."
#        return 0
#    fi
#
#    if [ -L "$link_path" ]; then
#        echo "Symlink already exists: $link_path -> $(readlink "$link_path")"
#        return 0
#    fi
#
#    if [ -f "$link_path" ]; then
#        echo "A regular file already exists at $link_path -> skipping symlink creation."
#        return 0
#    fi
#
#    echo "Creating symlink: $link_path -> $target_lib"
#    (cd "$lib_dir" && ln -s "$(basename "$target_lib")" "$link_name")
#}
#
#Source_SL="$SNAPCRAFT_PROJECT_DIR/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR"
#create_symlink_if_necessary "libappindicator3.so.1.0.0" "libappindicator3.so" "$Source_SL"
#create_symlink_if_necessary "liboss4-salsa.so.2.0.0" "libasound.so" "$Source_SL"