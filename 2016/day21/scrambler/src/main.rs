use std::fs::File;
use std::io::{BufRead, BufReader};

pub fn swap_letters(mut s: &mut [u8], i: u8, j: u8) {
    let i = s.iter().position(|&x| x == i).unwrap();
    let j = s.iter().position(|&x| x == j).unwrap();
    s.swap(i, j);
}

pub fn rotate_right(s: &mut [u8], times: usize) {
    let len = s.len();

    for _ in 0..times {
        let t = s[len - 1];
        for i in (0..len - 1).rev() {
            s[i + 1] = s[i];
        }
        s[0] = t;
    }
}

pub fn rotate_left(s: &mut [u8], times: usize) {
    let len = s.len();

    for _ in 0..times {
        let t = s[0];
        for i in 0..len - 1 {
            s[i] = s[i + 1];
        }
        s[len - 1] = t;
    }
}

pub fn reverse_span(s: &mut [u8], i: usize, j: usize) {
    let j = j + 1;
    for k in 0..(j - i) / 2 {
        s.swap(k + i, j - k - 1);
    }
}

pub fn move_position(s: &mut [u8], i: usize, j: usize) {
    if i < j {
        let first = s[i];
        for k in i..j {
            s[k] = s[k + 1];
        }
        s[j] = first;
    } else {
        let last = s[i];
        for k in (j..i).rev() {
            s[k + 1] = s[k];
        }
        s[j] = last;
    }
}

pub fn rotate_on_position_right(s: &mut [u8], c: u8)
{
    let pos = s.iter().position(|&x| x == c).unwrap();
    let times = if pos >= 4 {
        pos + 1 + 1
    } else {
        pos + 1
    };
    rotate_right(s, times);
}

pub fn rotate_on_position_left(s: &mut [u8], c: u8)
{
    let pos = s.iter().position(|&x| x == c).unwrap();
    let times = match pos {
        0 | 1 => 1,
        2 => 6,
        3 => 2,
        4 => 7,
        5 => 3,
        6 => 0,
        7 => 4,
        _ => panic!("unexpected input"),
    };
    rotate_left(s, times);
}

fn scramble_password(initial: &str, filename: &str) -> String {
    let mut s = String::from(initial);
    let mut s = unsafe { s.as_mut_vec() };

    let file = File::open(filename).unwrap();
    let reader = BufReader::new(file);

    for line in reader.lines() {
        let line = line.unwrap();
        let words : Vec<_> = line.split_whitespace().collect();
        match (words[0], words[1]) {
            ("move", "position") => {
                let i : usize = words[2].parse().unwrap();
                let j : usize = words[5].parse().unwrap();
                move_position(&mut s, i, j);
            },
            ("reverse", "positions") => {
                let i : usize = words[2].parse().unwrap();
                let j : usize = words[4].parse().unwrap();
                reverse_span(&mut s, i, j);
            },
            ("swap", "position") => {
                let i : usize = words[2].parse().unwrap();
                let j : usize = words[5].parse().unwrap();
                s.swap(i, j);
            },
            ("rotate", "right") => {
                let times : usize = words[2].parse().unwrap();
                rotate_right(&mut s, times);
            },
            ("rotate", "left") => {
                let times : usize = words[2].parse().unwrap();
                rotate_left(&mut s, times);
            },
            ("swap", "letter") => {
                let i = words[2].as_bytes()[0];
                let j = words[5].as_bytes()[0];
                swap_letters(&mut s, i, j);
            },
            ("rotate", "based") => {
                let c = words[6].as_bytes()[0];
                rotate_on_position_right(&mut s, c);
            },
            _ => panic!("{} {}", words[0], words[1]),
        }
    }
    let s = s.clone();
    String::from_utf8(s).unwrap()
}

fn unscramble_password(initial: &str, filename: &str) -> String {
    let mut s = String::from(initial);
    let mut s = unsafe { s.as_mut_vec() };

    let file = File::open(filename).unwrap();
    let reader = BufReader::new(file);

    let lines : Vec<_> = reader.lines().map(|s| s.unwrap()).collect();

    for line in lines.iter().rev() {
        let words : Vec<_> = line.split_whitespace().collect();
        match (words[0], words[1]) {
            ("move", "position") => {
                let i : usize = words[5].parse().unwrap();
                let j : usize = words[2].parse().unwrap();
                move_position(&mut s, i, j);
            },
            ("reverse", "positions") => {
                let i : usize = words[2].parse().unwrap();
                let j : usize = words[4].parse().unwrap();
                reverse_span(&mut s, i, j);
            },
            ("swap", "position") => {
                let i : usize = words[2].parse().unwrap();
                let j : usize = words[5].parse().unwrap();
                s.swap(i, j);
            },
            ("rotate", "right") => {
                let times : usize = words[2].parse().unwrap();
                rotate_left(&mut s, times);
            },
            ("rotate", "left") => {
                let times : usize = words[2].parse().unwrap();
                rotate_right(&mut s, times);
            },
            ("swap", "letter") => {
                let i = words[2].as_bytes()[0];
                let j = words[5].as_bytes()[0];
                swap_letters(&mut s, i, j);
            },
            ("rotate", "based") => {
                let c = words[6].as_bytes()[0];
                rotate_on_position_left(&mut s, c);
            },
            _ => panic!("{} {}", words[0], words[1]),
        }
    }
    let s = s.clone();
    String::from_utf8(s).unwrap()
}

