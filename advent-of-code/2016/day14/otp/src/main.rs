extern crate crypto;

use crypto::md5::Md5;
use crypto::digest::Digest;

const THREES : &'static [&'static str] = &[
    "000",
    "111",
    "222",
    "333",
    "444",
    "555",
    "666",
    "777",
    "888",
    "999",
    "aaa",
    "bbb",
    "ccc",
    "ddd",
    "eee",
    "fff",
];

const FIVES : &'static [&'static str] = &[
    "00000",
    "11111",
    "22222",
    "33333",
    "44444",
    "55555",
    "66666",
    "77777",
    "88888",
    "99999",
    "aaaaa",
    "bbbbb",
    "ccccc",
    "ddddd",
    "eeeee",
    "fffff",
];

const NUMBER_OF_KEYS : usize = 64;
const INITIAL_TTL : i32 = 1000;

#[derive(Clone, Debug)]
struct Key {
    idx: usize,
    needle: &'static str,
    ttl: i32,
    hash: String,
}

fn get_match_indices(candidates: &[Key], hash_str: &str) -> Vec<usize> {
    candidates.iter().enumerate().filter(|&(_, c)| {
        c.ttl >= 0 && hash_str.contains(c.needle)
    }).map(|(i, _)| i).collect()
}

fn get_hash(s: &str, n: usize, with_stretching: bool) -> String
{
    let mut sh = Md5::new();

    sh.input_str(s);
    sh.input_str(&n.to_string());

    let mut res = sh.result_str();

    if with_stretching {
        for _ in 0..2016 {
            sh.reset();
            sh.input_str(&res);
            res = sh.result_str();
        }
    }

    res
}

fn get_keys(s: &str, with_stretching: bool) -> Vec<Key> {
    let mut termination_index = usize::max_value();

    let mut five_of_a_kind_candidates = vec![];

    let mut result : Vec<Key> = vec![];

    let mut index = 0;
    loop {
        if index == termination_index {
            result.sort_by_key(|c| c.idx);
            return result;
        }

        let hash_str = get_hash(s, index, with_stretching);


        // Storing valid keys, setting termination index when we should have enough

        let indices = get_match_indices(&five_of_a_kind_candidates, &hash_str);
        for match_idx in indices.iter().rev() {
            let candidate = five_of_a_kind_candidates.swap_remove(*match_idx);
            result.push(candidate);
            if result.len() == NUMBER_OF_KEYS {
                termination_index = index + 1000;
            }
        }


        // Updating ttl and removing `obsolete` candidates

        for c in &mut five_of_a_kind_candidates {
            c.ttl -= 1;
        }
        five_of_a_kind_candidates.retain(|c| c.ttl > 0);


        // Detecting candidates

        let mut repeat_idx : Option<usize> = None;
        let mut pos = usize::max_value();
        for (i, three) in THREES.iter().enumerate() {
            if let Some(look_pos) = hash_str.find(three) {
                if look_pos < pos {
                    pos = look_pos;
                    repeat_idx = Some(i);
                }
            }
        }
        if let Some(repeat_idx) = repeat_idx {
            five_of_a_kind_candidates.push(Key {
                idx: index,
                needle: FIVES[repeat_idx],
                ttl: INITIAL_TTL,
                hash: hash_str.clone(),
            });
        }
        index += 1;
    }
}

fn main() {
    let salt = "ngcjuoqr";

    let keys = get_keys(salt, false);
    println!("{:#?}", keys[63]);

    let keys = get_keys(salt, true);
    println!("{:#?}", keys[63]);
}
