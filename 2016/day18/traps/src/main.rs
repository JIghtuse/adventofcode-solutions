#![feature(slice_patterns)]

fn is_safe(current_slice: &[char]) -> bool {
    match *current_slice {
        ['^', '^', '.'] | ['.', '^', '^'] | ['^', '.', '.'] | ['.', '.', '^'] => false,
        _ => true,
    }
}

fn next_row_vec(current_row: &[char]) -> Vec<char> {
    let mut result = Vec::with_capacity(current_row.len());
    for slice in current_row.windows(3) {
        result.push(if is_safe(slice) { '.' } else { '^' });
    }
    result
}

pub fn next_row(current_row: &str) -> String {
    let mut letters = Vec::new();
    letters.push('.');
    for letter in current_row.chars() {
        letters.push(letter);
    }
    letters.push('.');
    next_row_vec(&letters).iter().cloned().collect()
}

pub fn build_map(first_row: &str, number_of_rows: usize) -> Vec<String> {
    let mut map = vec![first_row.to_string()];
    while map.len() != number_of_rows {
        let last_line = map.iter().last().unwrap().clone();
        {
            map.push(next_row(&last_line));
        }
    }
    map
}

fn number_of_safe_tiles(room_map: &[String]) -> usize {
    room_map.iter()
        .map(|s| {
            s.chars()
                .filter(|&c| c == '.')
                .count()
        })
        .sum()
}

fn main() {
    let first_row = ".^^^.^.^^^.^.......^^.^^^^.^^^^..^^^^^.^.^^^..^^.^.^^..^.^..^^...^.^^.^^^...\
                     ^^.^.^^^..^^^^.....^....";

    let map = build_map(first_row, 40);
    println!("{}", number_of_safe_tiles(&map));

    let map = build_map(first_row, 400000);
    println!("{}", number_of_safe_tiles(&map));
}

#[cfg(test)]
mod tests {
    use super::{build_map, is_safe, next_row, number_of_safe_tiles};

    #[test]
    fn next_row_works() {
        assert_eq!(".^^^^", next_row("..^^."));
        assert_eq!("^^..^", next_row(".^^^^"));
    }

    #[test]
    fn is_safe_works() {
        assert!(is_safe(&['.', '.', '.']));
        assert!(!is_safe(&['.', '.', '^']));
        assert!(is_safe(&['.', '^', '.']));
        assert!(!is_safe(&['.', '^', '^']));
        assert!(!is_safe(&['^', '.', '.']));
        assert!(is_safe(&['^', '.', '^']));
        assert!(!is_safe(&['^', '^', '.']));
        assert!(is_safe(&['^', '^', '^']));
    }

    #[test]
    fn build_map_works() {
        assert_eq!(build_map("..^^.", 3),
                   vec![String::from("..^^."), String::from(".^^^^"), String::from("^^..^")]);
    }

    #[test]
    fn number_of_safe_tiles_works() {
        assert_eq!(6, number_of_safe_tiles(&build_map("..^^.", 3)));
        assert_eq!(38, number_of_safe_tiles(&build_map(".^^.^.^^^^", 10)));
    }
}
