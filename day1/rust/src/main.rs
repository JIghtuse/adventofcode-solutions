use std::fs::File;
use std::path::Path;
use std::io::BufReader;
use std::io::prelude::*;

fn read_input_file(input_file: &Path) -> Option<String> {
    match File::open(input_file) {
        Ok(file) => {
            let mut reader = BufReader::new(file);

            let mut line = String::new();
            reader.read_line(&mut line).unwrap();
            Some(line)
        }
        _ => None
    }
}

fn which_floor_at_the_end_fp() -> Option<i32> {
    read_input_file(Path::new("input"))
        .map(|s| s.chars()
                  .map(|x| if x == '(' { 1 } else { -1 })
                  .fold(0, |acc, x| acc + x))
}

fn which_floor_at_the_end() -> Option<i32> {
    match read_input_file(Path::new("input")) {
        None => None,
        Some(input) => {
            let mut floor = 0;
            for c in input.chars() {
                match c {
                    '(' => floor += 1,
                    ')' => floor -= 1,
                    _ => { println!("unexpected character!"); break; }
                }
            }
            Some(floor)
        },
    }
}

fn first_basement_index_fp() -> Option<i32> {
    let mut floor = 0;
    read_input_file(Path::new("input"))
        .map(|s| s.chars()
                  .enumerate()
                  .take_while(|&(_, x)| {
                      floor += if x == '(' { 1 } else { -1 };
                      floor != -1})
                  .count() as i32)
}

fn first_basement_index() -> Option<i32> {
    match read_input_file(Path::new("input")) {
        None => None,
        Some(input) => {
            let mut floor = 0;
            for (i, c) in input.chars().enumerate() {
                match c {
                    '(' => floor += 1,
                    ')' => floor -= 1,
                    _ => { println!("unexpected character!"); break; }
                }
                if floor == -1 {
                    return Some(i as i32)
                }
            }
            None
        },
    }
}

fn main() {
    println!("{}", which_floor_at_the_end().unwrap());
    println!("{}", which_floor_at_the_end_fp().unwrap());
    println!("{}", first_basement_index().unwrap() + 1);
    println!("{}", first_basement_index_fp().unwrap() + 1);
}
