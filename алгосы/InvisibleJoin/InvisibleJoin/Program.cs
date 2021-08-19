/// Зубарева Наталия БПИ195
/// i wanna fucking die
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Collections.Specialized;
using System.Dynamic;
using System.Collections;
using System.Diagnostics;

namespace InvisibleJoin
{
    /// <summary>
    /// Структура для запроса фильтрации -
    /// из какой таблицы какое поле надо брать, каким оператором
    /// сравнивать и с каким значением.
    /// </summary>
    struct Request
    {
        public Request(string tableName, string parameter, string operators, string value)
        {
            this.tableName = tableName;
            this.parameter = parameter;
            this.operators = operators;
            this.value = value;
        }
        string tableName;
        string parameter;
        string operators;
        string value;
        public string GetValue() { return value; }
        public string GetTableName() { return tableName; }
        public string GetParameter() { return parameter; }
        public string GetOperators() { return operators; }
    }

    /// <summary>
    /// Структура для запроса выходных полей - 
    /// из какой таблицы и какое поле надо брать.
    /// </summary>
    struct OutputFields
    {
        public OutputFields(string tableName, string parameter)
        {
            this.tableName = tableName;
            this.parameter = parameter;
        }
        string tableName;
        string parameter;
        public string GetTableName() { return tableName; }
        public string GetParameter() { return parameter; }
    }

    class Program
    {
        /// <summary>
        /// Поля для путей к информации и файлам входных-выходных
        /// данных, списки для структур запросов-фильтров и выходных
        /// полей, массив роаринг битмапов для запросов и один большой
        /// роаринг битмап чтобы ими править,
        /// строка для ответа, переменная для размера большой таблицы,
        /// массивы для хранения таблицы фактов и таблиц ключей.
        /// </summary>
        static string pathToData /*= "../../data"*/,
            pathToTest /*= "../../input/test_937.txt"*/,
            pathToAnswer /*= "../../output/temp937.txt"*/;
        static List<OutputFields> outputFields;
        static List<Request> searchRequests;
        static RoaringBitmap[] roaringBitmaps;
        static RoaringBitmap theRoaringBitmap;
        static int longestTable = 0;
        static string[][][] tablesDim;
        static string[][] tablesKey;
        static Stopwatch genaralWatch;
        static Stopwatch specificWatch;

        /// <summary>
        /// Точка входа, тут просиходит проверка входных параметров,
        /// инициализация основных полей, вызов основных методов для работы.
        /// </summary>
        /// <param name="args"></param>
        static void Main(string[] args)
        {
            genaralWatch = new Stopwatch();
            genaralWatch.Start();
            if (args.Length < 3)
            {
                Console.WriteLine("Not enough input, try again and stop ruining this damned thing");
                return;
            }
            pathToData = args[0];
            pathToTest = args[1];
            pathToAnswer = args[2];
            Console.WriteLine(pathToData + " " +
                pathToTest + " " + pathToAnswer +
                "\nбитмапы бегут ревут.");

            outputFields = new List<OutputFields>();
            searchRequests = new List<Request>();
            tablesDim = new string[8][][];
            tablesDim[7] = new string[5][];
            tablesKey = new string[7][];

            /// Вызв методов считывания - подготовки, самого поиска и вывода.
            /// Отлов исключений нужен потому, что csv файлы как-то ужасно сильно 
            /// любят падать на считывании.
            try
            {
                ReadInput();
                Find();
                Write();
            }
            catch (IOException)
            {
                Console.WriteLine("Вы пытались запустить программу когда файл с таблицей открыт? " +
                    "Так не надо. Запустите снова.");
            }
        }

        /// <summary>
        /// Метод для считывания всего, что нам надо считать 
        /// - входного файла с запросом, затем всех необходимых
        /// таблиц, формирование массива битмапов по количеству запросов.
        /// </summary>
        static void ReadInput()
        {
            ReadRequest();
            ReadKeyDimTables();
            ReadKeyTables();
            ReadFactTables();
            roaringBitmaps = new RoaringBitmap[searchRequests.Count];
        }

        /// <summary>
        /// Метод для считывания всех видов таблиц, которые участвуют либо в 
        /// запросах, либо в полях для вывода. Это какая-то маленькая оптимизация,
        /// потому что вроде как всю информацию мы не читаем, но все равно 
        /// читаем некоторое количество лишней.
        /// </summary>
        static void ReadKeyDimTables()
        {
            foreach (Request request in searchRequests)
            {
                if (request.GetTableName().StartsWith("Dim"))
                {
                    string keyName = request.GetTableName().Equals("DimDate") ? "OrderDateKey" :
                    request.GetTableName().Substring(3) + "Key";
                    string pathToDim = pathToData + "/" + request.GetTableName() + ".csv";
                    string pathToKey = pathToData + "/FactResellerSales." + keyName + ".csv";

                    string[] linesKey = File.ReadAllLines(pathToKey).Where(e
                      => e.Trim() != "").ToArray();
                    string[] linesDim = File.ReadAllLines(pathToDim).Where(e
                           => e.Trim() != "").ToArray();

                    string[][] info = new string[linesDim.Length][];
                    for (int i = 0; i < linesDim.Length; i++)
                    {
                        info[i] = linesDim[i].Split('|');
                    }

                    if (linesKey.Length > longestTable)
                    {
                        longestTable = linesKey.Length;
                    }

                    if (info.Length > longestTable)
                    {
                        longestTable = info.Length;
                    }

                    FillTableDim(request.GetTableName(), info);
                    FillTableKey(keyName, linesKey);
                }
            }

            foreach (OutputFields outputField in outputFields)
            {
                if (outputField.GetTableName().StartsWith("Dim"))
                {
                    string keyName = outputField.GetTableName().Equals("DimDate") ? "OrderDateKey" :
                    outputField.GetTableName().Substring(3) + "Key";
                    string pathToDim = pathToData + "/" + outputField.GetTableName() + ".csv";
                    string pathToKey = pathToData + "/FactResellerSales." + keyName + ".csv";

                    string[] linesKey = File.ReadAllLines(pathToKey).Where(e
                      => e.Trim() != "").ToArray();
                    string[] linesDim = File.ReadAllLines(pathToDim).Where(e
                           => e.Trim() != "").ToArray();

                    string[][] info = new string[linesDim.Length][];
                    for (int i = 0; i < linesDim.Length; i++)
                    {
                        info[i] = linesDim[i].Split('|');
                    }

                    if (linesKey.Length > longestTable)
                    {
                        longestTable = linesKey.Length;
                    }

                    if (info.Length > longestTable)
                    {
                        longestTable = info.Length;
                    }

                    FillTableDim(outputField.GetTableName(), info);
                    FillTableKey(keyName, linesKey);
                }
            }
        }

