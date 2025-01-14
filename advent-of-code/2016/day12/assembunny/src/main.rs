use std::collections::HashMap;
use std::io::{BufRead, BufReader};
use std::fs::File;


fn get_instructions(fname: &str) -> Vec<Vec<String>> {
    let file = File::open(fname).unwrap();
    let reader = BufReader::new(file);

    reader.lines()
        .map(|s| {
            s.unwrap()
                .split_whitespace()
                .map(|s| s.to_string())
                .collect()
        })
        .collect()
}

fn main() {
    let mut registers : HashMap<String, i32> = HashMap::new();
    let instructions = get_instructions("input.txt");
    let mut pos = 0;
    while pos < instructions.len() {
        match instructions[pos][0].as_str() {
            "inc" => {
                let entry = registers.entry(instructions[pos][1].to_string()).or_insert(0);
                *entry += 1;
                pos += 1;
            }
            "dec" => {
                let entry = registers.entry(instructions[pos][1].to_string()).or_insert(0);
                *entry -= 1;
                pos += 1;
            }
            "cpy" => {
                let value = if let Ok(value) = instructions[pos][1].parse::<i32>() {
                    value
                } else {
                    registers[&instructions[pos][1].to_string()]
                };

                let entry = registers.entry(instructions[pos][2].to_string()).or_insert(0);
                *entry = value;
                pos += 1;
            }
            "jnz" => {
                let value = if let Ok(value) = instructions[pos][1].parse::<i32>() {
                    value
                } else {
                    let entry = registers.entry(instructions[pos][1].to_string()).or_insert(0);
                    *entry
                };
                if value != 0 {
                    let offset : i32 = instructions[pos][2].parse().unwrap();
                    if offset > 0 {
                        pos += offset as usize;
                    } else {
                        pos -= (-offset) as usize;
                    }
                } else {
                    pos += 1;
                }
            }
            _ => panic!("unexpected input"),
        }
    }
    println!("{:?}", registers);
    println!("{}", registers["a"]);
}
