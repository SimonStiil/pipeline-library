#!/usr/bin/env groovy
@NonCPS
boolean call(String value) {
	def testValue = "^"+value
	if (testValue.contains("/"))
		testValue = testValue.replace('/','\\/')
	if (testValue.endsWith("/"))
		testValue += '.*'
	for (changeLogSet in currentBuild.changeSets) {
		for (entry in changeLogSet.getItems()) { // for each commit in the detected changes
			for (file in entry.getAffectedFiles()) {
				if (file.getPath() ==~ testValue) {
					return true
				}
			}
		}
	}
	return false
}