        static void ReadKeyTables()
        {
            foreach (Request request in searchRequests)
            {
                if (request.GetTableName().StartsWith("FactResellerSales") && request.GetParameter().Contains("Key"))
                {
                    string[] lines = File.ReadAllLines(pathToData + "/" + request.GetTableName() + "." + request.GetParameter() + ".csv").Where(e
                          => e.Trim() != "").ToArray();
                    if (lines.Length > longestTable)
                    {
                        longestTable = lines.Length;
                    }

                    FillTableKey(request.GetParameter(), lines);
                }
            }

            foreach (OutputFields outputField in outputFields)
            {
                if (outputField.GetTableName().StartsWith("FactResellerSales") && outputField.GetParameter().Contains("Key"))
                {
                    string[] lines = File.ReadAllLines(pathToData + "/" + outputField.GetTableName() + "." + outputField.GetParameter() + ".csv").Where(e
                          => e.Trim() != "").ToArray();
                    if (lines.Length > longestTable)
                    {
                        longestTable = lines.Length;
                    }

                    FillTableKey(outputField.GetParameter(), lines);
                }
            }
        }

        static void ReadFactTables()
        {
            foreach (Request request in searchRequests)
            {
                if (request.GetTableName().StartsWith("FactResellerSales") && !request.GetParameter().Contains("Key"))
                {
                    string[] lines = File.ReadAllLines(pathToData + "/" + request.GetTableName() + "." + request.GetParameter() + ".csv").Where(e
                          => e.Trim() != "").ToArray();
                    if (lines.Length > longestTable)
                    {
                        longestTable = lines.Length;
                    }



                    FillTableDim(request.GetParameter(), lines);
                }
            }

            foreach (OutputFields outputField in outputFields)
            {
                if (outputField.GetTableName().StartsWith("FactResellerSales") && !outputField.GetParameter().Contains("Key"))
                {
                    string[] lines = File.ReadAllLines(pathToData + "/" + outputField.GetTableName() + "." + outputField.GetParameter() + ".csv").Where(e
                          => e.Trim() != "").ToArray();
                    if (lines.Length > longestTable)
                    {
                        longestTable = lines.Length;
                    }

                    FillTableDim(outputField.GetParameter(), lines);
                }
            }
        }

        /// <summary>
        /// Метод для считывания информации из входного файла 
        /// - какие фильтры мы применяем, какие выходные поля хотим. 
        /// Создаются массивы из соответствующих структур.
        /// </summary>
        static void ReadRequest()
        {
            string[] lines = File.ReadAllLines(pathToTest);
            string[] toOutputFields = lines[0].Split(',');

            for (int i = 0; i < toOutputFields.Length; i++)
            {
                outputFields.Add(new OutputFields((toOutputFields[i].Split('.'))[0], (toOutputFields[i].Split('.'))[1]));
            }

            int numberSort = int.Parse(lines[1]);
            if (numberSort < 0 || numberSort > 1000)
            {
                throw new ArgumentException("the number doesnt fit in the bounds");
            }

            for (int i = 0; i < numberSort; i++)
            {
                string[] requests = lines[2 + i].Split(new char[] { ' ' }, 3);
                searchRequests.Add(new Request(requests[0].Split('.')[0],
                    requests[0].Split('.')[1],
                    requests[1], requests[2].Trim('\'')));
            }
        }

        /// <summary>
        /// Метод, в котором собственно происходит инвизибл джойн и осуществляется
        /// поиск данных по запросам и формирование финальных данных.
        /// </summary>
        static void Find()
        {
            //   InsteadOfOneAndTwo(); 
            specificWatch = new Stopwatch();
            specificWatch.Start();

            FirstAndSecondPhases();

            ThirdPhase();
        }

