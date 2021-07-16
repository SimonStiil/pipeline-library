#!/usr/bin/env groovy
package dk.stiil.pipeline

import java.util.regex.Pattern

class Constants {
    final static Pattern RELEASE_PATTERN = Pattern.compile("release\\/\\d{1,3}\\.\\d{1,3}(\\.\\d{1,3})?")
    final static Pattern TAG_RELEASE_PATTERN = Pattern.compile("^v\\d{1,3}\\.\\d{1,3}(\\.\\d{1,3})?\$")
    final static Pattern TAG_RELEASE_CANDIDATE_PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,3}(\\.\\d{1,3})?(-rc-\\d{1,2})?")
    final static Pattern PULL_REQUEST_PATTERN = Pattern.compile("^PR-\\d{1,5}")
    final static Pattern FEATURE_PATTERN = Pattern.compile("^feature\\/.+")
    final static Pattern BUGFIX_PATTERN = Pattern.compile("^bugfix\\/.+/")
    final static Pattern MAIN_PATTERN = Pattern.compile('^main$|^master$')
}