# Fejlődnek-e a build toolok? CI/CD hatásai, Maven és Gradle újdonságok

Fejlődnek-e a build toolok napjainkban?
Milyen változásokat hozott a CI/CD széleskörű elterjedése?
Maven vagy Gradle? Vannak-e új Maven pluginek? 
Mi az a wrapper, Maven extension, build cache, daemon? Mit hoz a Maven 4? 
Milyen új lehetőségek jelentek meg a Gradle legújabb verzióiban? 
Mire várhatunk a jövőben?

Csatlakozzatok hozzánk online, a ti tapasztalataitokra is kíváncsiak vagyunk!

Referencia:

* [The Current State of Apache Maven 4 - Development by Karl Heinz Marbaise](https://www.youtube.com/watch?v=68mbO92-Jfo)
* [Wait no more, here comes Maven 4! by Robert Scholte, Maarten Mulders](https://www.youtube.com/watch?v=P1nDlF2vg1I)
* [Apache Maven Project Team](https://maven.apache.org/team.html)

## Mikor melyiket? Maven vagy Gradle?

* Maven
  * De facto standard, szélesebb körben elterjedt
  * Nehéz testreszabni
  * Létező legjobb gyakorlatok
* Gradle
  * Nagyobb flexibilitás
  * Inkrementális build, csak a megváltozott osztályokat fordítja le újra
    * Ennek megfelelően bizonyos taskokat is képes átugorni
    * Multimodule projekteknél is működik: partial build
  * Nagyra növő multimodule projektekhez
  * Groovy DSL tömörebb, mint az XML
  * Android Build Tool alapja a Gradle

## Multimodule projekt?

* Microservice világ
* Modulith
    * Nagyobb hangsúly a csomagok közötti láthatóságon, lsd.: ArchUnit, Spring Modulith
* Overused, ezzel próbálják a laza kapcsolatot biztosítani, de nő a komplexitás és a build time
* Magyarázható:
    * Third party lib fejlesztőknél (pl. Spring, Quarkus - több, mint 700 modul)
    * Több artifacttal rendelkező alkalmazások, lsd. Twelve-Factor App (pl. közös réteggel rendelkező web alkalmazás és batch folyamat)

## CI/CD

* Minden build egy potenciális release
* CI-től build number
* Optimalizáció

## Maven

### `mvn clean install` vs. `mvn verify`

Régi Maven verziókban amennyiben multimodule projektnél volt a projektek között függőség, muszáj
volt a `clean install` kiadása, ugyanis ilyenkor került a local repo-ba az artifact, és innen tudta feloldani.

Azonban megjelent a Reactor, mely betölti a `pom.xml` fájlokat, és a projektről kialakít egy
in-memory mentális modellt. Ez viszont ismeri, hogy a függő projekthez elkészült az artifact
a `target` könyvtárban, és azt tudja használni.

Feltérképezi a függőségeket, és ez alapján határozza meg a sorrendet, ami nem feltétlenül egyezik a 
`pom.xml`-ben lévő `<modules>` sorrenddel.

`clean install` parancs hatására az alprojektenként sorra futtatja a `clean` majd `install` fázisokat.

A Mavenben van inkrementális build, melynek használatát a `clean` megakadályozza.

Van, amikor mégis kell `install`, workaround:

[Mock Repository Manager Maven Plugin](https://www.mojohaus.org/mrm/mrm-maven-plugin/)

Referencia:

* [Andres Almiray: Maven: verify or clean install?](https://andresalmiray.com/maven-verify-or-clean-install/)
* [Robert Scholte: maven-ci-extension](https://github.com/apache/maven-studies/tree/maven-ci-extension): 3. `mvn clean install` után töri a buildet
* [Andres Almiray: Maven related memes](https://github.com/aalmiray/mvn-clean-install)

![mvn verify](https://github.com/aalmiray/mvn-clean-install/blob/master/mvn73.jpg)

### Maven wrapper

Script a projektben, mely a Maven megfelelő verzióját is telepíti, ha kell.

```shell
 mvn wrapper:wrapper
```

```shell
mvn wrapper:wrapper -Dmaven=4.0.0-alpha-5
```

* `%HOMEPATH%\.m2\wrapper`

Referencia:

* [Maven Wrapper](https://maven.apache.org/wrapper/)

### Maven Daemon

Normál futtatás

```shell
mvn package
```

33s

Párhuzamos futtatás:

```shell
mvn -T 15 package
```

10s

* Beágyazott Maven
    * 2-es sorozat 4-es Mavennel
* Long living background process
* Natív, GraalVM-mel fordítva

Előnyök:

* Nem kell mindig betölteni a JVM-et
* Pluginek egyszer kerülnek betöltésre
* Parallel futtat
    * [Takari Smart Builder](https://github.com/takari/takari-smart-builder)
* Konzol output

```shell
mvnd package
```

8s

Referencia:

[mvnd - the Maven Daemon](https://github.com/apache/maven-mvnd)

### Maven extensions

Jobban bele lehet avatkozni a Maven működésébe, mint a pluginekkel. Pl. ki lehet cserélni a kommunikációt a remote repository-val való
kommunikációt (Wagon), vagy bele lehet avatkozni az életciklusba.

Maven esetén a konténer a Sisu, mely egy JSR330 (Dependency Injection for Java, pl. `@Inject` annotáció) implementáció.

Konfigurálható:

* Core Extension (Core Classloader): 
    * Registered via extension jar in `${maven.home}/lib/ext`
    * Registered via CLI argument `mvn -Dmaven.ext.class.path=extension.jar`
    * Registered via `.mvn/extensions.xml` file
* Build Extension (Project Classloader):
    * `pom.xml`

Referencia:

* [Apache Maven  Guide to using Extensions](https://maven.apache.org/guides/mini/guide-using-extensions.html)
* [Available Extensions](https://maven.apache.org/extensions/index.html)

### Build cache

* `.mvn/extensions.xml`
* `.mvn/maven-build-cache-config.xml`

* `%HOMEPATH%\.m2\build-cache`

### Enforcer extension

Ellenőrzi a környezetet és a projektet:

* [Enforcer Build-in Rules](https://maven.apache.org/enforcer/enforcer-rules/index.html)
    * Maven version, JDK, OS
    * `requireUpperBoundDeps` - ensures that every (transitive) dependency is resolved to its specified version or higher.
    * `dependencyConvergence` - ensure all dependencies converge to the same version
* [Extra Enforcer Rules](https://www.mojohaus.org/extra-enforcer-rules/)
    * `banDuplicateClasses` - verifies that there are no duplicate classes in the dependencies

![Enforcer](https://github.com/aalmiray/mvn-clean-install/blob/master/enforcer-02.jpg)

Extensionként használva: nem kell a `pom.xml`-be felvenni, globálisan lehet definiálni pom inheritence nélkül

### Maven Profiler

Referencia:

* [Maven Profiler](https://github.com/jcgay/maven-profiler)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
    <extension>
      <groupId>fr.jcgay.maven</groupId>
      <artifactId>maven-profiler</artifactId>
      <version>3.2</version>
    </extension>
</extensions>
```

```shell
mvnd package -Dprofile
```

### Maven OpenTelemetry Extension

Referencia:

* [Maven OpenTelemetry Extension](https://github.com/open-telemetry/opentelemetry-java-contrib/tree/main/maven-extension)

### Toolchain

* `pom.xml`
* `%HOMEPATH%\.m2\toolchains.xml`

Referencia:

* [Toolchain](https://maven.apache.org/guides/mini/guide-using-toolchains.html)

### Password Encyption

[Password Encyption](https://maven.apache.org/guides/mini/guide-encryption.html)

### Maven 4

* Open source projekt, nincs timeline

Ígéretek:

* A jelenlegi projektek módosítás nélkül lefut a build
* Több warning
* Következő major verzióban már lehet, hogy bukni fog a build

#### Warning

* Pluginek: nem a Maven része, külön tölti le, külön verziószámozhatók, külön release-elhetőek
* Super pom: `lib/maven-model-builder-3.9.9.jar` `org/apache/maven/model/pom-4.0.0.xml`
* Update-elve lettek
* Warningol, ha nem definiáljuk őket explicit módon

#### Fail on severity

`--fail-on-severity`: ilyen szintű log üzenet esetén elbukik a build

Vannak olyan pluginek, amik loggolnak, de nem buktatják a buildet

#### Checksum

Ha a letöltött artifact checksumja nem egyezik a publikált checksummal, Maven 3-ig warning volt.
Maven 4-től elbukik a build.

`-c` vagy `-lax-checksums` kapcsolóval visszaállítható az eredeti működés (miért akarnánk ilyent?)

#### Ismeretlen profile vagy module

Maven 3-ig warning.

Maven 4-től elbukik a build, valamint a build elején és végén is kiírja.

`-P?codecoverage` - opcionális, ebben az esetben nem bukik el.

Mikor lehet erre szükség? Ha standardizált pipeline-unk van, pl. másik team fejleszti.

#### Resume

* A `hello-frontend` modul elrontása, javítás után: `mvnw -rf :hello-frontend package`

Hiba: `Could not resolve dependencies for project training:hello-frontend:jar:1.0-SNAPSHOT`

Még sokkal rosszabb, ha a local repo-ból egy régi verziót talál meg.

Nincs benne a Reactorban.

Maven 4: Success (`target/resume.properties`)

#### CI Friendly versions

* Maven Release Plugin nem CI/CD kompatibilis

`${revision}`, `${sha1}`, `${changelist}`

```xml
<version>${revision}</version>
```

```shell
mvn -Drevision=1.0.0-1 install
```

`%HOMEPATH%\.m2\repository\training\helloworld\1.0.0-1\helloworld-1.0.0-1.pom`

[Flatten Maven Plugin](https://www.mojohaus.org/flatten-maven-plugin/)

Mik dolgozzák fel a `pom.xml` fájlt:

* Maven
* Repository manager
* Más build toolok, pl. Gradle, stb.
* CI servers
* Dependency updaters (Dependabot, Renovate)
* IDE-k

Nem lehet `pom.xml` verziót emelni.

Megoldás: _consumer pom:_

build pom -> enriched pom -> consumer pom

```shell
mvnw -Drevision=1.0.0-1 install
```

#### Változott séma

* `<project>`
* Nem kell `<modelVersion>`
* `<subprojects>`, `<subproject>`
* `<parent>` esetén nem kell `version`
* `<dependency>` esetén nem kell `groupId`, `version`

Release: egy helyen kell átírni

Repo-ban helyesen szerepel

#### Project local repository

```
[INFO] Copying training:hello-it:pom:1.0-SNAPSHOT to project local repository
[INFO] Copying training:hello-it:jar:1.0-SNAPSHOT to project local repository
[INFO] Copying training:hello-it:pom:consumer:1.0-SNAPSHOT to project local repository          
```

`target/project-local-repo`

#### BOM

* `pom.xml`, benne `<dependencyManagement>` tag
* Itt sem kell a verziót definiálni

#### Lifecycle phase-ek

[Lifecycles](https://maven.apache.org/ref/4.0.0-beta-5/maven-core/lifecycles.html)

Minden fázishoz van `pre-*` és `post-*`.

#### Activation: packaging

[activation](https://maven.apache.org/ref/4.0.0-beta-5/maven-model/maven.html#class_activation)

#### Plugin futtatás sorrendezés

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-resources-plugin</artifactId>
    <executions>
        <execution>
            <id>after-compile-200</id>
            <phase>after:compile[200]</phase>
            <goals>
                <goal>resources</goal>
            </goals>
        </execution>
        <execution>
            <id>after-compile-100</id>
            <phase>after:compile[100]</phase>
            <goals>
                <goal>resources</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### Console

Nincs 80 karakterre korlátozva

#### Plugin fejlesztőknek

* Immutable model
* Van Maven BOM

#### Belső fejlesztések

* Függőségek eltávolítása
* Függőségek frissítése
* Pár új függőség
* Több subproject

### Demo: Spring Boot projekt