        /// <summary>
        /// Первая и вторая фазы алгоритма инвизибл джойн - на первой фазе мы
        /// получаем первичные ключи из таблиц Dim (и обычной таблицы фактов, но там
        /// скучно), делаем по ним битмапы для каждого запроса
        /// На второй фазе по таблицам ключей битмапы запросов расширяются на битмап
        /// для всей таблицы, умножаются на него и в итоге формируется итоговый битмап.
        /// </summary>
        static void FirstAndSecondPhases()
        {
            /*RoaringBitmap[]*/
            roaringBitmaps = new RoaringBitmap[searchRequests.Count];

            Console.WriteLine("Starting phase one");

            //FirstFuckingPhase
            for (int i = 0; i < searchRequests.Count; i++)
            {
                dynamic currentTable;

                if (searchRequests[i].GetTableName().Equals("FactResellerSales"))
                {
                    currentTable = GetFactArray(searchRequests[i].GetParameter());
                }
                else
                {
                    currentTable = GetTableDimArray(searchRequests[i].GetTableName());
                }

                roaringBitmaps[i] = new RoaringBitmap(currentTable.Length);

                for (int j = 0; j < currentTable.Length; j++)
                {
                    if (searchRequests[i].GetTableName().Equals("FactResellerSales"))
                    {
                        if (Compare(GetTableDim(searchRequests[i].GetTableName(),
                       searchRequests[i].GetParameter(), j),
                       searchRequests[i].GetValue(), searchRequests[i].GetOperators()))
                        {
                            roaringBitmaps[i].Set(j, true);
                        }
                        else
                        {
                            roaringBitmaps[i].Set(j, false);
                        }
                    }
                    else
                    {
                        if (Compare(GetTableDim(searchRequests[i].GetTableName(),
                    searchRequests[i].GetParameter(), j),
                    searchRequests[i].GetValue(), searchRequests[i].GetOperators()))
                        {
                            roaringBitmaps[i].Set(int.Parse(GetTableDimFirstKey(searchRequests[i].GetTableName(),
                            searchRequests[i].GetParameter(), j)), true);
                        }
                        else
                        {
                            roaringBitmaps[i].Set(int.Parse(GetTableDimFirstKey(searchRequests[i].GetTableName(),
                           searchRequests[i].GetParameter(), j)), false);
                        }
                    }


                }
                Console.WriteLine("Filter " + i + " done");
            }

            theRoaringBitmap = new RoaringBitmap(longestTable, true);

            Console.WriteLine("Starting phase two");

            //SecondSONOFABITCHPhase
            for (int j = 0; j < searchRequests.Count; j++)
            {
                RoaringBitmap bitmap = new RoaringBitmap(longestTable);

                for (int i = 0; i < longestTable; i++)
                {
                    if (searchRequests[j].GetTableName().Equals("FactResellerSales"))
                    {
                        bitmap.Set(i, roaringBitmaps[j].Get(i));
                    }
                    else
                    {
                        bitmap.Set(i, roaringBitmaps[j].Get(int.Parse(GetKeyByDim(searchRequests[j].GetTableName(), i))));
                    }
                }

                theRoaringBitmap.And(bitmap);
                Console.WriteLine("Filter " + j + " done");
            }
        }

        ///Это был хороший замечательный восхитительный метод в котором первая
        ///фаза происходила внутри второй и он работал хорошо. Земля ему пухом.
        //static void InsteadOfOneAndTwo()
        //{
        //    for (int k = 0; k < searchRequests.Count; k++)
        //    {
        //        //              Console.WriteLine("here1");
        //        Bitmap bitmap = new Bitmap(longestTable);

        //        for (int i = 0; i < longestTable; i++)
        //        {
        //            //    int key = int.Parse(linesKey[i]);
        //            //                   Console.WriteLine(k+" "+i+" here2");
        //            if (Compare(GetTableDim(searchRequests[k].GetTableName(),
        //                searchRequests[k].GetParameter(), i).ToString(),
        //                searchRequests[k].GetValue(), searchRequests[k].GetOperators()))
        //            {
        //                Console.WriteLine("\aimportant" + i);
        //                bitmap.Set(i, true);
        //            }
        //            else bitmap.Set(i, false);
        //        }

        //        Console.WriteLine("at 998 " + bitmap.Get(998) + " at 59079 " + bitmap.Get(59079) + " at 1000 " + bitmap.Get(1000));

        //        //for (int p = 0; p < bitmap.Size(); p++)
        //        //{
        //        //    Console.WriteLine(bitmap.Get(p));
        //        //}
        //        theBitmap.And(bitmap);
        //        //               Console.WriteLine("here4");
        //    }
        //}

        /// <summary>
        /// Здесь мы собираем информацию из обозначенных в требованиях к выходным данным таблиц 
        /// с помощью битмапа подходящих элементов. Тут же выводится ответ в потоках, ибо так быстрее.
        /// </summary>
        static void ThirdPhase()
        {
            /// Реализация чтения с использованием файловых потоков.
            /// Альтернативно можно было руками создавать новый поток и
            /// осуществлять Flush и Dispose, но так удобнее.
            using (FileStream fs = new FileStream(pathToAnswer, FileMode.Create, FileAccess.Write))
            {
                using (StreamWriter sw = new StreamWriter(fs, Encoding.Unicode))
                {
                    Console.WriteLine("Starting phase 3");

                    //phase3
                    for (int i = 0; i < longestTable; i++)
                    {
                        if (theRoaringBitmap.Get(i))
                        {
                            StringBuilder answerToConsole = new StringBuilder();
                            for (int k = 0; k < outputFields.Count; k++)
                            {
                                answerToConsole.Append(GetTableDimThroughKey(outputFields[k].GetTableName(),
          outputFields[k].GetParameter(), i) + "|");
                            }
                            sw.WriteLine(answerToConsole.ToString().Substring(0, answerToConsole.Length - 1));
                        }
                    }

                }
            }
        }

        /// <summary>
        /// Метод для вывода результата по времени
        /// </summary>
        static void Write()
        {
            genaralWatch.Stop();
            specificWatch.Stop();
            Console.WriteLine("Программа работала " + genaralWatch.Elapsed.TotalSeconds
                + " секунд, из которых " + specificWatch.Elapsed.TotalSeconds + " происходила работа алгоритма.");
                 }

