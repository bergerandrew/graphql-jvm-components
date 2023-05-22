Uses GraphQL-Java and Federation JVM to print a `schema.graphqls` file from *.graphqls resources.  
**Note:**
This plugin will generate a valid schema _only_ if your project does not generate any schema from code.  

## How to use
```
<plugin>
    <groupId>me.andrewberger.graphql</groupId>
    <artifactId>printschema-nocode-maven-plugin</artifactId>
    <version>1.14</version>
    <configuration>
        <schemaFolderPath>/schema/</schemaFolderPath>
        <outputFolderPath>/</outputFolderPath>
    </configuration>
    <executions>
        <execution>
            <phase>compile</phase>
            <goals>
                <goal>print-schema</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
