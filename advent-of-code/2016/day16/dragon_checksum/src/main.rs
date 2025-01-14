pub fn generate_data(initial_state: &str, size: usize) -> String {
    let mut data = String::with_capacity(size);
    data.push_str(initial_state);

    while data.len() < size {
        let mut data_tmp = data.clone();

        data_tmp.push_str("0");
        for c in data.chars().rev() {
            match c {
                '1' => data_tmp.push('0'),
                '0' => data_tmp.push('1'),
                _ => panic!("unexpected input"),
            }
        }
        data = data_tmp;
    }
    data.truncate(size);
    data
}

pub fn dragon_checksum(s: &str) -> String {
    let mut checksum = String::new();
    for pair in s.as_bytes().chunks(2) {
        if pair[0] == pair[1] {
            checksum.push('1');
        } else {
            checksum.push('0');
        }
    }
    if checksum.len() % 2 == 1 {
        checksum
    } else {
        dragon_checksum(&checksum)
    }
}

fn main() {
    let initial_state = "00111101111101000";
    let disk_length = 272;
    println!("{}", dragon_checksum(&generate_data(initial_state, disk_length)));

    let initial_state = "00111101111101000";
    let disk_length = 35651584;
    println!("{}", dragon_checksum(&generate_data(initial_state, disk_length)))
}

#[cfg(test)]
mod tests {
    use super::{generate_data, dragon_checksum};

    #[test]
    fn generate_data_produces_correct_input() {
        assert_eq!("100", generate_data("1", 3));
        assert_eq!("001", generate_data("0", 3));
        assert_eq!("100", generate_data("1", 3));
        assert_eq!("11111000000", generate_data("11111", 11));
        assert_eq!("1111000010100101011110000", generate_data("111100001010", 25));
        assert_eq!("1111100000", generate_data("11111", 10));
        assert_eq!("100011001001110", generate_data("1", 15));
        assert_eq!("11110000101001010111", generate_data("111100001010", 20));
    }

    #[test]
    fn checksum_calculated_correctly() {
        assert_eq!("100", dragon_checksum("110010110100"));
    }

    #[test]
    fn combination_works() {
        assert_eq!("01100", dragon_checksum(&generate_data("10000", 20)));
    }
}
