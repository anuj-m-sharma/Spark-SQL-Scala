package org.SparkSQL

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

case class Car(buying:String, maint:String, doors:String, persons:String, lug_boot:String, safety:String, car_class:String)

object Driver {
  def main(args: Array[String]): Unit = {
    //Set Hadoop home directory
    System.setProperty("hadoop.home.dir", "D:\\hadoop\\")
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
