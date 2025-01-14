extern crate crypto;

use crypto::md5::Md5;
use crypto::digest::Digest;

fn lowest_number(key: &str, start_sequence: &str) -> Option<u32> {
    let key = key.to_string();

    for number in 1.. {
        let input = key.clone() + &number.to_string();

        let mut sh = Md5::new();
        sh.input_str(&input);
        if sh.result_str().starts_with(start_sequence) {
            return Some(number);
        }
    }
    None
}

fn main() {
    println!("result: {}", lowest_number("yzbqklnj", "00000").unwrap());
    println!("result: {}", lowest_number("yzbqklnj", "000000").unwrap());
}
