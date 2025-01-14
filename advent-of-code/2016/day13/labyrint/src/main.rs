#![feature(iter_max_by)]

use std::cmp::Ordering;
use std::collections::{HashMap, HashSet};

fn is_wall(pos: (i32, i32)) -> bool {
    let (x, y) = pos;
    let favorite_number = 1362;
    let bits = x * x + 3 * x + 2 * x * y + y + y * y + favorite_number;
    bits.count_ones() % 2 == 1
}

fn draw_labyrint_with_path(max_x: i32, max_y: i32, came_from: &HashMap<(i32, i32), (i32, i32)>) {
    print!(" ");
    for x in 0..max_x + 1 {
        print!("{}", x % 10);
    }
    println!("");

    for y in 0..max_y + 1 {
        print!("{}", y % 10);
        for x in 0..max_x + 1 {
            if is_wall((x, y)) {
                print!("#");
            } else if came_from.contains_key(&(x, y)) {
                print!("o");
            } else {
                print!(" ");
            }
        }
        println!("");
    }
    println!("");
}

fn draw_labyrint_with_set(max_x: i32, max_y: i32, positions: &HashSet<(i32, i32)>) {
    print!(" ");
    for x in 0..max_x + 1 {
        print!("{}", x % 10);
    }
    println!("");

    for y in 0..max_y + 1 {
        print!("{}", y % 10);
        for x in 0..max_x + 1 {
            if is_wall((x, y)) {
                print!("#");
            } else if positions.contains(&(x, y)) {
                print!("o");
            } else {
                print!(" ");
            }
        }
        println!("");
    }
    println!("");
}

fn heuristic_cost_estimate(a: (i32, i32), b: (i32, i32)) -> f32 {
    if is_wall(b) {
        std::f32::INFINITY
    } else {
        let (x1, y1) = a;
        let (x2, y2) = b;
        (((x2 - x1).pow(2) + (y2 - y1).pow(2)) as f32).sqrt()
    }
}

fn reconstruct_path(came_from: &HashMap<(i32, i32), (i32, i32)>, current: (i32, i32)) -> Vec<(i32, i32)> {
    let mut current = current;
    let mut total_path = vec![current];
    while came_from.contains_key(&current) {
        current = came_from[&current];
        total_path.push(current);
    }
    total_path
}

fn astar(start: (i32, i32), goal: (i32, i32)) -> Option<Vec<(i32, i32)>> {
    let mut closed_set = HashSet::new();

    let mut open_set = HashSet::new();
    open_set.insert(start);

    let mut came_from = HashMap::new();
    let mut gscore : HashMap<(i32, i32), f32> = HashMap::new();
    {
        gscore.insert(start, 0.0);
    }

    let mut fscore : HashMap<(i32, i32), f32> = HashMap::new();
    {
        fscore.insert(start, heuristic_cost_estimate(start, goal));
    }

    while !open_set.is_empty() {
        let current = *open_set.iter().max_by(|a, b| {
            fscore[*a].partial_cmp(&fscore[*b]).unwrap_or(Ordering::Equal)
        }).unwrap();
        if current == goal {
            return Some(reconstruct_path(&came_from, current));
        }

        open_set.remove(&current);
        closed_set.insert(current);

        let (x, y) = current;
        for neighbor in &[(x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)] {
            if neighbor.0 < 0 || neighbor.1 < 0 {
                continue;
            }
            if is_wall(*neighbor) {
                continue;
            }
            if closed_set.contains(neighbor) {
                continue;
            }
            let tentative_g = gscore[&current] + 1.0;
            if !open_set.contains(neighbor) {
                open_set.insert(*neighbor);
            } else if tentative_g >= gscore[neighbor] {
                continue;
            }

            {
                came_from.insert(*neighbor, current);
            }

            {
                let mut entry = gscore.entry(*neighbor).or_insert(std::f32::INFINITY);
                *entry = tentative_g;
            }


            {
                let mut entry = fscore.entry(*neighbor).or_insert(std::f32::INFINITY);
                *entry = gscore[neighbor] as f32 + heuristic_cost_estimate(*neighbor, goal);
            }
        }
        draw_labyrint_with_path(50, 50, &came_from);
    }
    None
}

fn count_unique_coordinates_after_steps(start: (i32, i32), steps: usize) -> HashSet<(i32, i32)> {
    let mut visited = HashSet::new();
    {
        visited.insert(start);
    }
    for _ in 0..steps {
        let mut v = visited.clone();
        for &(x, y) in &visited {
            for neighbor in &[(x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)] {
                if neighbor.0 < 0 || neighbor.1 < 0 {
                    continue;
                }
                if is_wall(*neighbor) {
                    continue;
                }
                v.insert(*neighbor);
            }
        }
        visited = v;
        draw_labyrint_with_set(50, 50, &visited);
    }
    visited
}

fn main() {
    let result = astar((1, 1), (31, 39)).unwrap();
    let unique = count_unique_coordinates_after_steps((1, 1), 50);

    println!("{}", result.len() - 1);
    println!("{}", unique.len());
}
