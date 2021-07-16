#!/usr/bin/env groovy

import static dk.stiil.pipeline.Constants.*

def call() {
	def matcher = BRANCH_NAME =~ MAIN_PATTERN
	return matcher.find()
}