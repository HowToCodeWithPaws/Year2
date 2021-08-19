#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <map>
#include <iterator>
using namespace std;


class CouFilter {
private:
    int vid_numb;
    int backet_numb;
    int fpr_numb;
    uint8_t* fingerprint;

    const double fpr = 0.06;
    const int cf_in_b = 4;

    uint8_t _fingerprint(const string& x) {
        const int numb = 53;
        const char* chars = x.c_str();
        long res = 0;
        long curent_pow = 1;
        for (int i = 0; i < x.length(); i++) {
            res += curent_pow * ((int)chars[i]);
            curent_pow *= numb;
        }
        uint8_t result = (uint8_t)(res % 128);
        if (result == 0) {
            result++;
        }
        return result;
    }

    int _hash(int numb) {
        return hash<int>()(numb) % backet_numb;
    }

    int _hash(const string& str) {
        return hash<string>()(str) % backet_numb;
    }

    int round_2(double numb) {
        int current_pow = 1;
        while (numb > current_pow) {
            current_pow *= 2;
        }
        return current_pow;
    }
public:
    CouFilter(int count) {
        vid_numb = count;
        backet_numb = round_2(vid_numb * (1 + fpr));
        fpr_numb = backet_numb * cf_in_b;
        fingerprint = new uint8_t[fpr_numb];

        for (int i = 0; i < fpr_numb; i++) {
            fingerprint[i] = 0;
        }
    }

    bool add(const string& video_name) {
        uint8_t f = _fingerprint(video_name);
        int ind1 = _hash(video_name);
        int ind2 = ind1 ^ _hash(f);
        for (int i = ind1 * cf_in_b; i < (ind1 + 1) * cf_in_b; i++) {
            if (fingerprint[i] == 0) {
                fingerprint[i] = f;
                return true;
            }
        }

        for (int i = ind2 * cf_in_b; i < (ind2 + 1) * cf_in_b; i++) {
            if (fingerprint[i] == 0) {
                fingerprint[i] = f;
                return true;
            }
        }

        int current_ind;
        if (rand() % 2 == 0) {
            current_ind = ind1;
        }
        else {
            current_ind = ind2;
        }

        for (int i = 0; i < 256; i++) {
            int ind = current_ind * cf_in_b + (rand() % cf_in_b);
            uint8_t tmp = fingerprint[ind];
            fingerprint[ind] = f;
            f = tmp;
            current_ind = current_ind ^ _hash(f);
            for (int j = current_ind * cf_in_b; j < (current_ind + 1) * cf_in_b; j++) {
                if (fingerprint[j] == 0) {
                    fingerprint[j] = f;
                    return true;
                }
            }
        }

        return false;
    }

    bool check(const string video_name) {
        uint8_t f = _fingerprint(video_name);
        int ind1 = _hash(video_name);
        int ind2 = ind1 ^ _hash(ind1);

        for (int i = ind1 * cf_in_b; i < (ind1 + 1) * cf_in_b; i++)
        {
            if (fingerprint[i] == f) {
                return true;
            }
        }

        for (int i = ind2 * cf_in_b; i < (ind2 + 1) * cf_in_b; i++)
        {
            if (fingerprint[i] == f) {
                return true;
            }
        }

        return false;
    }

    void remove(const string video_name) {
        uint8_t f = _fingerprint(video_name);
        int ind1 = _hash(video_name);
        int ind2 = ind1 ^ _hash(ind1);

        for (int i = ind1 * cf_in_b; i < (ind1 + 1) * cf_in_b; i++)
        {
            if (fingerprint[i] == f) {
                fingerprint[i] = 0;
            }
        }

        for (int i = ind2 * cf_in_b; i < (ind2 + 1) * cf_in_b; i++)
        {
            if (fingerprint[i] == f) {
                fingerprint[i] = 0;
            }
        }
    }

    ~CouFilter() {
        delete[] fingerprint;
    }
};

// Ризоева Амина вариант
int main(int argc, char** argv)
{
    std::string input_file = "../input/test3.txt", output_file="../output/temp3.txt";

    std::vector<std::string> arguments(argv + 1, argv + argc);

    if (arguments.size() == 2)
    {
        input_file = arguments[0];
        output_file = arguments[1];
    }
    else
    {
        std::cout << "debugging....\n";
    }
    const string ok = "Ok";
    const string mb = "Probably";
    const string no = "No";

    ifstream in;
    in.open(input_file);

    vector<string> outlines;
    string tmp;

    if (!in.is_open()) {
        cout << "файл для чтения не открылся";
        return -1;
    }

    in >> tmp;
    if (tmp != ("videos")) {
        cout << "неверная первая строка файла";
        return -2;
    }

    int video_count;
    in >> video_count;
    outlines.push_back(ok);
    map<string, CouFilter*> m;

    while (in) {
        string command;
        string user;
        string video;
        in >> command >> user >> video;

        if (!in) {
            break;
        }

        if (m.count(user) < 1) {
            m[user] = new CouFilter(video_count);
        }

        if (command == "watch") {
            m[user]->add(video);
            outlines.push_back(ok);
        }
        else if (command == "check") {
            if (m[user]->check(video)) {
                outlines.push_back(mb);
            }
            else {
                outlines.push_back(no);
            }
        }
        else
        {
            cout << "неизвестная команда" << command;
            return -3;
        }
    }
    in.close();

    ofstream out;
    out.open(output_file);
    if (!out.is_open()) {
        cout << "файл для записи не открылся";
        return -1;
    }
    for (int i = 0; i < outlines.size(); i++)
    {
        out << outlines[i] + "\n";
    }

    out.close();

    return 0;
}

