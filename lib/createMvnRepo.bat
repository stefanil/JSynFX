REM
REM <repositories>
REM    <repository>
REM        <id>lib</id>
REM        <url>file://${project.basedir}/lib</url>
REM    </repository>
REM </repositories>
REM <dependencies>
REM   <dependency>
REM       <groupId>com.jsyn</groupId>
REM       <artifactId>JSyn</artifactId>
REM       <version>16.6.4</version>
REM   </dependency>
REM </dependencies>
REM

mvn install:install-file -Dfile=jsyn-beta-16.6.4.jar -DgroupId=com.jsyn -DartifactId=JSyn -Dversion=16.6.4 -Dpackaging=jar -DlocalRepositoryPath=.