        /// <summary>
        /// Метод для сравнения значений с значением в запросе в зависимости от символа
        /// сравнения в запросе.
        /// </summary>
        /// <param name="first"></param>
        /// <param name="second"></param>
        /// <param name="operators"></param>
        /// <returns></returns>
        static bool Compare(string first, string second, string operators)
        {
            switch (operators)
            {
                case "=":
                    return first.Equals(second);
                case "<>":
                    return !first.Equals(second);
                case "<":
                    int firstInt, secondInt;
                    if (int.TryParse(first, out firstInt) && int.TryParse(second, out secondInt))
                    {
                        return firstInt < secondInt;
                    };
                    return false;
                case ">":
                    if (int.TryParse(first, out firstInt) && int.TryParse(second, out secondInt))
                    {
                        return firstInt > secondInt;
                    };
                    return false;
                case "<=":
                    if (int.TryParse(first, out firstInt) && int.TryParse(second, out secondInt))
                    {
                        return firstInt <= secondInt;
                    };
                    return false;
                case ">=":
                    if (int.TryParse(first, out firstInt) && int.TryParse(second, out secondInt))
                    {
                        return firstInt >= secondInt;
                    };
                    return false;
                default:
                    return false;
            }

        }
        /// <summary>
        /// Методы для заполнения соответствующих массивов для таблиц
        /// самими таблицами.
        /// </summary>
        /// <param name="tableName"></param>
        /// <param name="array"></param>
        static void FillTableKey(string tableName, string[] array)
        {
            switch (tableName)
            {
                case "PromotionKey":
                    tablesKey[0] = array;
                    return;
                case "SalesTerritoryKey":
                    tablesKey[1] = array;
                    return;
                case "ProductKey":
                    tablesKey[2] = array;
                    return;
                case "OrderDateKey":
                    tablesKey[3] = array;
                    return;
                case "ResellerKey":
                    tablesKey[4] = array;
                    return;
                case "CurrencyKey":
                    tablesKey[5] = array;
                    return;
                case "EmployeeKey":
                    tablesKey[6] = array;
                    return;
                default:
                    break;
            }
        }

        static void FillTableDim(string tableName, string[] array)
        {
            switch (tableName)
            {
                case "CarrierTrackingNumber":
                    tablesDim[7][0] = array;
                    return;
                case "CustomerPONumber":
                    tablesDim[7][1] = array;
                    return;
                case "OrderQuantity":
                    tablesDim[7][2] = array;
                    return;
                case "SalesOrderLineNumber":
                    tablesDim[7][3] = array;
                    return;
                case "SalesOrderNumber":
                    tablesDim[7][4] = array;
                    return;
                default:
                    break;
            }
        }

        static void FillTableDim(string tableName, string[][] array)
        {
            switch (tableName)
            {
                case "DimPromotion":
                    tablesDim[0] = array;
                    return;
                case "DimSalesTerritory":
                    tablesDim[1] = array;
                    return;
                case "DimProduct":
                    tablesDim[2] = array;
                    return;
                case "DimDate":
                    tablesDim[3] = array;
                    return;
                case "DimReseller":
                    tablesDim[4] = array;
                    return;
                case "DimCurrency":
                    tablesDim[5] = array;
                    return;
                case "DimEmployee":
                    tablesDim[6] = array;
                    return;
                default:
                    break;
            }
        }

        /// <summary>
        /// далее следует очень много методов для того чтобы получать
        /// какую-то информацию из таблиц - с использованием таблиц ключей,
        /// без использования, получать всю таблицу, только один элемент по индексу
        /// или первый элемент, таблица фактов или таблица ключей или таблица дим 
        /// - все учтено (мне искренне стыдно).
        /// </summary>
        /// <param name="tableName"></param>
        /// <param name="parameter"></param>
        /// <param name="index"></param>
        /// <returns></returns>
        static string GetTableDimThroughKey(string tableName, string parameter, int index)
        {
            int key;
            switch (tableName)
            {
                case "DimPromotion":
                    key = int.Parse(tablesKey[0][index]) - 1;
                    return GetDimPromotion(parameter, key);
                case "DimSalesTerritory":
                    key = int.Parse(tablesKey[1][index]) - 1;
                    return GetDimSalesTerritory(parameter, key);
                case "DimProduct":
                    key = int.Parse(tablesKey[2][index]) - 1;
                    return GetDimProduct(parameter, key);
                case "DimDate":
                    for (int i = 0; i < tablesDim[3].Length; i++)
                    {
                        if (tablesKey[3][index].Equals(tablesDim[3][i][0]))
                        {
                            return GetDimDate(parameter, i);
                        }
                    }
                    return "";
                case "DimReseller":
                    key = int.Parse(tablesKey[4][index]) - 1;
                    return GetDimReseller(parameter, key);
                case "DimCurrency":
                    key = int.Parse(tablesKey[5][index]) - 1;
                    return GetDimCurrency(parameter, key);
                case "DimEmployee":
                    key = int.Parse(tablesKey[6][index]) - 1;
                    return GetDimEmployee(parameter, key);
                case "FactResellerSales":
                    return GetFact(parameter, index);
                default:
                    return null;
            }
        }

