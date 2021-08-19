/// Класс для сортировки подсчетом - наследует абстрактную сортировку
/// и реализует требуемую функцию.
/// Неизбежно работает с дополнительной памятью, потому что такая уж она.

#include "AbstractSort.h"
#include <bits/stdc++.h>//добавим INT_MIN и INT_MAX
#include <vector>

class CountSort : public AbstractSort {
public:
    /// Как обычно в сортировке подсчетом находим мин и макс,
    /// заводим массив для подсчета и будущий сортированный массив,
    /// делаем подсчет, переносим в сортированный массив, копируем в изначальный.
    void sort(std::vector<int> &unordered) override {
        int minNumber = INT_MAX, maxNumber = INT_MIN;

        for (size_t i = 0; i < unordered.size(); ++i) {
            if (unordered[i] < minNumber) {
                minNumber = unordered[i];
            }
            if (unordered[i] > maxNumber) {
                maxNumber = unordered[i];
            }
        }

        std::vector<int> count = std::vector<int>(maxNumber - minNumber + 1);
        std::vector<int> sorted = std::vector<int>(unordered.size());

        for (size_t i = 0; i < maxNumber - minNumber + 1; ++i) {
            count[i] = 0;
        }

        for (size_t i = 0; i < unordered.size(); ++i) {
            ++count[unordered[i] - minNumber];
        }

        for (size_t i = 1; i < maxNumber - minNumber + 1; ++i) {
            count[i] = count[i] + count[i - 1];
        }

        for (size_t i = unordered.size() - 1; i + 1 != 0; --i) {
            sorted[count[unordered[i] - minNumber] - 1] = unordered[i];
            --count[unordered[i] - minNumber];
        }

        for (size_t i = 0; i < unordered.size(); ++i) {
            unordered[i] = sorted[i];
        }
    }
};