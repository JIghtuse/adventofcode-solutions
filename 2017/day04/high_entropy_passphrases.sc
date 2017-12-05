import scala.io.Source

def read_data(input_file_path: String): Array[Array[String]] = {
  Source.fromFile(input_file_path).getLines.map(s => s.split("\\s")).toArray
}

val data = read_data("input.txt")

data.map(ss => ss.toSet.size == ss.size).count(identity)

data.map(ss => ss.map(_.sorted).toSet.size == ss.size).count(identity)

