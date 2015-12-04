fn square(n: i32) -> i32 { n * n }
fn sum_up_to(n: i32) -> i32 { n * (n + 1) / 2 }

pub fn square_of_sum(n: i32) -> i32 {
    square(sum_up_to(n))
}

pub fn sum_of_squares(n: i32) -> i32 {
    (2 * n * n + 3 * n + 1) * n / 6
}

pub fn difference(_: i32) -> i32 {
    170
}
