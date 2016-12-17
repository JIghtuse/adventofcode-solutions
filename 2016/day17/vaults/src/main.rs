extern crate crypto;

use std::fmt;
use crypto::md5::Md5;
use crypto::digest::Digest;

const MAZE_WIDTH : i32 = 4;
const MAZE_HEIGHT : i32 = 4;

#[derive(Clone, Copy, Debug, PartialEq)]
enum Direction {
    Up,
    Down,
    Left,
    Right,
}

impl fmt::Display for Direction {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        let letter = match *self {
            Direction::Up => 'U',
            Direction::Down => 'D',
            Direction::Left => 'L',
            Direction::Right => 'R',
        };
        write!(f, "{}", letter)
    }
}

fn md5(s: &str) -> String {
    let mut md5 = Md5::new();
    md5.input_str(s);
    md5.result_str()
}

fn path_open(c: char) -> bool {
    ['b', 'c', 'd', 'e', 'f'].contains(&c)
}

fn get_available_directions(current_pass: &str, x: i32, y: i32) -> Vec<Direction> {
    let hash = md5(current_pass);
    let mut directions = vec![];

    let mut chars = hash.chars();
    let up = chars.next().unwrap();
    let down = chars.next().unwrap();
    let left = chars.next().unwrap();
    let right = chars.next().unwrap();

    if path_open(up) && y != 0 {
        directions.push(Direction::Up);
    }
    if path_open(down) && y != MAZE_HEIGHT - 1 {
        directions.push(Direction::Down);
    }
    if path_open(left) && x != 0 {
        directions.push(Direction::Left);
    }
    if path_open(right) && x != MAZE_WIDTH - 1 {
        directions.push(Direction::Right);
    }

    directions
}

fn new_position(x: i32, y: i32, direction: Direction) -> (i32, i32) {
    match direction {
        Direction::Up => (x, y - 1),
        Direction::Down => (x, y + 1),
        Direction::Left => (x - 1, y),
        Direction::Right => (x + 1, y),
    }
}

fn get_paths_to_vault(passcode: &str, x: i32, y: i32) -> Vec<String> {
    if x == MAZE_WIDTH - 1 && y == MAZE_HEIGHT - 1 {
        return vec![passcode.to_string()];
    }
    let mut paths_new = Vec::new();
    let directions = get_available_directions(passcode, x, y);
    if directions.is_empty() {
        return vec![];
    }

    for direction in directions {
        let new_passcode = format!("{}{}", passcode, direction);
        let (x, y) = new_position(x, y, direction);

        paths_new.extend(get_paths_to_vault(&new_passcode, x, y));
    }
    paths_new
}

fn get_shortest_path_to_vault(passcode: &str) -> String {
    let mut paths = get_paths_to_vault(passcode, 0, 0);
    paths.sort_by_key(|path| path.len());
    paths.first().unwrap().clone()
}

fn get_longest_path_len_to_vault(passcode: &str) -> usize {
    let mut paths = get_paths_to_vault(passcode, 0, 0);
    paths.sort_by_key(|path| path.len());
    paths.last().unwrap().len() - passcode.len()
}

fn main() {
    let passcode = "vwbaicqe";

    let shortest_path = get_shortest_path_to_vault(passcode);
    println!("{}", &shortest_path[passcode.len()..]);

    let longest_path_len = get_longest_path_len_to_vault(passcode);
    println!("{}", longest_path_len);
}


#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn available_directions_ok() {
        let directions = get_available_directions("hijkl", 0, 0);
        assert_eq!(directions.len(), 1);
        assert_eq!(Direction::Down, directions[0]);

        let directions = get_available_directions("hijklD", 0, 1);
        assert_eq!(directions.len(), 2);
        assert_eq!(Direction::Up, directions[0]);
        assert_eq!(Direction::Right, directions[1]);

        let directions = get_available_directions("hijklDR", 1, 1);
        assert_eq!(directions.len(), 0);

        let directions = get_available_directions("hijklDU", 0, 0);
        assert_eq!(directions.len(), 1);
        assert_eq!(Direction::Right, directions[0]);

        let directions = get_available_directions("hijklDUR", 1, 0);
        assert_eq!(directions.len(), 0);

        let directions = get_available_directions("ihgpwlah", 0, 0);
        assert_eq!(directions.len(), 2);
        assert_eq!(Direction::Down, directions[0]);
        assert_eq!(Direction::Right, directions[1]);
    }

    #[test]
    fn shortest_path_ok() {
        let passcode = "ihgpwlah";
        assert_eq!("ihgpwlahDDRRRD", get_shortest_path_to_vault(passcode));

        let passcode = "kglvqrro";
        assert_eq!("kglvqrroDDUDRLRRUDRD", get_shortest_path_to_vault(passcode));
        let passcode = "ulqzkmiv";
        assert_eq!("ulqzkmivDRURDRUDDLLDLUURRDULRLDUUDDDRR",
                   get_shortest_path_to_vault(passcode));
    }

    #[test]
    fn longest_path_len_ok() {
        let passcode = "ihgpwlah";
        assert_eq!(370, get_longest_path_len_to_vault(passcode));

        let passcode = "kglvqrro";
        assert_eq!(492, get_longest_path_len_to_vault(passcode));

        let passcode = "ulqzkmiv";
        assert_eq!(830, get_longest_path_len_to_vault(passcode));
    }
}
