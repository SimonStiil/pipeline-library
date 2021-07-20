#!/usr/bin/env groovy

boolean call() {
	return currentBuild.getPreviousBuild() == null
}