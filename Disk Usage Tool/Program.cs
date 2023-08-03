/*
 * Disk Usage Project
 * @author: Feng Jiang
 */

using System;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Threading.Tasks;

namespace du
{
    /*
     * the driver class that contains main method
     */
    class Program
    {
        /*
         * Based on user input from commandline, this main method will determine the mode in which
         * the program will operate. There are 3 modes, -s for sequential mode, -p for parallel mode,
         * and -b for operating both mode.
         * @param   args    contains mode of operation and the root path for parsing directories
         */
        public static void Main(string[] args)
        {
            if (args.Length == 2 && (args[0] == "-s" || args[0] == "-p" || args[0] == "-b"))
            {
                var path = args[1];
                Console.WriteLine("Directory '{0}'\n", path);
                switch (args[0])
                {
                    case "-s":
                    {
                        var sequential = new SequentialRun();
                        var seqTimer = new Stopwatch();
                        seqTimer.Start();
                        sequential.SeqCount(path);
                        seqTimer.Stop();
                        Console.WriteLine("Sequential Calculated in: {0}s", seqTimer.Elapsed.TotalSeconds);
                        var seqFolderCount = sequential.GetFolderCount();
                        var seqFileCount = sequential.GetFileCount();
                        var seqSizeCount = sequential.GetSizeCount();
                        Console.WriteLine("{0:n0} folders, {1:n0} files, {2:n0} bytes\n", seqFolderCount, seqFileCount, seqSizeCount);
                        break;
                    }
                    case "-p":
                    {
                        var parallel = new ParallelRun();
                        var parTimer = new Stopwatch();
                        parTimer.Start();
                        parallel.ParCount(path);
                        parTimer.Stop();
                        Console.WriteLine("Parallel Calculated in: {0}s", parTimer.Elapsed.TotalSeconds);
                        var parFolderCount = parallel.GetFolderCount();
                        var parFileCount = parallel.GetFileCount();
                        var parSizeCount = parallel.GetSizeCount();
                        Console.WriteLine("{0:n0} folders, {1:n0} files, {2:n0} bytes\n", parFolderCount, parFileCount, parSizeCount);

                        break;
                    }
                    case "-b":
                    {
                        var parallel = new ParallelRun();
                        var sequential = new SequentialRun();
                        
                        var parTimer = new Stopwatch();
                        parTimer.Start();
                        parallel.ParCount(path);
                        parTimer.Stop();
                        Console.WriteLine("Parallel Calculated in: {0}s", parTimer.Elapsed.TotalSeconds);
                        var parFolderCount = parallel.GetFolderCount();
                        var parFileCount = parallel.GetFileCount();
                        var parSizeCount = parallel.GetSizeCount();
                        Console.WriteLine("{0:n0} folders, {1:n0} files, {2:n0} bytes\n", parFolderCount, parFileCount, parSizeCount);
                        
                        var seqTimer = new Stopwatch();
                        seqTimer.Start();
                        sequential.SeqCount(path);
                        seqTimer.Stop();
                        Console.WriteLine("Sequential Calculated in: {0}s", seqTimer.Elapsed.TotalSeconds);
                        var seqFolderCount = sequential.GetFolderCount();
                        var seqFileCount = sequential.GetFileCount();
                        var seqSizeCount = sequential.GetSizeCount();
                        Console.WriteLine("{0:n0} folders, {1:n0} files, {2:n0} bytes\n", seqFolderCount, seqFileCount, seqSizeCount);
                        break;
                    }
                }
            }
            else
            {
                Console.WriteLine("Usage: du [-s] [-p] [-b] <path>");
                Console.WriteLine("Summarize disk usage of the set of FILES, recursively for directories.");
                Console.WriteLine("You MUST specify one of the parameters, -s, -p, or -b");
                Console.WriteLine("-s       Run in single threaded mode");
                Console.WriteLine("-p       Run in parallel mode (uses all available processors)");
                Console.WriteLine("-b       Run in both parallel and single threaded mode.");
                Console.WriteLine("         Runs parallel followed by sequential mode");
            }
        }
    }

    /*
     * The class that contains sequential, for each loop parsing methods
     */
    public class SequentialRun
    {
        private int folderCount;
        private int fileCount;
        private long sizeCount;
        
        /**
         * Constructor for sequential run
         */
        public SequentialRun()
        {
            folderCount = 0;
            fileCount = 0;
            sizeCount = 0;
        }
        
        /**
         * get the folder count
         * return:  int     the folder count
         */
        public int GetFolderCount()
        {
            return folderCount;
        }

        /**
         * get the file count
         * return:  int     the file count
         */
        public int GetFileCount()
        {
            return fileCount;
        }
        
        /**
         * get the size count
         * return:  int     the size count
         */
        public long GetSizeCount()
        {
            return sizeCount;
        }

        /**
         * the sequential method to count the file, folder, and calculate size of the files within a path
         */
        public void SeqCount(string path)
        {
            var directories = Directory.GetDirectories(path);
            foreach (var directory in directories)
            {
                try
                {
                    if (Directory.Exists(directory))
                    {
                        folderCount += 1;
                        SeqCount(directory);
                    }
                }
                catch (Exception)
                {
                    //General exception
                }
            }

            var files = Directory.GetFiles(path);
            foreach (var file in files)
            {
                try
                {
                    var fileInfo = new FileInfo(file);
                    fileInfo.Refresh();
                    fileCount += 1;
                    sizeCount += fileInfo.Length;
                }
                catch (Exception)
                {
                    //General exception
                }
            }
        }
    }
    
    /*
     * The class that contains sequential, for each loop parsing methods
     */
    public class ParallelRun
    {
        private int folderCount;
        private int fileCount;
        private long sizeCount;

        /**
         * Constructor for parallel run
         */
        public ParallelRun()
        {
            folderCount = 0;
            fileCount = 0;
            sizeCount = 0;
        }
        
        /**
         * get the folder count
         * return:  int     the folder count
         */
        public int GetFolderCount()
        {
            return folderCount;
        }
        
        /**
         * get the file count
         * return:  int     the file count
         */
        public int GetFileCount()
        {
            return fileCount;
        }

        /**
         * get the size count
         * return:  int     the size count
         */
        public long GetSizeCount()
        {
            return sizeCount;
        }

        /**
         * the parallel method to count the file, folder, and calculate size of the files within a path
         */
        public void ParCount(string path)
        {
            var directories = Directory.GetDirectories(path);
            Parallel.ForEach(directories, directory =>
            {
                try
                {
                    if (Directory.Exists(directory))
                    {
                        Interlocked.Increment(ref folderCount);
                        ParCount(directory);
                    }
                }
                catch (Exception)
                {
                    //General exception
                }
            });
            
            var files = Directory.GetFiles(path);
            Parallel.ForEach(files, file =>
            {
                try
                {
                    var fileInfo = new FileInfo(file);
                    fileInfo.Refresh();
                    Interlocked.Increment(ref fileCount);
                    Interlocked.Add(ref sizeCount, fileInfo.Length);
                }
                catch (Exception)
                {
                    //General exception
                }
            });
        }
    }
}