        static string[][] GetTableDimArray(string tableName)
        {
            switch (tableName)
            {
                case "DimPromotion":
                    return tablesDim[0];
                case "DimSalesTerritory":
                    return tablesDim[1];
                case "DimProduct":
                    return tablesDim[2];
                case "DimDate":
                    return tablesDim[3];
                case "DimReseller":
                    return tablesDim[4];
                case "DimCurrency":
                    return tablesDim[5];
                case "DimEmployee":
                    return tablesDim[6];
                default:
                    return null;
            }
        }

        static string GetTableDim(string tableName, string parameter, int index)
        {
            switch (tableName)
            {
                case "DimPromotion":
                    return GetDimPromotion(parameter, index);
                case "DimSalesTerritory":
                    return GetDimSalesTerritory(parameter, index);
                case "DimProduct":
                    return GetDimProduct(parameter, index);
                case "DimDate":
                    return GetDimDate(parameter, index);
                case "DimReseller":
                    return GetDimReseller(parameter, index);
                case "DimCurrency":
                    return GetDimCurrency(parameter, index);
                case "DimEmployee":
                    return GetDimEmployee(parameter, index);
                case "FactResellerSales":
                    return GetFact(parameter, index);
                default:
                    return null;
            }
        }

        static string GetTableDimFirstKey(string tableName, string parameter, int index)
        {
            switch (tableName)
            {
                case "DimPromotion":
                    return tablesDim[0][index][0];
                case "DimSalesTerritory":
                    return tablesDim[1][index][0];
                case "DimProduct":
                    return tablesDim[2][index][0];
                case "DimDate":
                    return tablesDim[3][index][0];
                case "DimReseller":
                    return tablesDim[4][index][0];
                case "DimCurrency":
                    return tablesDim[5][index][0];
                case "DimEmployee":
                    return tablesDim[6][index][0];
                case "FactResellerSales":
                    return GetFact(parameter, index);
                default:
                    return null;
            }
        }

        /// зачем все это читать? оно просто нужно для всякого. поставьте 10 пожалуйста
        static string GetKey(string tableName, int index)
        {
            switch (tableName)
            {
                case "PromotionKey":
                    return tablesKey[0][index];
                case "SalesTerritoryKey":
                    return tablesKey[1][index];
                case "ProductKey":
                    return tablesKey[2][index];
                case "OrderDateKey":
                    return tablesKey[3][index];
                case "ResellerKey":
                    return tablesKey[4][index];
                case "CurrencyKey":
                    return tablesKey[5][index];
                case "EmployeeKey":
                    return tablesKey[6][index];
                default:
                    return null;
            }
        }

        static string GetKeyByDim(string tableName, int index)
        {
            switch (tableName)
            {
                case "DimPromotion":
                    return GetKey("PromotionKey", index);
                case "DimSalesTerritory":
                    return GetKey("SalesTerritoryKey", index);
                case "DimProduct":
                    return GetKey("ProductKey", index);
                case "DimDate":
                    return GetKey("OrderDateKey", index);
                case "DimReseller":
                    return GetKey("ResellerKey", index);
                case "DimCurrency":
                    return GetKey("CurrencyKey", index);
                case "DimEmployee":
                    return GetKey("EmployeeKey", index);
                default:
                    return null;
            }
        }

        static string[] GetFactArray(string parameter)
        {
            switch (parameter)
            {
                case "CarrierTrackingNumber":
                    return tablesDim[7][0];
                case "CustomerPONumber":
                    return tablesDim[7][1];
                case "OrderQuantity":
                    return tablesDim[7][2];
                case "SalesOrderLineNumber":
                    return tablesDim[7][3];
                case "SalesOrderNumber":
                    return tablesDim[7][4];
                case "PromotionKey":
                    return tablesKey[0];
                case "SalesTerritoryKey":
                    return tablesKey[1];
                case "ProductKey":
                    return tablesKey[2];
                case "OrderDateKey":
                    return tablesKey[3];
                case "ResellerKey":
                    return tablesKey[4];
                case "CurrencyKey":
                    return tablesKey[5];
                case "EmployeeKey":
                    return tablesKey[6];
                default:
                    return null;
            }
        }

        static string GetFact(string parameter, int index)
        {
            switch (parameter)
            {
                case "CarrierTrackingNumber":
                    return tablesDim[7][0][index];
                case "CustomerPONumber":
                    return tablesDim[7][1][index];
                case "OrderQuantity":
                    return tablesDim[7][2][index];
                case "SalesOrderLineNumber":
                    return tablesDim[7][3][index];
                case "SalesOrderNumber":
                    return tablesDim[7][4][index];
                case "PromotionKey":
                    return tablesKey[0][index];
                case "SalesTerritoryKey":
                    return tablesKey[1][index];
                case "ProductKey":
                    return tablesKey[2][index];
                case "OrderDateKey":
                    return tablesKey[3][index];
                case "ResellerKey":
                    return tablesKey[4][index];
                case "CurrencyKey":
                    return tablesKey[5][index];
                case "EmployeeKey":
                    return tablesKey[6][index];
                default:
                    return null;
            }
        }

        static string GetDimPromotion(string parameter, int index)
        {
            switch (parameter)
            {
                case "PromotionKey":
                    return tablesDim[0][index][0];
                case "PromotionAlternateKey":
                    return tablesDim[0][index][1];
                case "EnglishPromotionName":
                    return tablesDim[0][index][2];
                case "EnglishPromotionType":
                    return tablesDim[0][index][3];
                case "EnglishPromotionCategory":
                    return tablesDim[0][index][4];
                case "StartDate":
                    return tablesDim[0][index][5];
                case "EndDate":
                    return tablesDim[0][index][6];
                case "MinQty":
                    return tablesDim[0][index][7];
                default:
                    return null;
            }
        }

