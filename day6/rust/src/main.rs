use std::io::prelude::*;
use std::io::BufReader;
use std::fs::File;

pub fn extract_range(start: &str, end: &str) -> ((i32, i32), (i32, i32)) {
    fn extract(s: &str) -> Vec<i32> {
        s.splitn(2, ",")
         .map(|x| x.parse().unwrap())
         .collect()
    };
    let start = extract(start);
    let end = extract(end);
    ((start[0], start[1]), (end[0], end[1]))
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
                let (start, end) = extract_range(words[1], words[3]);
                for i in (start.0 .. end.0) {
                    for j in (start.1 .. end.1) {
                        grid[i as usize][j as usize] = !grid[i as usize][j as usize];
                    }
                }
            }
            "turn" => {
                let operation: bool = words[1] == "on";
                let (start, end) = extract_range(words[2], words[4]);
                for i in (start.0 .. end.0) {
                    for j in (start.1 .. end.1) {
                        grid[i as usize][j as usize] = operation;
                    }
                }
            }
            _ => return None,
        }
    }
    let mut nlit = 0;
    for i in (0 .. 999) {
        for j in (0 .. 999) {
            if grid[i][j] {
                nlit += 1;
            }
        }
    }
    Some(nlit)
}

fn main() {
    println!("{}", how_many_lit("../input").unwrap());
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
