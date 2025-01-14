import scala.io.Source

def read_data(input_file_path: String): Array[Array[Int]] = {
  Source.fromFile(input_file_path).getLines.map(s => s.split("\\s").map(_.toInt)).toArray
}


def checksum(spreadsheet: Array[Array[Int]]): Int =
  spreadsheet.map(xs => xs.max - xs.min).sum


def checksum2(spreadsheet: Array[Array[Int]]): Int = {
  def evenlyDivisible(xs: Array[Int]): Boolean = xs.max % xs.min == 0
  def maxDivMin(xs: Array[Int]): Int = xs.max / xs.min

  def lineResult(line: Array[Int]): Int =
    line.combinations(2).toArray.filter(evenlyDivisible).map(maxDivMin).sum

  spreadsheet.map(lineResult).sum
}

val data = read_data("input.txt")
println(checksum(data))
println(checksum2(data))

