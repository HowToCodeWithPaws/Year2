/// Метод для проверки того, что входящий массив отсортирован
/// - для пустого массива это всегда правда, для непустого
/// проходим по длине, сравнивая пары, и возвращаем результат.

#include <vector>
bool checkSorted(std::vector<int> &sorted) {
    if (sorted.empty()) return true;
    int prev = sorted[0];
    for (size_t i = 1; i < sorted.size(); ++i) {
        if (prev > sorted[i]) return false;
        prev = sorted[i];
    }
    return true;
}