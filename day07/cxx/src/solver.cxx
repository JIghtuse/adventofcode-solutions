#include "task.h"

#include <boost/algorithm/string.hpp>
#include <boost/range.hpp>
#include <fstream>
#include <iostream>
#include <sstream>
#include <map>

using namespace std;
using namespace boost::algorithm;
using namespace boost;

using scheme_type = map<const string, task<int> >;

int getter(const string& s, task<int>& dependency)
{
    istringstream in{ s };
    int i;
    return in >> i ? i : dependency.get();
}

int noter(const string& a, task<int>& pa)
{
    return ~getter(a, pa);
}

int main()
{
    map<const string, task<int> > scheme;
    map<const string, int (*)(const string&, const string&, scheme_type&)> two_op{
        { "AND", [](const string& a, const string& b, scheme_type& scheme) { return getter(a, scheme[a]) & getter(b, scheme[b]); } },
        { "OR", [](const string& a, const string& b, scheme_type& scheme) { return getter(a, scheme[a]) | getter(b, scheme[b]); } },
        { "RSHIFT", [](const string& a, const string& b, scheme_type& scheme) { return getter(a, scheme[a]) >> getter(b, scheme[b]); } },
        { "LSHIFT", [](const string& a, const string& b, scheme_type& scheme) { return getter(a, scheme[a]) << getter(b, scheme[b]); } },
    };
    typedef vector<string> split_vector_type;
    string s;
    for (ifstream in{ "input" }; getline(in, s);) {
        split_vector_type sp;
        split(sp, s, is_any_of(" "), token_compress_on);
        switch (sp.size()) {
        case 3:
            scheme[sp[2]].set(getter, sp[0], std::ref(scheme[sp[0]]));
            break;
        case 4:
            scheme[sp[3]].set(noter, sp[1], std::ref(scheme[sp[1]]));
            break;
        case 5:
            scheme[sp[4]].set(two_op[sp[1]], sp[0], sp[2], std::ref(scheme));
            break;
        default:
            throw "not reachable";
        }
    }
    if (scheme.empty()) {
        cerr << "Failed to parse scheme\n";
        return 1;
    }

    cout << scheme["a"].get() << '\n';
}
