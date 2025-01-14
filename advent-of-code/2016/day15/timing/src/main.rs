use std::fs::File;
use std::io::{BufRead, BufReader};
use std::str::FromStr;
use std::error::Error;

#[derive(Debug)]
struct Disc {
    number_of_positions: i32,
    current_position: i32,
}

impl Disc {
    fn will_have_zero_position_at_offset(&self, offset: i32) -> bool {
        (self.current_position + offset) % self.number_of_positions == 0
    }
    fn tick(&mut self) {
        self.current_position += 1;
        self.current_position %= self.number_of_positions;
    }
}

impl FromStr for Disc {
    type Err = Box<Error>;
    fn from_str(s: &str) -> Result<Self, Self::Err> {
        let mut fields = s.split_whitespace();

        let number_of_positions: i32 = fields.nth(3)
            .ok_or("no number of positions")?
            .parse()?;

        let s = fields.nth(7).ok_or("no start position")?;
        let start_position: i32 = s[..s.len() - 1].parse()?;

        Ok(Disc {
            number_of_positions: number_of_positions,
            current_position: start_position,
        })
    }
}

fn get_discs_data(input_path: &str) -> Vec<Disc> {
    let file = File::open(input_path).unwrap();
    let reader = BufReader::new(file);
    reader.lines().map(|s| s.unwrap().parse().unwrap()).collect()
}

fn correct_timing(discs: &[Disc]) -> bool {
    discs.iter().enumerate().all(|(i, disc)| {
        disc.will_have_zero_position_at_offset(1 + i as i32)
    })
}

fn get_first_time(mut discs: &mut [Disc]) -> i32 {
    for i in 0.. {
        for disc in discs.iter_mut() {
            disc.tick();
        }
        if correct_timing(discs) {
            return i + 1;
        }
    }
    0
}

fn main() {
    let mut discs = get_discs_data("input.txt");
    let time = get_first_time(&mut discs);
    println!("{}", time);

    let mut discs = get_discs_data("input.txt");
    discs.push(Disc {
        number_of_positions: 11,
        current_position: 0,
    });
    let time = get_first_time(&mut discs);
    println!("{}", time);
}
