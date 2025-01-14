use std::io::prelude::*;
use std::io::BufReader;
use std::collections::HashMap;
use std::fs::File;
use std::io;

#[derive(Clone, Debug, Hash, Eq, PartialEq)]
struct Position {
    x: i32,
    y: i32,
}

fn get_input(filename: &str) -> io::Result<String> {
    let f = try!(File::open(filename));
    let mut reader = BufReader::new(f);

    let mut line = String::new();
    try!(reader.read_line(&mut line));
    Ok(line)
}


fn how_many_houses_with_n_santas(nsantas: usize) -> io::Result<usize> {
    let line = try!(get_input("input"));

    let mut current_pos = vec![Position { x: 0, y: 0}; nsantas];
    let mut positions: HashMap<Position, i32> = HashMap::new();
    positions.insert(current_pos[0].clone(), 1);

    for (i, c) in line.chars().enumerate() {
        let idx = i % nsantas;
        match c {
            '^'     => current_pos[idx].y += 1,
            'v'|'V' => current_pos[idx].y -= 1,
            '>'     => current_pos[idx].x += 1,
            '<'     => current_pos[idx].x -= 1,
            _       => break,
        }
        let counter = positions.entry(current_pos[idx].clone()).or_insert(0);
        *counter += 1;
    }
    Ok(positions.len())
}

fn main() {
    println!("{}", how_many_houses_with_n_santas(1).unwrap());
    println!("{}", how_many_houses_with_n_santas(2).unwrap());
    println!("{}", how_many_houses_with_n_santas(3).unwrap());
}
