#!/bin/bash

create_symlink_following_real_file() {
    local source_path="$1"
    local destination_dir="$2"
    local link_name="$3"

    local real_source
    real_source="$(readlink -f "$source_path")"

    if [ ! -f "$real_source" ]; then
        echo "Source file does not exist: $real_source"
        return 1
    fi

    mkdir -p "$destination_dir"

    local link_path="$destination_dir/$link_name"

    if [ -L "$link_path" ]; then
        local existing_target
        existing_target="$(readlink -f "$link_path")"
        if [ "$existing_target" == "$real_source" ]; then
            echo "Symlink already correct: $link_path -> $real_source"
            return 0
        else
            echo "Updating symlink: $link_path (was -> $existing_target)"
        fi
    fi

    (cd "$destination_dir" && ln -sfn "$(basename "$real_source")" "$link_name")

    echo "Created symlink: $link_path -> $real_source"
}

copy_file_with_new_name() {
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