#!/usr/bin/env groovy
@NonCPS
boolean call(String value) {
	def testValue = "/^"+value+"/"
	for (changeLogSet in currentBuild.changeSets) { 
		for (entry in changeLogSet.getItems()) { // for each commit in the detected changes
			for (file in entry.getAffectedFiles()) {
				if (file.getPath() ==~ testValue) {
					printf("+ %s ==~ %s",file.getPath(),testValue)
					return true
				}
				else
					printf("- %s ==~ %s",file.getPath(),testValue)
			}
		}
	}
	print("scmChanges false")
	return false
}