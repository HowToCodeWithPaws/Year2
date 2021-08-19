/// Класс для сортировки бинарным поиском наследуется от базовой
/// сортировки и реализует функцию sort.
/// А так это стандартная реализация.

#include "AbstractSort.h"
#include <vector>

class BinInsertionSort : public AbstractSort {
private:
    /// Алгоритм бинарного поиска для нахождения места для
    /// вставки нового элемента. Принимает массив, ключ и правую
    /// границу поиска.
    int binSearch(std::vector<int> &unsorted, int key, int right) {
        int left = 0;

        while (left < right) {
            int middle = (left + right) / 2;
            if (unsorted.size() <= middle) {
                return unsorted.size();
            }

            if (unsorted[middle] > key) {
                right = middle;
            } else {
                left = middle + 1;
            }
        }

        return left;
    }

public:
    /// Устойчивый алгоритм сортировки бинарным поиском:
    /// для каждого элемента находим его индекс вставки
    /// и вставляем, далее обрабатываем неотсортированную часть.
    void sort(std::vector<int> &unsorted) override {
        for (size_t i = 1; i < unsorted.size(); ++i) {
            int curr = unsorted[i];
            size_t ind = binSearch(unsorted, curr, i);

            for (int j = i; j > ind; --j) {
                unsorted[j] = unsorted[j - 1];
            }

            unsorted[ind] = curr;
        }
    }
};