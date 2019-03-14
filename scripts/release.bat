mvn release:prepare -Prelease -Darguments=-Dgpg.passphrase=thephrase
mvn release:perform -Prelease -Darguments=-Dgpg.passphrase=thephrase