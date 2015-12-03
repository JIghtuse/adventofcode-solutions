#include <iostream>
#include <fstream>
#include <string>
#include <unordered_set>
#include <vector>

using namespace std;

struct Position {
    int x;
    int y;
};

namespace std {
    template<>
    struct hash<Position> {
        size_t operator()(const Position& p) const {
            return hash<int>{}(p.x)^hash<int>{}(p.y);
        }
    };
    template<>
    struct equal_to<Position> {
        bool operator()(const Position& a, const Position& b) const {
            return a.x == b.x && a.y == b.y;
        }
    };
}

string get_data(const string& fname) {
    string data;
    ifstream in{fname};
    if (!in)
        return data;
    in >> data;
    return data;
}

int how_many_houses(const string& fname, int nsantas) {
    string data = get_data(fname);
    if (data.empty()) {
        cout << "no data\n";
        return 1;
    }

    unordered_set<Position> positions;
    vector<Position> cur_positions(nsantas);

    positions.insert(cur_positions[0]);

    for (size_t i = 0; i < data.size(); ++i) {
        int idx = i % nsantas;
        switch (data[i]) {
        case '^': cur_positions[idx].y += 1; break;
        case 'v': cur_positions[idx].y -= 1; break;
        case '>': cur_positions[idx].x += 1; break;
        case '<': cur_positions[idx].x -= 1; break;
        default: break;
        }
        positions.insert(cur_positions[idx]);
    }
    return positions.size();
}

int main() {
    cout << how_many_houses("input", 1) << endl;
    cout << how_many_houses("input", 2) << endl;
}
