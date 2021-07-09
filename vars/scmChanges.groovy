#!/usr/bin/env groovy
@NonCPS
boolean call(String value) {
	for (changeLogSet in currentBuild.changeSets) {
		for (entry in changeLogSet.getItems()) { // for each commit in the detected changes
			for (file in entry.getAffectedFiles()) {
				if (file.getPath().startsWith(value)) {
					return true
				}
			}
		}
	}
	return false
}
