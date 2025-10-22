#!/bin/bash
set -eux


##########################################
# This is for local env
##########################################
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


if [ -d "$PRIME_LIB_DIR" ]; then
    TARGET_LIB="$PRIME_LIB_DIR/libappindicator3.so.1.0.0"
    LINK_NAME="$PRIME_LIB_DIR/libappindicator3.so"

    if [ -f "$TARGET_LIB" ]; then
        echo "Found $TARGET_LIB"
        echo "Creating symlink: $LINK_NAME -> $TARGET_LIB"
        ln -sf "$(basename "$TARGET_LIB")" "$LINK_NAME"
    else
        echo "Library $TARGET_LIB not found — skipping symlink creation."
    fi
else
    echo "Directory $ARCH_LIB_DIR not found — nothing to do."
fi




##########################################
# This is for snapcraft.io env
##########################################
create_symlink_if_necessary() {
    local target_lib_name="$1"
    local link_name="$2"
    local lib_dir="$3"

    local target_lib="$lib_dir/$target_lib_name"
    local link_path="$lib_dir/$link_name"

    if [ ! -d "$lib_dir" ]; then
        echo "Directory $lib_dir not found -> skipping."
        return 0
    fi

    if [ ! -f "$target_lib" ]; then
        echo "Target library $target_lib not found -> skipping symlink creation."
        return 0
    fi

    if [ -L "$link_path" ]; then
        echo "Symlink already exists: $link_path -> $(readlink "$link_path")"
        return 0
    fi

    echo "Creating symlink: $link_path -> $target_lib"
    (cd "$lib_dir" && ln -s "$(basename "$target_lib")" "$link_name")
}

create_symlink_if_necessary "libappindicator3.so.1.0.0" "libappindicator3.so" "$PRIME_LIB_DIR"
create_symlink_if_necessary "liboss4-salsa.so.2.0.0" "libasound.so" "$PRIME_LIB_DIR"