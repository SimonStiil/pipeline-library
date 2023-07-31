#!/usr/bin/env groovy
def call() {
    if (env.TAG_NAME)
      return env.TAG_NAME
    return "HEAD"
}