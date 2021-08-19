using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Diagnostics;

namespace Level2
{
    class Program
    {
        /// <summary>
        /// Я не знаю, пишем ли мы комментарии на алгосах,
        /// поэтому на всякий случай напишу.
        /// </summary>
        /// <param name="args">
        /// Первый параметр - файл с входными данными для теста,
        /// второй - файл для записи результата. Обещали не ломать,
        /// но на всякий случай и для отладки есть вариант без них.
        /// </param>
        static void Main(string[] args)
        {
            if (args.Length == 2)
            {
                string input = File.ReadAllText(args[0]).Trim();

                Console.WriteLine("Input read");

                int[] odds = new int[input.Length];
                int[] evens = new int[input.Length];

                Stopwatch timer = new Stopwatch();
                timer.Start();

                Simple(input, odds, 1);
                Simple(input, evens, 0);

                string answer = $"{odds.Sum() + evens.Sum()} {evens.Sum()} {odds.Sum()}";

                timer.Stop();
                Console.WriteLine("Palindromes counted in "
                    + timer.Elapsed.TotalMilliseconds.ToString() + "ms");

                File.WriteAllText(args[1], answer);

                Console.WriteLine("Output written " + answer);
            }
            else
            {
                string args0 = "test0.txt";
                string args1 = "answer0.txt";

                Console.WriteLine("Please don't try and wreck it");

                string input = File.ReadAllText(args0).Trim();

                Console.WriteLine("Input read");

                int[] odds = new int[input.Length];
                int[] evens = new int[input.Length];

                Stopwatch timer = new Stopwatch();
                timer.Start();

                Simple(input, odds, 1);
                Simple(input, evens, 0);

                string answer = $"{odds.Sum() + evens.Sum()} {evens.Sum()} {odds.Sum()}";

                timer.Stop();
                Console.WriteLine("Palindromes counted in "
                    + timer.Elapsed.TotalMilliseconds.ToString() + "ms");

                File.WriteAllText(args1, answer);

                Console.WriteLine("Output written " + answer);
            }

        }

        /// <summary>
        /// Метод, реализующий алгоритм Манакера для поиска
        /// палиндромов четной и нечетной длин (для них значение
        /// odd будет 0 и 1 соответственно).
        /// </summary>
        /// <param name="inputString"> Строка, где ищем
        /// палиндромы. </param>
        /// <param name="pals"> Массив, в котором на позиции pals[i] 
        /// стоит число палиндромов с центром в i-той букве. </param>
        /// <param name="odd"> Маркер длины искомых палиндромов. </param>
        static void Simple(string inputString, int[] pals, int odd)
        {
            int l = 0;
            int r = -1;

            for (int i = 0; i < inputString.Length; ++i)
            {
                int k = i > r ? odd : Math.Min(pals[l + r - i + 1 - odd], r - i + 1);

                while (i + k < inputString.Length && i - k - 1 + odd >= 0 
                    && inputString[i + k] == inputString[i - k - 1 + odd]) ++k;

                pals[i] = k;

                if (i + k - 1 > r)
                {
                    l = i - k + odd;
                    r = i + k - 1;
                }
            }
        }
    }
}
