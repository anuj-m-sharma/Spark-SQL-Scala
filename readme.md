# Creating a Jar file using Spark and Scala and executing it on AWS (Hive). 
## Follow the below steps



Scala Environment SetUp

Download the following:
1. Scala:
   Go to website http://www.scala-lang.org/download/ and download Scala binaries from 'Other ways to install Scala'
   Install Scala in the laptop
   Set environment variable (under system variables), set a path to scala/bin folder
   (NOTE: To set the environment variables, in Windows :
   Right click on My Computer -> Select Properties -> 'Select Advanced system Settings' -> Select 'Environment Variables' button ->
   Under 'System Variables', select PATH and click on 'Edit button' -> Click on 'New button' and add the desired path and give OK)
   in Macintosh :
   See instructions here: http://osxdaily.com/2015/07/28/set-enviornment-variables-mac-os-x/
   )

2. Scala-IDE
   Go to website https://www.jetbrains.com/idea/download/#section=windows and download "Community Version .exe file".

3. Hadoop packages:
   Go to website https://github.com/srccodes/hadoop-common-2.2.0-bin and download the .zip file
   Unzp the .zip file
   Copy the hadoop-common-2.2.0-bin-master, which contains a bin folder, to any desired location. (Copy it to D:\hadoop)
   (NOTE: While programming, we need this location.)

4. Make sure the environment variable for Java ( java/bin folder ) is also set like Scala see Step 1. (NOTE: Try to isntall JDK  1.8 or Java 8 for Hadoop)
   You can check java version installed in your system by opening command prompt and typing java -version.

5. Launch AWS Spark Cluster:
   5.1. Log-in to the AWS account
   5.2. Create a new cluster and add Spark to the Software configuration
   5.3. Give required Hardware configuration (Cheapest one is m4.large) and number of instances (2 is sufficient for this Exercise)
   5.4. Start the cluster
   5.5. Once the cluster is started, login to the master node using Putty(Windows) or SSH(Mac or Linux)
   5.5. Create a data bucket in AWS S3 for storing all data

   (Video to setup AWS cluster: https://www.youtube.com/watch?v=_A_xEtd2OIM)

Part-2
Creating a New Maven Project using IntelliJ

1. Open IntelliJ -> Press "ctrl+alt+s" -> Plugins -> Install "Scala".
2. Click File-> New-> Project -> Maven and select Next.
3. Click on Artifact Coordinates. Set 'Group Id' as 'org.SparkSQL' and 'Artifact Id' as 'SparkSQLScala'. Click Finish.
4. Right click on newly created project -> Add Frameworks Support. Choose Scala -> Configure ->  Click '+' -> Navigate to where
   you installed scala. Go to scala-2.13.8/lib. Choose scala-compiler.jar,scala-library.jar and scala-reflect.jar.
5. Inside the project, right click on src/main/java, select Refactor and select Rename. Rename 'java' as 'scala'.
6. Inside the project, right click on src/test/java, select Refactor and select Rename. Rename 'java' as 'scala'.
7. Open pom.xml and paste the following code.

``` <?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>
    <groupId>org.SparkSQL</groupId>
    <artifactId>SparkSQLScala</artifactId>
    <version>1.0-SNAPSHOT</version>
    <dependencies>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_2.11</artifactId>
            <version>2.2.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_2.11</artifactId>
            <version>2.2.1</version>
        </dependency>
    </dependencies>

    <build>
        <sourceDirectory>src/main/scala</sourceDirectory>
        <testSourceDirectory>src/test/scala</testSourceDirectory>
        <plugins>
        <plugin>
            <groupId>net.alchim31.maven</groupId>
            <artifactId>scala-maven-plugin</artifactId>
            <version>3.2.1</version>
            <configuration>
                <mainClass>Driver</mainClass>
                <scalaCompatVersion>2.11</scalaCompatVersion>
            </configuration>
            <executions>
                <execution>
                    <phase>compile</phase>
                    <goals>
                        <goal>compile</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        </plugins>
    </build>
</project>
```

8. Refresh pom.xml file.
9. Right click on src/main/scala and select New -> Package. Name the package as 'org.SparkSQL'(Same name as you have given for 'Group Id')
   10.Right click on the package(org.SparkSQL) and select New -> Scala Class -> Object. Name it as 'org.SparkSQL.Driver'.

Find Spark programming tutorials here: http://spark.apache.org/docs/latest/programming-guide.html

Part-3
Use SparkSQL Library to load Data into a Table , and run SQL queries on it

1. Copy the following code into Driver.scala

```
package org.SparkSQL

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

case class Car(buying:String, maint:String, doors:String, persons:String, lug_boot:String, safety:String, car_class:String)

object Driver {
def main(args: Array[String]): Unit = {
//Set Hadoop home directory
System.setProperty("hadoop.home.dir", "D:\\anujsharma\\hadoop\\")  //Change the location here
//Setting up Spark configurations
val conf = new SparkConf().setAppName("SparkAction").setMaster("local")
val sc = new SparkContext(conf)
val sqlc = new SQLContext(sc)
//Loading a data file
val loadFile = sc.textFile(args(0))
val fileRDD = loadFile.map { line => line.split(",") }
//Mapping the data into an RDD
val carRDD = fileRDD.map { car => Car(car(0),car(1),car(2),car(3),car(4),car(5),car(6)) }
import sqlc.implicits._
//Converting the RDD into a dataframe
val carsDF = carRDD.toDF()
carsDF.registerTempTable("Cars") // Creating a table from the dataframe
//Querying the table
val queryDF = sqlc.sql("select * from Cars where buying='vhigh' and doors like '%more' and car_class='unacc'")
val queryRDD = queryDF.rdd
queryRDD.saveAsTextFile(args(1)) //Storing the result as a textfile
}
}
```

In the program, line number 12,
System.setProperty("hadoop.home.dir", "location of bin folder of Hadoop you saved in step 3 of Part 1")
Follow the same pattern for the folder location.

2. On the top right corner you should see Maven. Click on Maven -> SparkSQLScala -> Lifecycle. Select 'Install'.
3. You should see a new folder named Target in your project. Click on it. A jar file with the name SparkSQLScala-1.0-SNAPSHOT.jar
   will get created. Right click on it choose 'Open in' -> Explorer. Rename the .jar file as 'SparkSQLScala.jar'
4. Download Car Evaluation Data from http://webpages.uncc.edu/aatzache/ITCS6162/Project/Data/CarEvaluationData/CarData.zip.
5. Upload data.txt and SparkSQLScala.jar files to your AWS S3 data bucket.
6. From the master node download SparkSQLScala.jar using the command:
   aws s3 cp s3://BUCKET_NAME/SparkSQLScala.jar .
7. Run the program using the command:
   spark-submit --class org.SparkSQL.Driver ./SparkSQLScala.jar s3://BUCKET_NAME/data.txt s3://BUCKET_NAME/SparkSQLOutput
8. Download the output folder(SparkSQLOutput) from S3 to your local machine.
9. Upload the downloaded folder to Canvas.
   10.Save all commands in your Terminal Window in a Text File , including your login and username until the last command, and upload to Canvas.
   11.Terminate the AWS cluster and delete all files from S3 when finished, otherwise Amazon will charge your Credit Card :) .









