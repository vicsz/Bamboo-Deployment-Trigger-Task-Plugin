## Description

Bamboo Task Plugin to allow for Triggering of Deployments.

Intended for older versions of Bamboo (5.6)

## Requirements

Requirements include having the atlassian SDK installed.
https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project

Note, that this plugin requires an older change of the SDK (specifically 5)

brew tap atlassian/tap
brew search atlassian-plugin-sdk
brew install atlassian/tap/atlassian-plugin-sdk5

# For IDE complication / editing you will need to manual add Class Path to download Bamboo directires
the lib directory is at : /target/bamboo/webapp/WEB-INF/lib

# Steps

(After install the Atlassian SDK)

1. From commandline run: *atlas-run*
2. Go to : localhost:6990/bamboo/
3. Default login is admin/admin
4. Plugin will be installed by default (in Bamboo).

## TODO
1. Add Validation of Deployment Project / and Environment Name -- (save as id, and not name)
2. Wait on Deployment to finish for task to finish.

## Issues

Note broken backwards compatibility with Deployment Execution Service in version 5.9.

https://jira.atlassian.com/browse/BAM-16289

## Debugging

Bamboo 5.6 does NOT work with Java 1.8

So you will need to install Java 1.7, and configure atlas-run for 1.7  ( recommend using jenv on osx )