extern crate regex;

use std::io::{BufRead, BufReader};
use std::fs::File;
use std::path::Path;
use regex::Regex;

fn read_data<P: AsRef<Path>>(fname: P) -> Vec<String> {
    let file = File::open(fname).unwrap();
    BufReader::new(file).lines().map(|s| s.unwrap()).collect()
}

fn get_sequence_length_format_v1(marker_re: &Regex, line: &str) -> usize {
    let mut pos = 0;
    let mut new_line = String::new();
    for capture in marker_re.captures_iter(line) {
        let (cap_begin, cap_end) = capture.pos(0).unwrap();

        if cap_begin < pos {
            continue;
        }

        new_line.push_str(&line[pos..cap_begin]);

        let length: usize = capture[1].parse().unwrap();
        let times: usize = capture[2].parse().unwrap();

        let replacement = &line[cap_end..length + cap_end];

        for _ in 0..times {
            new_line.push_str(replacement);
        }

        pos = length + cap_end;
    }
    new_line.push_str(&line[pos..line.len()]);
    new_line.len()
}

fn get_sequence_length_format_v2(marker_re: &Regex, line: &str) -> usize {
    if !marker_re.is_match(line) {
        line.len()
    } else {
        let capture = marker_re.captures(line).unwrap();
        let capture_pos = capture.pos(0).unwrap();

        let length: usize = capture[1].parse().unwrap();
        let times: usize = capture[2].parse().unwrap();

        let slice_current = &line[capture_pos.1..capture_pos.1 + length];
        let slice_end = &line[capture_pos.1 + length..line.len()];

        capture_pos.0 + times * get_sequence_length_format_v2(marker_re, slice_current) +
        get_sequence_length_format_v2(marker_re, slice_end)
    }
}

fn main() {
    let marker_re = Regex::new(r"\((\d+)x(\d+)\)").unwrap();
    let data = read_data("input.txt");

    for line in &data {
        println!("{}", get_sequence_length_format_v1(&marker_re, line));
    }

    for line in &data {
        println!("{}", get_sequence_length_format_v2(&marker_re, line));
    }
}
