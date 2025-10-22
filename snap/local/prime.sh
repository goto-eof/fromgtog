#!/bin/bash

set -eux

ARCH_LIB_DIR="$CRAFT_ARCH_TRIPLET_BUILD_FOR"
STAGE_LIB_DIR="$SNAPCRAFT_STAGE/usr/lib/$ARCH_LIB_DIR"
PRIME_LIB_DIR="$SNAPCRAFT_PRIME/usr/lib/$ARCH_LIB_DIR"

mkdir -p "$PRIME_LIB_DIR"

for lib in "$STAGE_LIB_DIR"/libappindicator3.so*; do
  DEST="$PRIME_LIB_DIR/$(basename "$lib")"
  BASENAME="$(basename "$lib")"
  SHORTNAME="${BASENAME%%.so*}.so"

  if [ -f "$lib" ] && [ ! -L "$lib" ] && [ ! -e "$DEST" ]; then
      cp "$lib" "$PRIME_LIB_DIR/"
      if [ ! -e "$PRIME_LIB_DIR/$SHORTNAME" ]; then
                  ln -sf "$BASENAME" "$PRIME_LIB_DIR/$SHORTNAME"
      fi
  fi
done


#GNOME_STAGE_LIB_DIR="$SNAPCRAFT_STAGE/gnome-platform/usr/lib/$ARCH_LIB_DIR"
#
#for lib in "$GNOME_STAGE_LIB_DIR"/libappindicator3.so*; do
#  DEST="$PRIME_LIB_DIR/$(basename "$lib")"
#  if [ -f "$lib" ] && [ ! -L "$lib" ] && [ ! -e "$DEST" ]; then
#      cp "$lib" "$PRIME_LIB_DIR/"
#  fi
#done



# gnome-platform/usr/lib
#LIB_PIX_BUF="$LIB_DIR/gdk-pixbuf-2.0/2.10.0/loaders"
#
#if [ -d "$LIB_PIX_BUF" ]; then
#    (cd "$LIB_PIX_BUF" && {
#        ln -sf  "$LIB_DIR/librsvg-2.so.2" "libpixbufloader_svg.so"
#    })
#fi
