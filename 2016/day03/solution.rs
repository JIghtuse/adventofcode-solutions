use std::io::prelude::*;
use std::io::BufReader;
use std::fs::File;
use std::io;

fn get_input(filename: &str) -> io::Result<Vec<Vec<u32>>> {
    let f = try!(File::open(filename));
    let mut reader = BufReader::new(f);

    let mut line = String::new();
    try!(reader.read_line(&mut line));

    let mut data = vec![];
    for line in reader.lines() {
        data.push(line.unwrap()
            .split_whitespace()
            .map(|s| s.parse().unwrap())
            .collect())
    }
    Ok(data)
}

fn main() {
    let mut data = get_input("input.txt").expect("Cannot read data");
    let number_of_valid_triangles = data.iter_mut()
        .map(|v| {
            v.sort();
            v
        })
        .filter(|v| v[0] + v[1] > v[2])
        .count();
    println!("{:?}", number_of_valid_triangles);
}
