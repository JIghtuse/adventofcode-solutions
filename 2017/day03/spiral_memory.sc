def odd_square(n: Int): Double =
  scala.math.pow(n * 2 + 1, 2)

def upper_odd(x: Int): Int =
  (0 to Int.MaxValue - 1).find(odd_square(_) >= x).get

def get_position(x: Int): (Int, Int) = {
  val u = upper_odd(x)
  val l = u - 1

  val upper = odd_square(u).toInt
  val lower = odd_square(l).toInt

  val n_sides = 4

  val step = (upper - lower) / n_sides

  val k = (1 to n_sides).find(n => upper - n * step <= x).get

  val x1 = (x - (upper - k * step - step / 2)).abs
  val x2 = (step - x1).abs

  (u, x1 min x2)
}

def get_distance(x: Int): Int = {
  val p = get_position(x)
  p._1 + p._2
}

get_distance(1)
get_distance(12)
get_distance(23)
get_distance(1024)
get_distance(289326)

// part2

def solve_part2(): Int = {
  val key_value = 289326

  val m = Array.ofDim[Int](1001, 1001)

  var x = m.length / 2
  var y = m.length / 2

  var y_direction = -1
  var x_direction = 1

  var next_diff = 1

  m(x)(y) = 1

  def update_value(): Unit = {
    m(x)(y) = m(x - 1)(y - 1) +
      m(x - 1)(y) +
      m(x - 1)(y + 1) +
      m(x)(y - 1) +
      m(x)(y + 1) +
      m(x + 1)(y - 1) +
      m(x + 1)(y) +
      m(x + 1)(y + 1)
  }

  while (true) {
    for (k <- 1 to next_diff) {
      x += x_direction
      update_value()
      if (m(x)(y) > key_value) {
        return m(x)(y)
      }
    }
    x_direction *= -1

    for (k <- 1 to next_diff) {
      y += y_direction
      update_value()
      if (m(x)(y) > key_value) {
        return m(x)(y)
      }
    }
    y_direction *= -1

    next_diff += 1
  }
  0
}

solve_part2
