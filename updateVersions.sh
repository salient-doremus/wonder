#!/bin/bash

# echo "Step1: Remove -SNAPSHOT from version number"
# sh updateVersions release
# echo "Step2: mvn deploy"
# mvn deploy
# echo "Step3: Increase version and add -SNAPSHOT"
# sh updateVersions patch
# sh updateVersions minor
# sh updateVersions major


source wonderversion.properties
OLD_VERSION=$cfBundleShortVersion

case $1 in
  release)
    NEW_VERSION=$(sh Tools/semver bump release $OLD_VERSION)
  ;;
  patch)
    NEW_VERSION=$(sh Tools/semver bump patch $OLD_VERSION)
    NEW_VERSION=$(sh Tools/semver bump prerel SNAPSHOT $NEW_VERSION)
  ;;
  minor)
    NEW_VERSION=$(sh Tools/semver bump minor $OLD_VERSION)
    NEW_VERSION=$(sh Tools/semver bump prerel SNAPSHOT $NEW_VERSION)
  ;;
  major)
    NEW_VERSION=$(sh Tools/semver bump major $OLD_VERSION)
    NEW_VERSION=$(sh Tools/semver bump prerel SNAPSHOT $NEW_VERSION)
  ;;
  *)
    echo "Unknown argument: Use 'release', 'patch', 'minor' or 'major'";
    echo "Current version: $OLD_VERSION"
    exit 0
  ;;
esac


find . -type f -name 'pom.xml' -exec sed -i '' "s/$OLD_VERSION/$NEW_VERSION/g" {} \;
sed -i '' "s/$OLD_VERSION/$NEW_VERSION/g" wonderversion.properties

echo "Done upgrading from $OLD_VERSION to $NEW_VERSION"