fn main() {
    let scrambled = scramble_password("abcdefgh", "input.txt");
    println!("{}", scrambled);

    let unscrambled = unscramble_password("fbgdceah", "input.txt");
    println!("{}", unscrambled);
}

#[cfg(test)]
mod tests {
    use super::{swap_letters, rotate_left, rotate_right};
    use super::{reverse_span, move_position, rotate_on_position_right};

    #[test]
    fn swap_letter_works() {
        let mut s = String::from("ebcda");
        let mut s = unsafe { s.as_mut_vec() };

        let mut expected = String::from("edcba");
        let expected = unsafe { expected.as_mut_vec() };

        swap_letters(&mut s, b'd', b'b');
        assert_eq!(expected, s);
    }

    #[test]
    fn rotate_left_works() {
        let mut s = String::from("abcd");
        let mut s = unsafe { s.as_mut_vec() };

        let mut expected = String::from("bcda");
        let expected = unsafe { expected.as_mut_vec() };

        rotate_left(&mut s, 1);
        assert_eq!(expected, s);

        let mut expected = String::from("abcd");
        let expected = unsafe { expected.as_mut_vec() };

        rotate_left(&mut s, 3);
        assert_eq!(expected, s);
    }

    #[test]
    fn rotate_right_works() {
        let mut s = String::from("abcd");
        let mut s = unsafe { s.as_mut_vec() };

        let mut expected = String::from("dabc");
        let expected = unsafe { expected.as_mut_vec() };

        rotate_right(&mut s, 1);
        assert_eq!(expected, s);

        let mut expected = String::from("abcd");
        let expected = unsafe { expected.as_mut_vec() };

        rotate_right(&mut s, 3);
        assert_eq!(expected, s);
    }

    #[test]
    fn reverse_span_works() {
        let mut s = String::from("edcba");
        let mut s = unsafe { s.as_mut_vec() };

        let mut expected = String::from("abcde");
        let expected = unsafe { expected.as_mut_vec() };

        reverse_span(&mut s, 0, 4);
        assert_eq!(expected, s);

        let mut expected = String::from("acbde");
        let expected = unsafe { expected.as_mut_vec() };

        reverse_span(&mut s, 1, 2);
        assert_eq!(expected, s);
    }

    #[test]
    fn move_position_works() {
        let mut s = String::from("bcdea");
        let mut s = unsafe { s.as_mut_vec() };

        let mut expected = String::from("bdeac");
        let expected = unsafe { expected.as_mut_vec() };

        move_position(&mut s, 1, 4);
        assert_eq!(expected, s);

        let mut expected = String::from("abdec");
        let expected = unsafe { expected.as_mut_vec() };

        move_position(&mut s, 3, 0);
        assert_eq!(expected, s);
    }

    #[test]
    fn rotate_on_position_works() {
        let mut s = String::from("abdec");
        let mut s = unsafe { s.as_mut_vec() };

        let mut expected = String::from("ecabd");
        let expected = unsafe { expected.as_mut_vec() };

        rotate_on_position_right(&mut s, b'b');
        assert_eq!(expected, s);

        let mut expected = String::from("decab");
        let expected = unsafe { expected.as_mut_vec() };

        rotate_on_position_right(&mut s, b'd');
        assert_eq!(expected, s);
    }

    #[test]
    fn scramble_works() {
        let mut s = String::from("abcde");
        let mut s = unsafe { s.as_mut_vec() };

        s.swap(4, 0);
        swap_letters(&mut s, b'd', b'b');
        reverse_span(&mut s, 0, 4);
        rotate_left(&mut s, 1);
        move_position(&mut s, 1, 4);
        move_position(&mut s, 3, 0);
        rotate_on_position_right(&mut s, b'b');
        rotate_on_position_right(&mut s, b'd');

        assert_eq!("decab", String::from_utf8(s.clone()).unwrap());
    }
}
