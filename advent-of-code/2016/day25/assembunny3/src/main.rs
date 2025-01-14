fn get_lowest_a() -> i32 {
    for mut a in 0.. {
        let initial_a = a;
        let mut v = vec![];
        let mut d = a;
        let mut c = 4;
        let mut b = 0;
        d += 643 * c;
        c = 0;

        'outer: loop {
            a = d;
            while a != 0 {
                b = a;
                a = 0;
                'a: loop {
                    c = 2;
                    while c != 0 {
                        if b != 0 {
                            b -= 1;
                            c -= 1;
                        } else {
                            break 'a;
                        }
                    }
                    a += 1;
                }
                b = 2;
                if c != 0 {
                    b -= c;
                    c = 0;
                }
                v.push(b);
                if v.len() >= 14 {
                    if v == [0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1] {
                        return initial_a;
                    } else {
                        break 'outer;
                    }
                }
            }
        }
    }
    0
}

fn main() {
    // assembunny high level

    let a = get_lowest_a();
    println!("{}", a);
}
