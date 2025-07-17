DESCRIPTION = "Barton IoT Platform Library"
HOMEPAGE = "https://github.com/rdkcentral/BartonCore"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1079582effd6f382a3fba8297d579b46"

PACKAGE_ARCH = "${MIDDLEWARE_ARCH}"

DEPENDS:append = " \
    cjson \
    curl \
    jsoncpp \
    dbus \
    glib-2.0 \
    barton-matter \
    mbedtls \
    libcertifier \
    libxml2 \
    linenoise \
"
#otbr-agent \ barton depends on

RPROVIDES_${PN} += "barton"

SRC_URI = "git://git@github.com/rdkcentral/BartonCore.git;protocol=ssh;name=barton;nobranch=1"
SRCREV = "baef4966c3d844da2f5073543f5c7cf8e394e69c"

SRC_URI += "file://0001-commit-for-patch.patch"
S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE = "\
    -DBDS_BUILD_REFERENCE=OFF \
    -DBDS_GEN_GIR=OFF \
    -DBDS_MATTER_LIB=BartonMatter \
    -DBUILD_TESTING=OFF \
    -DBDS_THREAD=OFF \
    -DJsonCpp_INCLUDE_DIR=${STAGING_INCDIR}/json \
    -DBCORE_THREAD=OFF \
    -DBCORE_GEN_GIR=OFF \
"
CXXFLAGS += "-I${STAGING_INCDIR}/json"
do_install:append() {
    install -d ${D}${includedir}/barton

    # Install public API headers
    if [ -d ${S}/api/c/public ]; then
        cp -r --no-preserve=ownership ${S}/api/c/public/* ${D}${includedir}/barton/
    else
        echo "Warning: No public API headers found in ${S}/api/c/public"
        exit 1
    fi
}

# Define what goes in the main runtime package
FILES_${PN} = "${libdir}/libbrtnDeviceServiceShared.so.*"

# Ensure the dev package contains the public API headers
FILES_${PN}-dev += "${includedir}/barton/"

# Skip QA check for .so files in the -dev package
INSANE_SKIP_${PN}-dev += "dev-elf"
