use std::io::{BufRead, BufReader};
use std::fs::File;
use std::path::Path;

#[derive(Default)]
struct AddressIpv7 {
    supernets: Vec<Vec<char>>,
    hypernets: Vec<Vec<char>>,
}

fn get_addresses<P: AsRef<Path>>(fname: P) -> Vec<AddressIpv7> {
    let file = File::open(fname).unwrap();
    let reader = BufReader::new(file);

    let mut addresses = vec![];

    for line in reader.lines() {
        let mut address = AddressIpv7::default();

        let line = line.unwrap();
        for (i, field) in line.split(|c| c == '[' || c == ']').enumerate() {
            if i % 2 == 0 {
                address.supernets.push(field.chars().collect());
            } else {
                address.hypernets.push(field.chars().collect());
            }
        }
        addresses.push(address);
    }
    addresses
}

fn has_abba(s: &[char]) -> bool {
    s.windows(4).any(|s| s[0] != s[1] && s[0] == s[3] && s[1] == s[2])
}

fn has_tls(a: &AddressIpv7) -> bool {
    a.supernets.iter().any(|s| has_abba(s)) && !a.hypernets.iter().any(|h| has_abba(h))
}

fn make_bab(aba: &[char]) -> Vec<char> {
    let mut bab = vec![];
    bab.push(aba[1]);
    bab.push(aba[0]);
    bab.push(aba[1]);
    bab
}

fn has_ssl(a: &AddressIpv7) -> bool {
    for hypernet in &a.hypernets {
        for window in hypernet.windows(3) {
            // [bab], [xyx]
            if window[0] == window[2] && window[0] != window[1] {
                let bab = make_bab(window);

                // need to find [aba], [yxy] in supernets
                for supernet in &a.supernets {
                    if supernet.windows(3).any(|w| bab == w) {
                        return true;
                    }
                }
            }
        }
    }
    false
}

fn main() {
    let addresses = get_addresses("input.txt");

    let tls_addresses = addresses.iter().filter(|a| has_tls(a));
    println!("{}", tls_addresses.count());

    let ssl_addresses = addresses.iter().filter(|a| has_ssl(a));
    println!("{}", ssl_addresses.count());
}

#[test]
fn abba_works() {
    assert!(has_abba("abba"));
    assert!(has_abba("gabba"));
    assert!(has_abba("abbak"));
    assert!(has_abba("babban"));
    assert!(!has_abba("abashev"));
    assert!(!has_abba("aaba"));
}

#[test]
fn make_bab_works() {
    assert_eq!("bab", make_bab("aba"));
    assert_eq!("xyx", make_bab("yxy"));
}
