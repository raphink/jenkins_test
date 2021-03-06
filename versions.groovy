// parse a version and return all versions to tag
def parse(version) {
  // [2.3.4-5, 2.3.4-5.build6]
  versions = [version, "${version}.build${env.BUILD_NUMBER}"]

  // [2.3.4, 5]
  tokens = version.tokenize("-")
  upstream_tokens = tokens[0].tokenize(".")

  // [2.3.4-5, 2.3.4-5.build6, 2, 2.3, 2.3.4]
  for (int i=0; i<upstream_tokens.size(); ++i) {
    // createRange is blacklisted in Jenkinsfile
    // versions << upstream_tokens[0..i].join(".")
    version = upstream_tokens[0]
    for (int j=1; j<=i; ++j) {
      version += ".${upstream_tokens[j]}"
    }
    versions << version
  }

  return versions
}

// get the tag associated with the latest git commit
def getTag() {
  tag = sh(
    script: 'git describe --tags --exact-match || true',
    returnStdout: true
  ).trim()
  return tag
}

// get the tag associated with the latest git commit
// and return all versions to tag
def parseTag() {
  tag = getTag()
  if (tag) {
    return parse(tag)
  } else {
    return []
  }
}

// push all tags for given container
def pushContainer(cont) {
  v = parseTag()
  for (int i=0; i<v.size(); ++i) {
    cont.push(v[i])
  }
}

return this;
