use std::io::prelude::*;
use std::io::BufReader;
use std::fs::File;

pub fn has_three_vowels(word: &str) -> bool {
    word.chars()
        .filter(|c| "aeiou".contains(*c))
        .count() > 2
}

pub fn has_twice_in_a_row(word: &str) -> bool {
    let mut prev = ' ';
    for c in word.chars() {
        if c == prev {
            return true;
        }
        prev = c;
    }
    false
}

pub fn has_naughty(word: &str) -> bool {
    let naughty = ["ab", "cd", "pq", "xy"];
    naughty.iter().any(|naughty_word| word.contains(naughty_word))
}

pub fn is_nice(word: &str) -> bool {
    has_three_vowels(word) && has_twice_in_a_row(word) && !has_naughty(word)
}

fn how_many_nice_strings(filename: &str) -> Option<usize> {
    let f = File::open(filename).unwrap();
    let reader = BufReader::new(f);

    Some(reader.lines()
               .map(|line| line.unwrap())
               .filter(|line| is_nice(&line))
               .count())
}

fn main() {
    println!("{}", how_many_nice_strings("../input").unwrap());
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn has_three_vowels_test() {
        assert_eq!(true, has_three_vowels("aei"));
        assert_eq!(true, has_three_vowels("xazegov"));
        assert_eq!(true, has_three_vowels("aeiouaeiouaeiou"));
    }

    #[test]
    fn has_twice_in_a_row_test() {
        assert_eq!(true, has_twice_in_a_row("xx"));
        assert_eq!(true, has_twice_in_a_row("abcdde"));
        assert_eq!(true, has_twice_in_a_row("aabbccdd"));
    }

    #[test]
    fn does_not_contains_naughty_substrs_test() {
        assert_eq!(false, is_nice("ab"));
        assert_eq!(false, is_nice("cd"));
        assert_eq!(false, is_nice("pq"));
        assert_eq!(false, is_nice("xy"));
    }

    #[test]
    fn is_nice_works() {
        assert_eq!(true, is_nice("ugknbfddgicrmopn"));
        assert_eq!(true, is_nice("aaa"));
        assert_eq!(false, is_nice("jchzalrnumimnmhp"));
        assert_eq!(false, is_nice("haegwjzuvuyypxyu"));
        assert_eq!(false, is_nice("dvszwmarrgswjxmb"));
    }
}
