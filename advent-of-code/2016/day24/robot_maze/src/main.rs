// Travelling Robot Problem

use std::cmp::{self, Ordering};
use std::collections::{HashMap, HashSet};


#[derive(Clone, Copy, Debug, Eq, Hash, Ord, PartialEq, PartialOrd)]
struct Point {
    y: usize,
    x: usize,
}

fn heuristic_cost_estimate(a: Point, b: Point, maze: &[Vec<char>]) -> f64 {
    if b.is_wall(maze) {
        std::f64::INFINITY
    } else {
        let y_square = (cmp::max(a.y, b.y) - cmp::min(a.y, b.y)).pow(2);
        let x_square = (cmp::max(a.x, b.x) - cmp::min(a.x, b.x)).pow(2);
        let squares = y_square + x_square;
        (squares as f64).sqrt()
    }
}

fn reconstruct_path(came_from: &HashMap<Point, Point>, current: Point) -> Vec<(Point)> {
    let mut current = current;
    let mut total_path = vec![current];
    while came_from.contains_key(&current) {
        current = came_from[&current];
        total_path.push(current);
    }
    total_path
}

impl Point {
    fn is_wall(&self, maze: &[Vec<char>]) -> bool {
        maze[self.y][self.x] == '#'
    }

    fn distance(&self, other: Self, maze: &[Vec<char>]) -> f64 {
        let mut closed_set = HashSet::new();

        let mut open_set = HashSet::new();
        open_set.insert(*self);

        let mut came_from = HashMap::new();
        {
            came_from.insert(*self, Point { x: 0, y: 0 });
        }
        let mut gscore: HashMap<Point, f64> = HashMap::new();
        {
            gscore.insert(*self, 0.0);
        }

        let mut fscore: HashMap<Point, f64> = HashMap::new();
        {
            fscore.insert(*self, heuristic_cost_estimate(*self, other, maze));
        }

        while !open_set.is_empty() {
            let current = *open_set.iter()
                .min_by(|&&a, &&b| fscore[&a].partial_cmp(&fscore[&b]).unwrap_or(Ordering::Equal))
                .unwrap();
            if current == other {
                let path = reconstruct_path(&came_from, current);
                return (path.len() - 2) as f64;
            }

            open_set.remove(&current);
            closed_set.insert(current);

            let Point { x, y } = current;
            for neighbor in &[Point { x: x - 1, y: y },
                              Point { x: x + 1, y: y },
                              Point { x: x, y: y - 1 },
                              Point { x: x, y: y + 1 }] {
                if neighbor.is_wall(maze) {
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
                    let mut entry = gscore.entry(*neighbor).or_insert(std::f64::INFINITY);
                    *entry = tentative_g;
                }

                {
                    let mut entry = fscore.entry(*neighbor).or_insert(std::f64::INFINITY);
                    *entry = gscore[neighbor] + heuristic_cost_estimate(*neighbor, other, maze);
                }
            }
        }
        unreachable!()
    }
}

fn travel_rec(init: Point,
              initial_positions: &[(u8, Point)],
              positions: &[(u8, Point)],
              current_distance: f64,
              maze: &[Vec<char>],
              return_back: bool)
              -> (f64, usize) {
    if positions.is_empty() {
        if return_back && init != initial_positions[0].1 {
            (std::f64::INFINITY, 0)
        } else {
            let last = initial_positions.iter()
                .enumerate()
                .find(|&(_, pos)| pos.1 == init)
                .unwrap();
            (current_distance, last.0)
        }
    } else {
        let mut min_distance = usize::max_value() as f64;
        let mut _last_idx = usize::max_value();
        for (i, position) in positions.iter().enumerate() {
            let &(_, next_point) = position;
            let mut p: Vec<_> = positions.iter().cloned().collect();

            p.swap_remove(i);

            let first_distance = init.distance(next_point, maze);

            let (distance, last_idx) = travel_rec(next_point,
                                                  initial_positions,
                                                  &p,
                                                  current_distance + first_distance,
                                                  maze,
                                                  return_back);

            if distance < min_distance {
                min_distance = distance;
                _last_idx = last_idx;
            }
        }
        (min_distance, _last_idx)
    }
}

fn travel(positions: &[(u8, Point)], maze: &[Vec<char>], return_back: bool) -> f64 {
    let (_, init_point) = positions[0];
    let (min_distance, _) = if return_back {
        travel_rec(init_point, positions, positions, 0f64, maze, return_back)
    } else {
        travel_rec(init_point,
                   positions,
                   &positions[1..],
                   0f64,
                   maze,
                   return_back)
    };
    min_distance
}

fn find_fewest_steps_all_numbers(return_back: bool) -> f64 {
    let maze = include_str!("../../input.txt");
    let maze: Vec<Vec<_>> = maze.split_whitespace().map(|s| s.chars().collect()).collect();

    let mut number_positions = vec![];
    for (i, row) in maze.iter().enumerate() {
        for (j, col) in row.iter().enumerate() {
            if col.is_numeric() {
                let c = *col as u8 - b'0';
                number_positions.push((c, Point { y: i, x: j }));
            }
        }
    }
    number_positions.sort();
    travel(&number_positions, &maze, return_back)
}

fn main() {
    let steps = find_fewest_steps_all_numbers(false);
    println!("Fewest number of steps to travel all numbers: {}", steps);

    let steps = find_fewest_steps_all_numbers(true);
    println!("Fewest number of steps to travel all numbers: {}", steps);
}
