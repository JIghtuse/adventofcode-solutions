use std::io::prelude::*;
use std::io::BufReader;
use std::fs::File;
use std::cmp;

pub fn extract_range_idx(start: &str, end: &str) -> ((usize, usize), (usize, usize)) {
    fn extract(s: &str) -> Vec<usize> {
        s.splitn(2, ",")
         .map(|x| x.parse().unwrap())
         .collect()
    };
    let start = extract(start);
    let end = extract(end);
    ((start[0], start[1]), (end[0] + 1, end[1] + 1))
}

fn how_many_lit(filename: &str) -> Option<usize> {
    let f = File::open(filename).unwrap();
    let reader = BufReader::new(f);

    let mut grid = [[false; 1000]; 1000];

    for line in reader.lines() {
        let line = line.unwrap();

        let words: Vec<&str> = line.split_whitespace().collect();
        match words[0] {
            "toggle" => {
                let (start, end) = extract_range_idx(words[1], words[3]);
                for i in (start.0..end.0) {
                    for j in (start.1..end.1) {
                        grid[i][j] = !grid[i][j];
                    }
                }
            }
            "turn" => {
                let operation: bool = words[1] == "on";
                let (start, end) = extract_range_idx(words[2], words[4]);
                for i in (start.0..end.0) {
                    for j in (start.1..end.1) {
                        grid[i][j] = operation;
                    }
                }
            }
            _ => return None,
        }
    }
    let mut nlit = 0;
    for i in (0..1000) {
        for j in (0..1000) {
            if grid[i][j] {
                nlit += 1;
            }
        }
    }
    Some(nlit)
}

fn get_brightness(filename: &str) -> Option<usize> {
    let f = File::open(filename).unwrap();
    let reader = BufReader::new(f);

    let mut grid = [[0i8; 1000]; 1000];

    for line in reader.lines() {
        let line = line.unwrap();

        let words: Vec<&str> = line.split_whitespace().collect();
        let diff: i8 = match words[0] {
            "toggle" => 2,
            "turn" => {
                match words[1] {
                    "on" => 1,
                    "off" => -1,
                    _ => panic!("unexpected input"),
                }
            }
            _ => panic!("unexpected input"),
        };
        let (start, end) = match words[0] {
            "toggle" => extract_range_idx(words[1], words[3]),
            "turn" => extract_range_idx(words[2], words[4]),
            _ => panic!("unexpected input"),
        };
        for i in (start.0..end.0) {
            for j in (start.1..end.1) {
                grid[i][j] = cmp::max(grid[i][j] + diff, 0);
            }
        }
    }
    let mut brightness = 0;
    for i in (0..1000) {
        for j in (0..1000) {
            brightness += grid[i][j] as usize;
        }
    }
    Some(brightness)
}

fn main() {
    println!("{}", how_many_lit("../input").unwrap());
    println!("{}", get_brightness("../input").unwrap());
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn extract_range_works() {
        assert_eq!(((0, 0), (1, 1)), extract_range("0,0", "1,1"));
        assert_eq!(((234, 123), (999, 999)),
                   extract_range("234,123", "999,999"));
    }
}
