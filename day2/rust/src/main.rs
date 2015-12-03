use std::io::prelude::*;
use std::io::BufReader;
use std::fs::File;

fn get_dimensions(line: String) -> Vec<i32> {
    line.split('x').map(|x| x.parse::<i32>().unwrap()).collect::<Vec<i32>>()
}

fn how_many_paper() -> i32 {
    let f = File::open("input").unwrap();
    let reader = BufReader::new(f);

    let mut npaper = 0;
    for line in reader.lines() {
        let line = line.unwrap();
        let mut dimensions = get_dimensions(line);
        dimensions.sort();
        npaper += 3 * dimensions[0] * dimensions[1]
                + 2 * dimensions[1] * dimensions[2]
                + 2 * dimensions[0] * dimensions[2];
    }
    npaper
}

fn how_many_ribbon() -> i32 {
    let f = File::open("input").unwrap();
    let reader = BufReader::new(f);

    let mut nribbon = 0;
    for line in reader.lines() {
        let line = line.unwrap();
        let mut dimensions = get_dimensions(line);
        dimensions.sort();
        nribbon += 2 * dimensions[0] 
                 + 2 * dimensions[1]
                 + dimensions[0] * dimensions[1] * dimensions[2];
    }
    nribbon
}

fn how_many_paper_fp() -> i32 {
    let f = File::open("input").unwrap();
    let reader = BufReader::new(f);

    reader.lines().map(|line| {
        let mut dimensions = get_dimensions(line.unwrap());
        dimensions.sort();
        dimensions
    })
    .fold(0, |acc, x| acc + 3 * x[0] * x[1]
                          + 2 * x[1] * x[2]
                          + 2 * x[0] * x[2])
}

fn how_many_ribbon_fp() -> i32 {
    let f = File::open("input").unwrap();
    let reader = BufReader::new(f);

    reader.lines().map(|line| {
        let mut dimensions = get_dimensions(line.unwrap());
        dimensions.sort();
        dimensions
    })
    .fold(0, |acc, x| acc + 2 * x[0]
                          + 2 * x[1]
                          + x[0] * x[1] * x[2])
}


fn main() {
    println!("{}", how_many_paper());
    println!("{}", how_many_paper_fp());
    println!("{}", how_many_ribbon());
    println!("{}", how_many_ribbon_fp());
}
