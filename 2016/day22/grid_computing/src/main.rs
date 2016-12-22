use std::{thread, time};

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

struct Grid {
    move_counter: usize,
    xmax: i32,
    ymax: i32,
    marked: (i32, i32),
    nodes: Vec<Node>,
}

impl Grid {
    fn move_data(&mut self, x1: i32, y1: i32, x2: i32, y2: i32) {
        let p = self.x_y_to_pos(x1, y1);
        let q = self.x_y_to_pos(x2, y2);
        if self.nodes[p].used > self.nodes[q].available() {
            panic!("cannot move {} to {}", self.nodes[p].used, self.nodes[1].available());
        }
        if (x1, y1) == self.marked {
            self.marked = (x2, y2);
        }
        self.nodes[q].used += self.nodes[p].used;
        self.nodes[p].used = 0;
        self.move_counter += 1;

        if self.marked == (0, 0) {
            self.draw();
            panic!("DONE: {}", self.move_counter);
        }
    }

    fn x_y_to_pos(&self, x: i32, y: i32) -> usize {
        (y * self.xmax + x) as usize
    }

    fn draw(&self) {
        let large_threshold = 100;
        let small_threshold = 5;

        print!("   ");
        for x in 0..self.xmax {
            print!("{} ", x / 10);
        }
        println!("");
        print!("   ");
        for x in 0..self.xmax {
            print!("{} ", x % 10);
        }
        println!("");

        for y in 0..self.ymax {
            print!("{:02} ", y);
            for x in 0..self.xmax {
                let node = &self.nodes[self.x_y_to_pos(x, y)];
                match node.used {
                    n if n > large_threshold => print!("#"),
                    n if n < small_threshold => print!("_"),
                    _ => {
                        if (node.x, node.y) == self.marked {
                            print!("G");
                        } else {
                            print!(".");
                        }
                    },
                }
                print!(" ");
            }
            println!("");
        }
        if self.marked != (0, 0) {
            println!("");
            println!("");
        }
        println!("");
        thread::sleep(time::Duration::from_millis(40));
    }
}

fn main() {
    let mut nodes = get_nodes_data();
    let count = count_viable_pairs(&nodes);

    println!("{}", count);

    nodes.sort_by_key(|n| n.x);
    nodes.sort_by_key(|n| n.y);

    let grid_size = (nodes.len() as f32).sqrt() as i32;
    let mut grid = Grid {
        move_counter: 0,
        xmax: grid_size,
        ymax: grid_size,
        nodes: nodes,
        marked: (30, 0),
    };
    grid.draw();

    let mut x = 13;
    let mut y = 27;

    for _ in 0..11 {
        grid.move_data(x, y - 1, x, y);
        grid.draw();
        y -= 1;
    }
    for _ in 0..9 {
        grid.move_data(x - 1, y, x, y);
        grid.draw();
        x -= 1;
    }
    for _ in 0..16 {
        grid.move_data(x, y - 1, x, y);
        grid.draw();
        y -= 1;
    }

    for _ in 0..25 {
        grid.move_data(x + 1, y, x, y);
        grid.draw();
        x += 1;
    }

    for _ in 0..30 {
        grid.move_data(x + 1, y, x, y);
        grid.draw();
        x += 1;

        grid.move_data(x, y + 1, x, y);
        grid.draw();
        y += 1;

        grid.move_data(x - 1, y, x, y);
        grid.draw();
        x -= 1;

        grid.move_data(x - 1, y, x, y);
        grid.draw();
        x -= 1;

        grid.move_data(x, y - 1, x, y);
        grid.draw();
        y -= 1;
    }
}
