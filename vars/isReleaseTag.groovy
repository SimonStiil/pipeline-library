#!/usr/bin/env groovy

import static dk.ufst.devops.branchnaming.Constants.*

def call() {
	def matcher = BRANCH_NAME =~ TAG_RELEASE_PATTERN
	return matcher.find()
}