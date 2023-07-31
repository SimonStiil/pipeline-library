#!/usr/bin/env groovy
def call() {
    if (env.BRANCH_NAME && env.TAG_NAME)
      return true
    return false
}
