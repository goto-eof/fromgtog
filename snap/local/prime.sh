#!/bin/bash

set -eux

LIB_DIR=$(find "$SNAPCRAFT_PRIME/usr/lib/" -maxdepth 1 -type d -name "*-linux-gnu" -print -quit)

if [ -d "$LIB_DIR" ]; then
  (cd "$LIB_DIR" && {
      ln -sf libappindicator3.so libappindicator3.so.1.0.0
#      ln -sf libasound.so liboss4-salsa.so.2.0.0
#      ln -sf libasound.so libasound.so.2.0.0
#      ls -sf libpixbufloader-svg.so librsvg-2.so.2
  })
fi

#LIB_PIX_BUF="$LIB_DIR/gdk-pixbuf-2.0/2.10.0/loaders"
#
#if [ -d "$LIB_PIX_BUF" ]; then
#    (cd "$LIB_PIX_BUF" && {
#        ln -sf  "$LIB_DIR/librsvg-2.so.2" "libpixbufloader_svg.so"
#    })
#fi
