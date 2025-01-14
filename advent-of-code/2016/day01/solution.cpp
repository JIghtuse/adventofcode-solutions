#include <array>
#include <iostream>
#include <fstream>
#include <vector>

using Direction = std::pair<int, int>;
using Coordinate = std::pair<int, int>;

const auto kDirections = std::array<Direction, 4>{
    Direction{0, -1},
    Direction{1, 0},
    Direction{0, 1},
    Direction{-1, 0}
};

int rotate(char rotate_direction, int direction_idx)
{
    if (rotate_direction == 'L') {
        direction_idx -= 1;
    } else {
        direction_idx += 1;
    }
    return direction_idx % kDirections.size();
}

struct Command {
    char rotation;
    int length;
};

std::istream& operator>>(std::istream& in, Command& c)
{
    return in >> c.rotation >> c.length;
}

auto read_data(const char *fname)
{
    std::vector<Command> commands;

    for (auto in = std::ifstream{fname}; in;) {
        Command command;
        char c;
        in >> command >> c;
        commands.emplace_back(command);
    }

    return commands;
}

auto distance_from_origin(Coordinate c)
{
    return std::abs(std::get<0>(c)) + std::abs(std::get<1>(c));
}

Coordinate move(Direction direction, int length, Coordinate pos)
{
    return std::make_pair(
        std::get<0>(pos) + std::get<0>(direction) * length,
        std::get<1>(pos) + std::get<1>(direction) * length);
}

int main()
{
    auto commands = read_data("input.txt");
    auto pos = Coordinate{0, 0};
    auto direction_idx = 0;
    for (auto command: commands) {
        direction_idx = rotate(command.rotation, direction_idx);
        auto direction = kDirections[direction_idx];
        pos = move(direction, command.length, pos);
    }
    std::cout << distance_from_origin(pos) << '\n';
}
