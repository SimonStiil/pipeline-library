#!/usr/bin/env groovy
@NonCPS
List<String> call(String value) {
	List<String> list = []
	for (changeLogSet in currentBuild.changeSets) {
		for (entry in changeLogSet.getItems()) { // for each commit in the detected changes
			for (file in entry.getAffectedFiles()) {
				list.add(file.getPath())
			}
		}
	}
	return list
}
