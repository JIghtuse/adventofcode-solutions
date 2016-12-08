use std::fmt;
use std::str;
use std::fs::File;
use std::io::{BufRead, BufReader};

#[derive(Debug)]
struct Display {
    pixels: Vec<Vec<char>>,
}

enum Command {
    RotateColumn { x: usize, rotates: usize },
    RotateRow { y: usize, rotates: usize },
    Rect { width: usize, height: usize },
}

#[derive(Debug)]
struct InvalidCommand;

impl str::FromStr for Command {
    type Err = InvalidCommand;
    fn from_str(s: &str) -> Result<Self, Self::Err> {
        let fields: Vec<_> = s.split_whitespace().collect();
        match fields[0] {
            "rect" => {
                let dimensions = fields[1]
                    .split('x')
                    .map(|s| s.parse::<usize>().unwrap())
                    .collect::<Vec<_>>();
                Ok(Command::Rect {
                    width: dimensions[0],
                    height: dimensions[1],
                })
            }
            "rotate" => {
                match fields[1] {
                    "row" => {
                        let rownum =
                            fields[2].split('=').nth(1).unwrap().parse::<usize>().unwrap();
                        let rotation = fields[4].parse::<usize>().unwrap();
                        Ok(Command::RotateRow {
                            y: rownum,
                            rotates: rotation,
                        })
                    }
                    "column" => {
                        let colnum =
                            fields[2].split('=').nth(1).unwrap().parse::<usize>().unwrap();
                        let rotation = fields[4].parse::<usize>().unwrap();
                        Ok(Command::RotateColumn {
                            x: colnum,
                            rotates: rotation,
                        })
                    }
                    _ => Err(InvalidCommand),
                }
            }
            _ => Err(InvalidCommand),
        }
    }
}

impl fmt::Display for Display {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        for row in &self.pixels {
            let mut line = String::new();
            for c in row.iter() {
                line.push(*c);
            }
            try!(writeln!(f, "{}", line));
        }
        Ok(())
    }
}

impl Display {
    pub fn new(width: usize, height: usize) -> Self {
        let mut pixels = vec![];
        for _ in 0..height {
            let mut row = Vec::new();
            row.resize(width, ' ');
            pixels.push(row);
        }
        Display { pixels: pixels }
    }

    pub fn rect(&mut self, width: usize, height: usize) {
        for i in 0..height {
            for j in 0..width {
                self.pixels[i][j] = '#';
            }
        }
    }

    pub fn rotate_column(&mut self, x: usize, rotates: usize) {
        let len = self.pixels.len();
        for _ in 0..rotates {
            let last_pixel = self.pixels[len - 1][x];
            for i in (1..len).rev() {
                self.pixels[i][x] = self.pixels[i - 1][x];
            }
            self.pixels[0][x] = last_pixel;
        }
    }

    pub fn rotate_row(&mut self, y: usize, rotates: usize) {
        let len = self.pixels[0].len();
        for _ in 0..rotates {
            let last_pixel = self.pixels[y][len - 1];
            for i in (1..len).rev() {
                self.pixels[y][i] = self.pixels[y][i - 1];
            }
            self.pixels[y][0] = last_pixel;
        }
    }

    pub fn count_lit(&self) -> usize {
        self.pixels.iter().fold(0,
                                |acc, row| acc + row.iter().filter(|&&c| c == '#').count())
    }
}

fn main() {
    let file = File::open("input.txt").expect("Cannot open input file");
    let reader = BufReader::new(file);

    let mut display = Display::new(50, 6);
    for line in reader.lines() {
        println!("{}", display);
        let line = line.unwrap();
        let command: Command = line.parse().unwrap();
        match command {
            Command::Rect { width, height } => display.rect(width, height),
            Command::RotateColumn { x, rotates } => display.rotate_column(x, rotates),
            Command::RotateRow { y, rotates } => display.rotate_row(y, rotates),
        }
    }
    println!("{}", display);
    println!("{}", display.count_lit());
}
