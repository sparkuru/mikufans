#!/usr/bin/env bash
# Install the Android SDK required by app/build.gradle.kts inside ./hako.
set -Eeuo pipefail

readonly TOOLS_VERSION=15859902
readonly TOOLS_SHA256=4e4c464f145a7512b57d088ac6c278c03c9eea610886b35a5e0804e74eedf583
readonly TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-${TOOLS_VERSION}_latest.zip"
temp_dir=''

usage() {
    printf 'Usage: ./hako bash scripts/setup-android-sdk.sh\n' >&2
}

die() {
    printf '[android-sdk] %s\n' "$*" >&2
    exit 1
}

require_command() {
    command -v "$1" >/dev/null 2>&1 || die "required command not found: $1"
}

cleanup() {
    case "$temp_dir" in
    /tmp/mikufans-android-sdk.*) rm -rf -- "$temp_dir" ;;
    '') ;;
    *) printf '[android-sdk] refusing to remove unexpected path: %s\n' "$temp_dir" >&2 ;;
    esac
}

install_command_line_tools() {
    local sdk_home=$1
    local destination=${sdk_home}/cmdline-tools/latest
    local archive

    [[ -x ${destination}/bin/sdkmanager ]] && return 0
    [[ ! -e $destination ]] || die "incomplete command-line tools at $destination"

    temp_dir=$(mktemp -d /tmp/mikufans-android-sdk.XXXXXXXXXX)
    trap cleanup EXIT
    archive=${temp_dir}/commandlinetools.zip

    printf '[android-sdk] downloading Android command-line tools %s\n' "$TOOLS_VERSION" >&2
    curl --fail --location --retry 3 --output "$archive" "$TOOLS_URL"
    printf '%s  %s\n' "$TOOLS_SHA256" "$archive" | sha256sum --check --status ||
        die 'Android command-line tools checksum mismatch'
    unzip -q "$archive" -d "$temp_dir"
    [[ -x ${temp_dir}/cmdline-tools/bin/sdkmanager ]] ||
        die 'the downloaded archive does not contain sdkmanager'

    mkdir -p -- "${sdk_home}/cmdline-tools"
    mv -- "${temp_dir}/cmdline-tools" "$destination"
}

main() {
    local sdk_home=${ANDROID_HOME:-}
    local sdk_manager
    local command

    if [[ ${1:-} == --help || ${1:-} == -h ]]; then
        usage
        return 0
    fi
    [[ $# -eq 0 ]] || {
        usage
        return 2
    }
    [[ $sdk_home == /app/.devhome/android-sdk ]] ||
        die 'run this script through ./hako'
    for command in curl sha256sum unzip mktemp mkdir mv rm yes; do
        require_command "$command"
    done

    install_command_line_tools "$sdk_home"
    sdk_manager=${sdk_home}/cmdline-tools/latest/bin/sdkmanager
    printf '[android-sdk] installing API 37 tools and accepting their required licenses\n' >&2
    if ! (
        set +o pipefail
        yes | "$sdk_manager" --sdk_root="$sdk_home" --install \
            'platform-tools' 'platforms;android-37.0' 'build-tools;37.0.0'
    ); then
        die 'failed to install Android SDK packages'
    fi
}

main "$@"
