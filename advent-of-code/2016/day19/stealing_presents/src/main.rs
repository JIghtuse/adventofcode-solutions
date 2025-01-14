fn steal_from_neighbor_winner(n: i32) -> i32 {
    let mut indexes = (1..n + 1).collect::<Vec<_>>();
    while indexes.len() > 1 {
        let retain_first = indexes.len() % 2 == 0;
        indexes = indexes.iter().enumerate().filter(|&(i, _)| {
            if retain_first {
                i % 2 == 0
            } else {
                i % 2 == 0 && i != 0
            }
        }).map(|(_, &item)| item).collect();
    }
    indexes[0]
}

fn steal_from_opposite_winner(n: i32) -> i32 {
    let mut indexes = (1..n + 1).collect::<Vec<_>>();
    let mut current_idx = 0;
    while indexes.len() > 1 {
        let len = indexes.len();
        let remove_idx = (current_idx + len / 2) % len;
        indexes.remove(remove_idx);
        if current_idx <= remove_idx {
            current_idx += 1;
        }
        current_idx %= indexes.len();
    }
    indexes[0]
}

fn main() {
    let winner = steal_from_neighbor_winner(3018458);
    println!("{}", winner);

    let winner = steal_from_opposite_winner(3018458);
    println!("{}", winner);
}
