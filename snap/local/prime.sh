#!/bin/bash

MARKER_FILE="$SNAPCRAFT_PART_INSTALL/prime_marker"

if [ -f "$MARKER_FILE" ]; then
  echo "Prime already completed, skipping."
  exit 0
fi

touch "$MARKER_FILE"

#cp -r "$SNAPCRAFT_PART_INSTALL/usr/lib/jvm" "$SNAPCRAFT_PART_PRIME/usr/lib/"
#cp -r "$SNAPCRAFT_PART_INSTALL/jar" "$SNAPCRAFT_PART_PRIME/"

LIB_DIR=$(find "$SNAPCRAFT_PART_PRIME/usr/lib/" -maxdepth 1 -type d -name "*-linux-gnu" -print -quit)

if [ -d "$LIB_DIR" ]; then
  (cd "$LIB_DIR" && {
      ln -sf libappindicator3.so.1.0.0 libappindicator3.so
      ln -sf libasound.so liboss4-salsa.so.2.0.0
  })
fi

LIB_PIX_BUF="$LIB_DIR/gdk-pixbuf-2.0/2.10.0/loaders"

if [ -d "$LIB_PIX_BUF" ]; then
    (cd "$LIB_PIX_BUF" && {
        ln -sf  "$LIB_DIR/librsvg-2.so.2" "libpixbufloader_svg.so"
    })
fi