        static string GetDimSalesTerritory(string parameter, int index)
        {
            switch (parameter)
            {
                case "SalesTerritoryKey":
                    return tablesDim[1][index][0];
                case "SalesTerritoryAlternateKey":
                    return tablesDim[1][index][1];
                case "SalesTerritoryRegion":
                    return tablesDim[1][index][2];
                case "SalesTerritoryCountry":
                    return tablesDim[1][index][3];
                case "SalesTerritoryGroup":
                    return tablesDim[1][index][4];
                default:
                    return null;
            }
        }

        static string GetDimProduct(string parameter, int index)
        {
            switch (parameter)
            {
                case "ProductKey":
                    return tablesDim[2][index][0];
                case "ProductAlternateKey":
                    return tablesDim[2][index][1];
                case "EnglishProductName":
                    return tablesDim[2][index][2];
                case "Color":
                    return tablesDim[2][index][3];
                case "SafetyStockLevel":
                    return tablesDim[2][index][4];
                case "ReorderPoint":
                    return tablesDim[2][index][5];
                case "SizeRange":
                    return tablesDim[2][index][6];
                case "DaysToManufacture":
                    return tablesDim[2][index][7];
                case "StartDate":
                    return tablesDim[2][index][8];
                default:
                    return null;
            }
        }

        static string GetDimDate(string parameter, int index)
        {
            switch (parameter)
            {
                case "DateKey":
                    return tablesDim[3][index][0];
                case "FullDateAlternateKey":
                    return tablesDim[3][index][1];
                case "DayNumberOfWeek":
                    return tablesDim[3][index][2];
                case "EnglishDayNameOfWeek":
                    return tablesDim[3][index][3];
                case "DayNumberOfMonth":
                    return tablesDim[3][index][4];
                case "DayNumberOfYear":
                    return tablesDim[3][index][5];
                case "WeekNumberOfYear":
                    return tablesDim[3][index][6];
                case "EnglishMonthName":
                    return tablesDim[3][index][7];
                case "MonthNumberOfYear":
                    return tablesDim[3][index][8];
                case "CalendarQuarter":
                    return tablesDim[3][index][9];
                case "CalendarYear":
                    return tablesDim[3][index][10];
                case "CalendarSemester":
                    return tablesDim[3][index][11];
                case "FiscalQuarter":
                    return tablesDim[3][index][12];
                case "FiscalYear":
                    return tablesDim[3][index][13];
                case "FiscalSemester":
                    return tablesDim[3][index][14];
                default:
                    return null;
            }
        }

        static string GetDimReseller(string parameter, int index)
        {
            switch (parameter)
            {
                case "ResellerKey":
                    return tablesDim[4][index][0];
                case "ResellerAlternateKey":
                    return tablesDim[4][index][1];
                case "Phone":
                    return tablesDim[4][index][2];
                case "BusinessType":
                    return tablesDim[4][index][3];
                case "ResellerName":
                    return tablesDim[4][index][4];
                case "NumberEmployees":
                    return tablesDim[4][index][5];
                case "OrderFrequency":
                    return tablesDim[4][index][6];
                case "ProductLine":
                    return tablesDim[4][index][7];
                case "AddressLine1":
                    return tablesDim[4][index][8];
                case "BankName":
                    return tablesDim[4][index][9];
                case "YearOpened":
                    return tablesDim[4][index][10];
                default:
                    return null;
            }
        }

        static string GetDimCurrency(string parameter, int index)
        {
            switch (parameter)
            {
                case "CurrencyKey":
                    return tablesDim[5][index][0];
                case "CurrencyAlternateKey":
                    return tablesDim[5][index][1];
                case "CurrencyName":
                    return tablesDim[5][index][2];
                default:
                    return null;
            }
        }

        static string GetDimEmployee(string parameter, int index)
        {
            switch (parameter)
            {
                case "EmployeeKey":
                    return tablesDim[6][index][0];
                case "FirstName":
                    return tablesDim[6][index][1];
                case "LastName":
                    return tablesDim[6][index][2];
                case "Title":
                    return tablesDim[6][index][3];
                case "BirthDate":
                    return tablesDim[6][index][4];
                case "LoginID":
                    return tablesDim[6][index][5];
                case "EmailAddress":
                    return tablesDim[6][index][6];
                case "Phone":
                    return tablesDim[6][index][7];
                case "MaritalStatus":
                    return tablesDim[6][index][8];
                case "Gender":
                    return tablesDim[6][index][9];
                case "PayFrequency":
                    return tablesDim[6][index][10];
                case "VacationHours":
                    return tablesDim[6][index][11];
                case "SickLeaveHours":
                    return tablesDim[6][index][12];
                case "DepartmentName":
                    return tablesDim[6][index][13];
                case "StartDate":
                    return tablesDim[6][index][14];
                default:
                    return null;
            }
        }
    }

    /// <summary>
    /// Абстрактный класс для битмапов: битмап умеет доставать по индексу,
    /// задавать по индексу и умножаться на себя.
    /// </summary>
    public abstract class AbstractBitmap
    {
        public abstract bool Get(int index);

        public abstract void Set(int index, bool value);

        public abstract void And(AbstractBitmap value);

        public AbstractBitmap() { }
    }

    /// <summary>
    /// Класс для бывших просто битмапов
    /// </summary>
    //public class Bitmap : AbstractBitmap
    //{
    //    private readonly long[] _mapArray;
    //    private readonly int _mapLength;
    //    private int _bitsCount = -1;
    //    public const int BitShiftPerInt64 = 6;

