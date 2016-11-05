package com.databricks.apps.twitterClassifier

import java.io.File
import com.github.acrisci.commander.Program

abstract sealed case class ExamineAndTrainOptions(
  overWrite: Boolean = false,
  tweetDirectory: File = new File("~/sparkTwitter/tweets/"),
  modelDirectory: File = new File("~/sparkTwitter/modelDirectory/"),
  numClusters: Int = 10,
  numIterations: Int = 100
)

object ExamineAndTrainOptions {
  val _program = new Program()
    .version("2.0.0")
    .option(flags="-w, --overWrite", description="Overwrite model from a previous run [false]", default=false)
    .usage("ExamineAndTrain [Options] <tweetDirectory> <modelDirectory> <numClusters> <numIterations>")

  def parse(args: Array[String]): ExamineAndTrainOptions = {
    val program: Program = _program.parse(args)
    if (program.args.length!=program.usage.split(" ").length-2) program.help

    val options = new ExamineAndTrainOptions(
      overWrite = program.overWrite,
      tweetDirectory = new File(program.args.head),
      modelDirectory = new File(program.args(1)),
      numClusters = program.args(2).toInt,
      numIterations = program.args(3).toInt
    ){}
    import options._

    if (!tweetDirectory.exists) {
      System.err.println(s"${ tweetDirectory.getCanonicalPath } does not exist. Did you run Collect yet?")
      System.exit(-1)
    }
    if (modelDirectory.exists) {
      if (options.overWrite) {
        import org.apache.commons.io.FileUtils
        println("Replacing pre-existing model")
        FileUtils.deleteDirectory(modelDirectory)
      } else {
        System.err.println("Model already exists and --overWrite option was not specified")
        System.exit(-3)
      }
    }
    if (numClusters<1) {
      System.err.println(s"At least 1 clusters must be specified")
      System.exit(-3)
    }
    if (numIterations<1) {
      System.err.println(s"At least 1 iteration must be specified")
      System.exit(-4)
    }

    options
  }
}
