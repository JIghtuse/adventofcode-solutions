#[derive(Debug)]
struct Node {
    x: i32,
    y: i32,
    size: i32,
    used: i32,
}

impl Node {
    fn available(&self) -> i32 {
        self.size - self.used
    }
}

fn size_field_to_number(s: &str) -> i32 {
    s[..s.len() - 1].parse().unwrap()
}

fn coordinate_field_to_number(s: &str) -> i32 {
    s[1..].parse().unwrap()
}

fn get_nodes_data() -> Vec<Node> {
    let data = include_str!("../../input.txt");
    let mut nodes = vec![];
    for line in data.split('\n').skip(2) {
        if line.is_empty() {
            break;
        }
        let fields: Vec<_> = line.split_whitespace().collect();
        let name_fields : Vec<_> = fields[0].split('-').collect();
        let x = coordinate_field_to_number(name_fields[1]);
        let y = coordinate_field_to_number(name_fields[2]);

        let size : i32 = size_field_to_number(fields[1]);
        let used : i32 = size_field_to_number(fields[2]);
        nodes.push(Node { x: x, y: y, size: size, used: used });
    }
    nodes.sort_by_key(|n| n.size - n.used);
    nodes
}

fn count_viable_pairs(nodes: &[Node]) -> usize {
    let mut count : usize = 0;
    for node in nodes {
        if node.used == 0 {
            continue;
        }
        let mut pos = match nodes.binary_search_by_key(&node.used, |n| n.available()) {
            Ok(n) | Err(n) => n
        };
        while pos > 0 && nodes[pos - 1].available() >= node.used {
            pos -= 1;
        }
        if node.available() >= node.used {
            pos -= 1;
        }
        count += nodes.len() - pos;
    }
    count
}

fn main() {
    let nodes = get_nodes_data();
    let count = count_viable_pairs(&nodes);

    println!("{}", count);
}