    //    public override bool Get(int index)
    //    {
    //        index = _mapLength - index;
    //        var (arrIndex, bit) = Div64Remainder(index);
    //        return (_mapArray[arrIndex] & (1L << bit)) != 0;
    //    }

    //    public override void Set(int index, bool value)
    //    {
    //        index = _mapLength - index;
    //        var (arrIndex, bit) = Div64Remainder(index);
    //        var newValue = _mapArray[arrIndex];
    //        if (value)
    //        {
    //            newValue |= 1L << bit;
    //        }
    //        else
    //        {
    //            newValue &= ~(1L << bit);
    //        }
    //        _mapArray[arrIndex] = newValue;
    //        _bitsCount = -1;
    //    }

    //    public override void And(AbstractBitmap value)
    //    {
    //        var count = _mapArray.Length;

    //        var i = 0;
    //        for (; i < count; i++)
    //        {
    //            _mapArray[i] &= (value as Bitmap).GetArray()[i];
    //        }
    //        _bitsCount = -1;
    //        //     return this;
    //    }

    //    public long[] GetArray() => _mapArray;

    //    public Bitmap(int length) : this(length, false) => _bitsCount = 0;

    //    public Bitmap(int length, bool defaultValue)/*:this(length, defaultValue)*/
    //    {
    //        _mapArray = new long[GetArrayLength(length)];
    //        _mapLength = length;
    //        if (defaultValue)
    //        {
    //            for (var i = 0; i < _mapArray.Length; i++)
    //            {
    //                _mapArray[i] = -1L;
    //            }
    //        }

    //        _bitsCount = defaultValue ? length : 0;
    //    }

    //    public int Size() { return _mapLength; }

    //    public static int GetArrayLength(int n) =>
    //       (int)((uint)(n - 1 + (1L << BitShiftPerInt64)) >> BitShiftPerInt64);

    //    public static (int quotient, int remainder) Div64Remainder(int number)
    //    {
    //        var quotient = (uint)number / 64;
    //        var remainder = number & (64 - 1);
    //        return ((int)quotient, remainder);
    //    }
    //}


    /// <summary>
    /// Абстрактный класс для контейнеров, чтобы хранить
    /// лист с контейнерами в роаринг битмап. вообще контейнер
    /// в вакууме должен уметь задавать элемент, возвращать по индексу
    /// и умножаться с собой.
    /// </summary>
    public abstract class Containers
    {
        public abstract void Set(int index, bool value);
        public abstract bool Get(int index);
        public abstract void And(Containers container);
    }

    /// <summary>
    /// Это контейнер битмап. Он милый, обаятельный,
    /// и у него битовые операции.
    /// Чтобы здесь что-то получить или куда-то положить,
    /// нужно осуществить сдвиг по фазе и не один
    /// но оно работает и хорошо и надежно хранит плотные данные.
    /// </summary>
    public class BitmapContainer : Containers
    {
        private readonly ulong[] array;
        private readonly int length;
        public const int BitShift = 6;

        public override bool Get(int index)
        {
            return (array[index >> 6] & (1Ul << (index & 0x3f))) != 0;
        }

        public override void Set(int index, bool value)
        {
            if (value)
            {
                array[index >> 6] |= (1ul << (index & 0x3f));
            }
            else
            {
                array[index >> 6] &= ~(1ul << (index & 0x3f));
            }
        }

        public override void And(Containers container)
        {
            for (int i = 0; i < array.Length; i++)
            {
                array[i] &= (container as BitmapContainer).GetArray()[i];
            }
        }

        public ulong[] GetArray() => array;

        public BitmapContainer(int length) : this(length, false) { }

        public BitmapContainer(int length, bool defaultValue)
        {
            array = new ulong[GetArrayLength(length)];
            this.length = length;
            if (defaultValue)
            {
                for (var i = 0; i < array.Length; i++)
                {
                    Set(i, defaultValue);
                }
            }
        }

        public int Size() { return length; }

        public static int GetArrayLength(int n) =>
           (int)((uint)(n - 1 + (1L << BitShift)) >> BitShift);
    }

    /// <summary>
    /// Это класс эррей контейнер, унаследованный от просто контейнера,
    /// он нужен чтобы хранить разреженные блоки в роаринг битмапе
    /// Он хранит список чисел, и просто прекрасен
    /// у него простые методы получения и задания элемента
    /// и прекрасный метод перемножения с любым контейнером.
    /// Короче, он чудесный. А еще по нему можно итерироваться,
    /// для полноты чудесности.
    /// </summary>
    public class ArrayContainer : Containers, IEnumerable<int>
    {
        private readonly List<int> array;

        public override bool Get(int index) => array.Contains(index);

        public override void Set(int index, bool value)
        {
            if (value && !Get(index))
            {
                array.Add(index);
                return;
            }

            if (!value && Get(index))
            {
                int position = array.BinarySearch(index);
                array.RemoveAt(position);
            }
        }

        public override void And(Containers container)
        {
            for (int i = 0; i < array.Count; i++)
            {
                if (Get(i) && !container.Get(i))
                {
                    Set(i, false);
                }
            }
        }

        public ArrayContainer()
        {
            array = new List<int>();
        }

        public int Size() => array.Count;

        public IEnumerator<int> GetEnumerator()
        {
            return ((IEnumerable<int>)array).GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return ((IEnumerable)array).GetEnumerator();
        }
    }

