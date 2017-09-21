import org.slf4j.LoggerFactory

import java.util.regex.Pattern

final versions = new VersionProperties("version.properties")

new PomFile("pom.xml")
		.replaceFirst("<version>.*</version>", "<version>${versions.parent}</version>")
		.replaceFirst("<springmock.version>.*</springmock.version>", "<springmock.version>${versions.infrastructure}</springmock.version>")
		.save()

new PomFile("infrastructure/pom.xml")
		.replaceParentPomVersion(versions.parent)
		.replaceModuleVersion(versions.infrastructure)
		.save()

new PomFile("mockito/pom.xml")
		.replaceParentPomVersion(versions.parent)
		.replaceModuleVersion(versions.mockito)
		.save()

new PomFile("spock/pom.xml")
		.replaceParentPomVersion(versions.parent)
		.replaceModuleVersion(versions.spock)
		.save()

new MockitoSamplesPom("samples/mockito-samples/pom.xml")
		.updateSpringmockMockitoVersion(versions.mockito)
		.save()

new SpockSamplesBuildGradle("samples/spock-samples/build.gradle")
		.updateSpringmockSpockVersion(versions.spock)
		.save()

class VersionProperties {
	final parent
	final infrastructure
	final mockito
	final spock

	VersionProperties(String versionFileLocation) {
		final versionProperties = loadVersionProperties(versionFileLocation)
		this.parent = versionProperties["springmock-parent"]
		this.infrastructure = versionProperties["springmock-infrastructure"]
		this.mockito = versionProperties["springmock-mockito"]
		this.spock = versionProperties["springmock-spock"]
	}

	static Properties loadVersionProperties(String fileLocation) {
		final properties = new Properties()
		final propertiesFile = new File(fileLocation)
		propertiesFile.withInputStream {
			properties.load(it)
		}
		return properties
	}
}

abstract class VersionedFile {
	static final log = LoggerFactory.getLogger(getClass())

	final String location
	final String content

	VersionedFile(String location) {
		this(location, new File(location).getText('utf-8'))
	}

	protected VersionedFile(String location, String content) {
		this.content = content
		this.location = location
	}

	void save() {
		save(location)
	}

	void save(String location) {
		new File(location).setText(content, 'utf-8')
	}
}

class PomFile extends VersionedFile {
	PomFile(String location) {
		super(location)
	}

	protected PomFile(String location, String content) {
		super(location, content)
	}

	PomFile replaceFirst(String regexp, String replacement) {
		final matcher = Pattern.compile(regexp).matcher(content)
		if (matcher.find()) {
			log.info("$location first occurence of $regexp set to $replacement")
			return new PomFile(
					location,
					matcher.replaceFirst(replacement))
		} else {
			throw new IllegalStateException("Can not find $regexp in file $location")
		}
	}

	PomFile replaceParentPomVersion(String parentVersion) {
		final matcher = Pattern
				.compile("<parent>.*(<version>.*</version>).*</parent>", Pattern.DOTALL)
				.matcher(content)

		if (matcher.find()) {
			log.info("$location parent pom version set to $parentVersion")
			final int start = matcher.start(1)
			final int end = matcher.end(1)
			return new PomFile(
					location,
					content.substring(0, start) + "<version>$parentVersion</version>" + content.substring(end))
		} else {
			throw new IllegalStateException("Can not update parent pom version in $location")
		}
	}

	PomFile replaceModuleVersion(String newVersion) {
		final matcher = Pattern
				.compile("</parent>.*(<version>.+</version>)", Pattern.DOTALL)
				.matcher(content)

		if (matcher.find()) {
			log.info("$location version set to $newVersion")
			int start = matcher.start(1)
			int end = matcher.end(1)
			return new PomFile(
					location,
					content.substring(0, start) + "<version>$newVersion</version>" + content.substring(end))
		} else {
			throw new IllegalStateException("Can not update component version in $location")
		}
	}
}

class MockitoSamplesPom extends VersionedFile {
	MockitoSamplesPom(String location) {
		super(location)
	}

	private MockitoSamplesPom(String location, String content) {
		super(location, content)
	}

	MockitoSamplesPom updateSpringmockMockitoVersion(String newVersion) {
		final currentVersionRegexp = "</dependency>.*" +
				"<groupId>com.pchudzik.springmock</groupId>.*" +
				"<artifactId>springmock-mockito</artifactId>.*" +
				"(<version>.+</version>)"
		final matcher = Pattern.compile(currentVersionRegexp, Pattern.DOTALL).matcher(content)
		if (matcher.find()) {
			log.info("$location springmock-mockito version set to $newVersion")
			int start = matcher.start(1)
			int end = matcher.end(1)
			return new MockitoSamplesPom(
					location,
					content.substring(0, start) +
							"<version>$newVersion</version>" +
							content.substring(end))
		} else {
			throw new IllegalStateException("Can not find springmock-mockito dependency in $location")
		}
	}
}

class SpockSamplesBuildGradle extends VersionedFile {
	SpockSamplesBuildGradle(location) {
		super(location)
	}

	private SpockSamplesBuildGradle(location, content) {
		super(location, content)
	}

	SpockSamplesBuildGradle updateSpringmockSpockVersion(String newVersion) {
		final replacement = "testCompile('com.pchudzik.springmock:springmock-spock:$newVersion')"
		final matcher = Pattern.compile("testCompile\\('com.pchudzik.springmock:springmock-spock:.*'\\)").matcher(content)
		if (matcher.find()) {
			log.info("$location version of com.pchudzik.springmock:springmock-spock set to $newVersion")
			return new SpockSamplesBuildGradle(
					location,
					matcher.replaceFirst(replacement))
		} else {
			throw new IllegalStateException("Can not find $regexp in file $location")
		}
	}
}
