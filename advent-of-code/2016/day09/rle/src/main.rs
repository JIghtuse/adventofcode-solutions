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
    let mut len = 0;
    for capture in marker_re.captures_iter(line) {
        let re_match = capture.get(0).unwrap();
        let cap_begin = re_match.start();
        let cap_end = re_match.end();

        if cap_begin < pos {
            continue;
        }

        len += cap_begin - pos;

        let length: usize = capture[1].parse().unwrap();
        let times: usize = capture[2].parse().unwrap();

        len += times * length;
        pos = length + cap_end;
    }
    len + line.len() - pos
}

fn get_sequence_length_format_v2(marker_re: &Regex, line: &str) -> usize {
    if !marker_re.is_match(line) {
        line.len()
    } else {
        let capture = marker_re.captures(line).unwrap();
        let capture_pos = capture.get(0).unwrap();

        let length: usize = capture[1].parse().unwrap();
        let times: usize = capture[2].parse().unwrap();

        let slice_current = &line[capture_pos.end()..capture_pos.end() + length];
        let slice_end = &line[capture_pos.end() + length..line.len()];

        capture_pos.start() + times * get_sequence_length_format_v2(marker_re, slice_current) +
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
