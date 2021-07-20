#!/usr/bin/env groovy

import static dk.stiil.pipeline.Constants.*

def call() {
	def matcher = BRANCH_NAME =~ PULL_REQUEST_PATTERN
	return matcher.find()
}