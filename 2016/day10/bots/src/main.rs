use std::fs::File;
use std::io::{BufRead, BufReader};
use std::collections::HashMap;
use std::path::Path;
use std::str;

#[derive(Debug)]
enum Command {
    TakeValue { bot: usize, value: usize },
    GiveValue {
        bot: usize,
        other_bot_low: Option<usize>,
        other_bot_high: Option<usize>,
        output_low: Option<usize>,
        output_high: Option<usize>,
    },
}

#[derive(Debug)]
struct InvalidCommand;

impl str::FromStr for Command {
    type Err = InvalidCommand;
    fn from_str(s: &str) -> Result<Self, Self::Err> {
        let fields: Vec<_> = s.split_whitespace().collect();
        match fields[0] {
            "value" => {
                let value = fields[1].parse::<usize>().unwrap();
                let bot = fields[5].parse::<usize>().unwrap();
                Ok(Command::TakeValue {
                    bot: bot,
                    value: value,
                })
            }
            "bot" => {
                let bot = fields[1].parse::<usize>().unwrap();
                let low_value = fields[6].parse::<usize>().unwrap();
                let high_value = fields[11].parse::<usize>().unwrap();
                let mut other_bot_low: Option<usize> = None;
                let mut other_bot_high: Option<usize> = None;
                let mut output_low: Option<usize> = None;
                let mut output_high: Option<usize> = None;

                match fields[5] {
                    "bot" => {
                        other_bot_low = Some(low_value);
                    }
                    "output" => {
                        output_low = Some(low_value);
                    }
                    _ => panic!("unexpected input"),
                };

                match fields[10] {
                    "bot" => {
                        other_bot_high = Some(high_value);
                    }
                    "output" => {
                        output_high = Some(high_value);
                    }
                    _ => panic!("unexpected input"),
                };
                Ok(Command::GiveValue {
                    bot: bot,
                    other_bot_low: other_bot_low,
                    other_bot_high: other_bot_high,
                    output_low: output_low,
                    output_high: output_high,
                })
            }
            _ => Err(InvalidCommand),
        }
    }
}

fn has_unknown_values(m: &HashMap<usize, Bot>) -> bool {
    m.values().any(|bot| bot.values.len() != 2)
}

#[derive(Clone, Debug)]
enum Output {
    Bot { id: usize },
    Bin { id: usize },
}

#[derive(Clone, Debug)]
struct Bot {
    id: usize,
    values: Vec<usize>,
    low: Option<Output>,
    high: Option<Output>,
}


fn get_bots_data<P: AsRef<Path>>(input_path: P) -> HashMap<usize, Bot> {
    let file = File::open(input_path).expect("Cannot open input file");
    let reader = BufReader::new(file);

    let mut bots = HashMap::new();

    for line in reader.lines() {
        let line = line.unwrap();
        let command: Command = line.parse().unwrap();

        match command {
            Command::TakeValue { bot, value } => {
                let entry = bots.entry(bot).or_insert(Bot {
                    id: bot,
                    values: vec![],
                    low: None,
                    high: None,
                });
                entry.values.push(value);
            }
            Command::GiveValue { bot, other_bot_low, other_bot_high, output_low, output_high } => {
                {
                    let entry = bots.entry(bot).or_insert(Bot {
                        id: bot,
                        values: vec![],
                        low: None,
                        high: None,
                    });
                    if let Some(other_bot_low) = other_bot_low {
                        entry.low = Some(Output::Bot { id: other_bot_low });
                    }
                    if let Some(other_bot_high) = other_bot_high {
                        entry.high = Some(Output::Bot { id: other_bot_high });
                    }
                    if let Some(output_low) = output_low {
                        entry.low = Some(Output::Bin { id: output_low });
                    }
                    if let Some(output_high) = output_high {
                        entry.high = Some(Output::Bin { id: output_high });
                    }
                }

                let current_len = bots[&bot].values.len();

                if let Some(other_bot) = other_bot_low {
                    let low_value = if current_len == 2 {
                        *bots[&bot].values.iter().min().unwrap()
                    } else {
                        usize::max_value()
                    };

                    let entry = {bots.entry(other_bot).or_insert(Bot {
                        id: other_bot,
                        values: vec![],
                        low: None,
                        high: None,
                    }) };
                    if current_len == 2 {
                        entry.values.push(low_value);
                    }
                }
                if let Some(other_bot) = other_bot_high {
                    let high_value = if current_len == 2 {
                        *bots[&bot].values.iter().max().unwrap()
                    } else {
                        usize::max_value()
                    };

                    let entry = bots.entry(other_bot).or_insert(Bot {
                        id: other_bot,
                        values: vec![],
                        low: None,
                        high: None,
                    });
                    if current_len == 2 {
                        entry.values.push(high_value);
                    }
                }
            }
        }
    }
    bots
}


fn main() {
    let mut bots = get_bots_data("input.txt");

    loop {
        if !has_unknown_values(&bots) {
            break;
        }
        let mut modifiable_bots = bots.clone();

        for (_, bot) in &mut bots {
            if bot.values.len() == 2 {
                if let Some(Output::Bot { id: low_bot }) = bot.low {
                    let low = bot.values.iter().min().unwrap();

                    if let Some(low_bot) = modifiable_bots.get_mut(&low_bot) {
                        if !low_bot.values.contains(low) {
                            low_bot.values.push(*low);
                        }
                    }
                }
                if let Some(Output::Bot { id: high_bot }) = bot.high {
                    let high = bot.values.iter().max().unwrap();
                    if let Some(high_bot) = modifiable_bots.get_mut(&high_bot) {
                        if !high_bot.values.contains(high) {
                            high_bot.values.push(*high);
                        }
                    }
                }
            }
        }
        bots = modifiable_bots;
    }


    for bot in bots.values() {
        if bot.values.contains(&17) && bot.values.contains(&61) {
            println!("{:#?}", bot.id);
            break;
        }
    }

    let mut product = 1;
    for bot in bots.values() {
        if let Some(Output::Bin { id: bin_low }) = bot.low {
            if [0, 1, 2].contains(&bin_low) {
                product *= *bot.values.iter().min().unwrap();
            }
        }
        if let Some(Output::Bin { id: bin_high }) = bot.high {
            if [0, 1, 2].contains(&bin_high) {
                product *= *bot.values.iter().max().unwrap();
            }
        }
    }
    println!("{}", product);
}
