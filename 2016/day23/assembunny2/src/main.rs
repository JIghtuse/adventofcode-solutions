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
    let mut registers : HashMap<&'static str, i32> = HashMap::new();
    {
        registers.insert("a", 12);
        registers.insert("b", 0);
        registers.insert("c", 0);
        registers.insert("d", 0);
    }

    let mut instructions = get_instructions("input.txt");
    let mut pos = 0;
    while pos < instructions.len() {
        match instructions[pos][0].as_str() {
            "inc" => {
                let entry = registers.get_mut(instructions[pos][1].as_str()).unwrap();
                *entry += 1;
                pos += 1;
            }
            "dec" => {
                let entry = registers.get_mut(instructions[pos][1].as_str()).unwrap();
                *entry -= 1;
                pos += 1;
            }
            "cpy" => {
                let value = if let Ok(value) = instructions[pos][1].parse::<i32>() {
                    value
                } else {
                    let entry = registers[instructions[pos][1].as_str()];
                    entry
                };

                let entry = registers.get_mut(instructions[pos][2].as_str()).unwrap();
                *entry = value;
                pos += 1;
            }
            "jnz" => {
                let value = if let Ok(value) = instructions[pos][1].parse::<i32>() {
                    value
                } else {
                    let entry = registers[instructions[pos][1].as_str()];
                    entry
                };
                if value != 0 {
                    // println!("{:?}", instructions[pos]);
                    let offset : i32 = if let Ok(value) = instructions[pos][2].parse() {
                        value
                    } else {
                        let entry = registers[instructions[pos][2].as_str()];
                        entry
                    };
                    if offset > 0 {
                        pos += offset as usize;
                    } else {
                        pos -= (-offset) as usize;
                    }
                } else {
                    pos += 1;
                }
            }
            "tgl" => {
                let value_field = instructions[pos][1].clone();

                let offset = if let Ok(value) = value_field.parse::<i32>() {
                    value
                } else {
                    let entry = registers[value_field.as_str()];
                    entry
                };

                if offset > 0 {
                    let offset = offset as usize + pos;
                    if offset >= instructions.len() {
                        pos += 1;
                        continue;
                    }
                    let command_on_offset = instructions[offset][0].clone();
                    match command_on_offset.as_str() {
                        "dec" => instructions[offset][0] = "inc".to_string(),
                        "inc" => instructions[offset][0] = "dec".to_string(),
                        "jnz" => instructions[offset][0] = "cpy".to_string(),
                        _ => {
                            println!("TGL: {:?} {:?}", instructions[offset], registers);
                            panic!("waaat");
                        },
                    }
                    pos += 1;
                } else {
                    println!("{:?} {:?}", instructions[pos], registers);
                    panic!("OFFSET < 0");
                }
            }
            _ => panic!("unexpected input"),
        }
    }
    println!("{:?}", registers);
    println!("{}", registers["a"]);
}