    /// <summary>
    /// Класс для, неужели, роаринг битмапов, унаследованный от абстрактных
    /// битмапов (зачем? для кого?)
    /// В нем есть список контейнеров, которые либо разреженные - арреи, либо
    /// плотные - битмапы, также размер контейнера - 2^16=65536 чисел, 
    /// запомненное значение самого большого индекса.
    /// Говорят, роаринг битмапы должны быть быстрыми
    /// на фкн вообще много чего говорят
    /// </summary>
    public class RoaringBitmap : AbstractBitmap
    {
        List<Containers> containers;
        public const int length = 65536;
        int biggestIndex = -1;

        /// <summary>
        /// Конструкторы, в том числе для создания роаринг битмапа по размеру
        /// и с заданным значением.
        /// </summary>
        public RoaringBitmap()
        {
            containers = new List<Containers>();
        }

        public RoaringBitmap(int size) : this(size, false) { }

        public RoaringBitmap(int size, bool value) : this()
        {
            if (size > 4096)
            {
                containers.Add(new BitmapContainer(size));

                for (int i = 0; i < (containers[0] as BitmapContainer).Size(); i++)
                {
                    containers[0].Set(i, value);
                }
            }
        }

        public int Size() => biggestIndex + 1;

        /// <summary>
        /// Метод получения элемента
        /// рассчитываем индекс контейнера по сдвигу, если контейнер - битмап
        /// достаем по индексу по модулю длины блока, если эррей - просто по индексу.
        /// </summary>
        /// <param name="index"></param>
        /// <returns></returns>
        public override bool Get(int index)
        {
            Containers current = containers[index >> 16];

            if (current is BitmapContainer)
            {
                return current.Get(index & 0xffff);
            }

            return current.Get(index);
        }

        public void MakeContainers(int index)
        {
            if (index / length < containers.Count)
            {
                return;
            }

            containers.Add(new ArrayContainer());
            MakeContainers(index);
        }

        /// <summary>
        /// Метод добавления и удаления элемента, сначала проверяется, достаточно
        /// ли у нас контейнеров, затем в случае добавления добавляется в зависимости от
        /// контейнера, проверяется, не надо ли сделать из эррея битмап, если был эррей
        /// в случае удаления удаляется, если был битмап, проверяется, не надо ли сделать эррей.
        /// </summary>
        /// <param name="index"></param>
        /// <param name="value"></param>
        public override void Set(int index, bool value)
        {
            MakeContainers(index);

            if (value && !Get(index))
            {
                if (index > biggestIndex)
                {
                    biggestIndex = index;
                }
                Containers current = containers[index >> 16];

                if (current is BitmapContainer)
                {
                    current.Set(index & 0xffff, true);
                    return;
                }

                //array
                current.Set(index, true);
                if ((current as ArrayContainer).Size() > 4096)
                {
                    containers[index >> 16] = MakeBitmap(current as ArrayContainer);
                }
            }

            if (!value && Get(index))
            {
                Containers current = containers[index >> 16];

                if (current is ArrayContainer)
                {
                    current.Set(index, false);
                    return;
                }

                //bitmap
                current.Set(index >> 16, false);
                if ((current as BitmapContainer).Size() <= 4096)
                {
                    containers[index >> 16] = MakeArray(current as BitmapContainer, index >> 16);
                }
            }
        }

        /// <summary>
        /// Метод для умножения массивов. В зависимости от того кого с кем мы умножаем,
        /// либо вызываются методы эррей либо метод битмапа с последующей проверкой,
        /// не надо ли его переделать в эррей.
        /// </summary>
        /// <param name="bitmap"></param>
        public override void And(AbstractBitmap bitmap)
        {
            if (!(bitmap is RoaringBitmap)) return;

            for (int i = 0; i < containers.Count; i++)
            {
                if (containers[i] is ArrayContainer)
                {
                    containers[i].And((bitmap as RoaringBitmap).containers[i]);
                    return;
                }

                if (containers[i] is ArrayContainer && (bitmap as RoaringBitmap).containers[i] is ArrayContainer)
                {
                    ((bitmap as RoaringBitmap).containers[i] as ArrayContainer).And(containers[i]);

                    containers[i] = (bitmap as RoaringBitmap).containers[i] as ArrayContainer;
                }

                if (containers[i] is BitmapContainer && (bitmap as RoaringBitmap).containers[i] is BitmapContainer)
                {
                    containers[i].And((bitmap as RoaringBitmap).containers[i]);

                    if ((containers[i] as BitmapContainer).Size() < 4096)
                    {
                        containers[i] = MakeArray(containers[i] as BitmapContainer, i);
                    }
                }
            }
        }

        /// <summary>
        /// Метод для того чтобы переделать битмап контейнер в аррей контейнер
        /// когда размер стал достаточно маленьким для эррей
        /// </summary>
        /// <param name="bitmap"></param>
        /// <param name="indexContainer"></param>
        /// <returns></returns>
        ArrayContainer MakeArray(BitmapContainer bitmap, int indexContainer)
        {
            ArrayContainer newArray = new ArrayContainer();
            for (int i = 0; i < bitmap.Size(); i++)
            {
                if (bitmap.Get(i))
                {
                    newArray.Set(1024 * indexContainer + i, true);
                }
            }

            return newArray;
        }

        /// <summary>
        /// Метод для того чтобы делать из эррей контейнера битмап контейнер когда размер переходит за 4096
        /// </summary>
        /// <param name="array"></param>
        /// <returns></returns>
        BitmapContainer MakeBitmap(ArrayContainer array)
        {
            BitmapContainer newBitmap = new BitmapContainer(length);
            foreach (int value in array)
            {
                newBitmap.Set(value & 0xffff, true);
            }

            return newBitmap;
        }
    }
}
