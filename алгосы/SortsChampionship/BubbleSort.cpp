/// Класс сортировки пузырьком - наследуется от базовой сортировки,
/// реализует требуемую функцию. Ни больше, ни меньше.

#include "AbstractSort.h"
#include <vector>

class BubbleSort : public AbstractSort {
public:
    /// Проходим по массиву во вложенных циклах, сравниваем
    /// пары, выталкивая самые большие элементы наверх, пока
    /// весь массив не отсортируется.
    void sort(std::vector<int> &unsorted) override {

        for (size_t i = 0; i < unsorted.size(); ++i) {
            for (int j = unsorted.size() - 1; j > i; --j) {
                if (unsorted[j - 1] > unsorted[j]) {
                    std::swap(unsorted[j - 1], unsorted[j]);
                }
            }
        }
    }
};