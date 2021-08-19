/// Класс для сортировки слиянием - наследует абстрактную
/// сортировку и реализует требуемый метод, кроме него -
/// собственно рекурсивный метод сортировки и метод слияния.

#include "AbstractSort.h"
#include <vector>

class MergeSort : public AbstractSort {
private:
    /// Метод слияния - создаем буфер, проходим по сливаемым
    /// половинам и сравниваем их элементы, кладем их в буфер
    /// в соответствующем порядке, копируем из буфера в изначальный массив.
    void merge(std::vector<int> &unsorted, size_t left, size_t middle, size_t right) {
        size_t anotherLeft = left, anotherRight = middle + 1;
        std::vector<int> buffer;
        for (int i = left; i <= right; i++) {
            if (anotherLeft <= middle && anotherRight <= right) {
                if (unsorted[anotherLeft] < unsorted[anotherRight]) {
                    buffer.push_back(unsorted[anotherLeft]);
                    anotherLeft++;
                } else {
                    buffer.push_back(unsorted[anotherRight]);
                    anotherRight++;
                }
            } else if (anotherLeft <= middle) {
                buffer.push_back(unsorted[anotherLeft]);
                anotherLeft++;
            } else {
                buffer.push_back(unsorted[anotherRight]);
                anotherRight++;
            }
        }

        for (size_t i = 0; i < buffer.size(); ++i) {
            unsorted[left + i] = buffer[i];
        }
    }

    /// Рекурсивный метод сортировки слияинем - делим массив пополам,
    /// вызываем рекурсию для половин и делаем слияние.
    void recursiveMerge(std::vector<int> &unsorted, int left, int right) {
        if (left < right) {
            size_t middle = (left + right) / 2;
            recursiveMerge(unsorted, left, middle);
            recursiveMerge(unsorted, middle + 1, right);
            merge(unsorted, left, middle, right);
        }
    }

public:
    /// Основной метод с требуемой сигнатурой, из которого
    /// мы не делаем ничего кроме вызова рекурсивного метода
    /// от размеров массива целиком.
    void sort(std::vector<int> &unsorted) override {
        recursiveMerge(unsorted, 0, unsorted.size() - 1);
    }
};
