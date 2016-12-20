use std::fs::File;
use std::io::{BufRead, BufReader};

fn get_ranges(fname: &str) -> Vec<(u32, u32)> {
    let file = File::open(fname).unwrap();
    let reader = BufReader::new(file);
    let mut ranges : Vec<_> = reader.lines().map(|s| {
        let s = s.unwrap();
        let mut words = s.split('-');
        let begin = words.next().unwrap().parse().unwrap();
        let end = words.next().unwrap().parse().unwrap();
        (begin, end)
    }).collect();
    ranges.sort();
    ranges
}

fn is_blocked(n: u32, ranges: &[(u32, u32)]) -> bool {
    for range in ranges {
        if range.0 <= n && n <= range.1 {
            return true;
        }
    }
    false
}

fn get_first_non_blocked_ip(ranges: &[(u32, u32)]) -> u32 {
    for i in 0u32.. {
        if !is_blocked(i, ranges) {
            return i
        }
    }
    unreachable!()
}

fn count_non_blocked_addresses(ranges: &[(u32, u32)]) -> u32 {
    let mut count = 0;
    let mut begin = 0;
    for range in ranges.iter() {
        if range.0 > begin {
            count += range.0 - begin - 1;
        }
        if range.1 > begin {
            begin = range.1;
        }
    }
    count
}

fn main() {
    let ranges = get_ranges("input.txt");

    let first_ip = get_first_non_blocked_ip(&ranges);
    println!("{}", first_ip);

    let address_count = count_non_blocked_addresses(&ranges);
    println!("{}", address_count);
}
